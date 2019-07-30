package reasoningmodels.outputhandlers;


import org.apache.commons.math3.util.Pair;

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

public class ReasoningModelOutputHandlers {

  private static List<IReasoningModel> models = new ArrayList<>();

  public static List<IReasoningModel> getModels() {
    return new ArrayList<>(ReasoningModelOutputHandlers.models);
  }

  public static void addReasoningOutputHandlersToAgent(Agent agent, String createWMEName,
                                                       String trainWMEName, String queryWMEName) {
    agent.AddOutputHandler(createWMEName, createModel, models);
    agent.AddOutputHandler(trainWMEName, trainModel, models);
    agent.AddOutputHandler(queryWMEName, queryModel, models);
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

      List<IFeature> trainingFeatures = ReasoningModelOutputHandlers.parseFeatures(features);

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
      List<IFeature> queryFeatures = ReasoningModelOutputHandlers.parseFeatures(features);

      IEntry queryEntry = new EntryImpl(queryFeatures);

      WMElement params = queryWME.ConvertToIdentifier().FindByAttribute("parameters", 0);

      Map<String, Object> queryParams = ReasoningModelOutputHandlers.handleParams(params);

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
