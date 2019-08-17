package reasoningmodels.outputhandlers;


import org.apache.commons.math3.util.Pair;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.CategoricalFeature;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;
import reasoningmodels.classifiers.NumericalFeature;
import sml.Agent;
import sml.Agent.OutputEventInterface;
import sml.WMElement;

public class ReasoningModels {

  private static List<IReasoningModel> models = new ArrayList<>();
  private static Serializable userData = null;

  public static String printModels() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < models.size(); i++) {
      stringBuilder.append("Model: ").append(i).append("\n");
      stringBuilder.append(models.get(i).toString()).append("\n");
    }

    return stringBuilder.toString();
  }

  public static void serialize(String filePath) throws IOException {
    List<Object> toSave = new ArrayList<>();
    toSave.add(models);
    if (userData != null) {
     toSave.add(userData);
    }

    try {
      FileOutputStream fos = new FileOutputStream(filePath);
      ObjectOutputStream out = new ObjectOutputStream(fos);
      out.writeObject(toSave);

      out.flush();
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Serializable deserialize(String filePath) throws IOException {
    try {
      FileInputStream fis = new FileInputStream(filePath);
      ObjectInputStream in = new ObjectInputStream(fis);
      List<Object> readModels = (List<Object>) in.readObject();
      List<IReasoningModel> models = (List<IReasoningModel>) readModels.get(0);
      in.close();

      ReasoningModels.models.clear();
      ReasoningModels.models.addAll(models);

      try {
        ReasoningModels.userData = (Serializable) readModels.get(1);
        return (Serializable)readModels.get(1);
      } catch (Exception e) {
        return null;
      }
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void addReasoningOutputHandlersToAgent(Agent agent, String createWMEName,
                                                       String trainWMEName, String queryWMEName) {
    agent.AddOutputHandler(createWMEName, createModel, userData);
    agent.AddOutputHandler(trainWMEName, trainModel, userData);
    agent.AddOutputHandler(queryWMEName, queryModel, userData);
  }

  public static void addReasoningOutputHandlersToAgent(Agent agent,
                                                       Serializable userData, String createWMEName,
                                                       String trainWMEName, String queryWMEName) {
    ReasoningModels.userData = userData;
    agent.AddOutputHandler(createWMEName, createModel, userData);
    agent.AddOutputHandler(trainWMEName, trainModel, userData);
    agent.AddOutputHandler(queryWMEName, queryModel, userData);
  }

  private static final OutputEventInterface createModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      WMElement modelWME = pWmeAdded.ConvertToIdentifier().FindByAttribute("model", 0);
      IReasoningModel newModel =
              ReasoningModelFactory.createModel(modelWME.ConvertToIdentifier().GetChild(0));

      models.add(newModel);
      pWmeAdded.ConvertToIdentifier().CreateIntWME("id", models.size() - 1);
    }
  };

  private static final OutputEventInterface trainModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      int modelID = Integer
              .parseInt(pWmeAdded.ConvertToIdentifier().FindByAttribute("id", 0).GetValueAsString());
      IReasoningModel modelToTrain = models.get(modelID);

      WMElement features = pWmeAdded.ConvertToIdentifier().FindByAttribute("train", 0);

      List<IFeature> trainingFeatures = ReasoningModels.parseFeatures(features);

      IEntry trainingExample = new EntryImpl(trainingFeatures);
      modelToTrain.train(trainingExample);
    }
  };

  private static final OutputEventInterface queryModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      int modelID = Integer
              .parseInt(pWmeAdded.ConvertToIdentifier().FindByAttribute("id", 0).GetValueAsString());
      IReasoningModel modelToQuery = models.get(modelID);

      WMElement queryWME = pWmeAdded.ConvertToIdentifier().FindByAttribute("query", 0);
      WMElement features = queryWME.ConvertToIdentifier().FindByAttribute("features", 0);
      List<IFeature> queryFeatures = ReasoningModels.parseFeatures(features);

      IEntry queryEntry = new EntryImpl(queryFeatures);

      WMElement params = queryWME.ConvertToIdentifier().FindByAttribute("parameters", 0);

      Map<String, Object> queryParams = ReasoningModels.handleParams(params);

      String res  = modelToQuery.queryWithParams(queryEntry, queryParams);

      try {
        double prob = Double.parseDouble(res);
        pWmeAdded.ConvertToIdentifier().CreateFloatWME("result", prob);
      } catch (Exception e) {
        pWmeAdded.ConvertToIdentifier().CreateStringWME("result", res);
      }
    }
  };

  private static Map<String, Object> handleParams(WMElement params) {
    Map<String, Object> res = new HashMap<>();
    int i = 0;
    for (; i < params.ConvertToIdentifier().GetNumberChildren(); i++) {
      WMElement curParam = params.ConvertToIdentifier().GetChild(i);
      String curParamName = curParam.GetAttribute();
      if (!curParam.GetValueType().equalsIgnoreCase("id")) {
        res.put(curParamName, curParam.GetValueAsString());
      }
      else {
        List<Pair<String, String>> extraFeatures = new ArrayList<>();
        for (int j = 0; j < curParam.ConvertToIdentifier().GetNumberChildren(); j++) {
          WMElement curFeature = curParam.ConvertToIdentifier().GetChild(j);
          extraFeatures.add(new Pair<>(curFeature.GetAttribute(), curFeature.GetValueAsString()));
        }

        res.put(curParamName, extraFeatures);
      }
    }
    return res;
  }

  private static List<IFeature> parseFeatures(WMElement features) {
    List<IFeature> trainingFeatures = new ArrayList<>();
    for (int i = 0; i < features.ConvertToIdentifier().GetNumberChildren(); i++) {
      IFeature res;
      WMElement curFeatureType = features.ConvertToIdentifier().GetChild(i);
      WMElement curFeature = curFeatureType.ConvertToIdentifier().GetChild(0);
      String featureName = curFeature.GetAttribute();
      String featureVal = curFeature.GetValueAsString();
      double featureValNumerical = -1.0;
      try {
        featureValNumerical = Double.parseDouble(featureVal);
      } catch (Exception e) {
        if (featureVal.equalsIgnoreCase("true")) {
          featureValNumerical = 1.0;
        } else if (featureVal.equalsIgnoreCase("false")) {
          featureValNumerical = 0.0;
        }
      }

      switch (curFeatureType.GetAttribute()) {
        case "boolean":
          res = new BooleanFeature(featureName, featureValNumerical);
          break;
        case "numerical":
          res = new NumericalFeature(featureName, featureValNumerical);
          break;
        case "categorical":
          res = new CategoricalFeature(featureName, featureVal);
          break;
        default:
          throw new IllegalArgumentException("Given feature type is not supported.");
      }

      trainingFeatures.add(res);
    }
    return trainingFeatures;
  }
}
