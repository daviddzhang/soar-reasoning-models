package reasoningmodels.outputhandlers;


import java.util.ArrayList;
import java.util.List;

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

  public static final OutputEventInterface createModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      WMElement modelWME = pWmeAdded.ConvertToIdentifier().FindByAttribute("model", 0);
      IReasoningModel newModel =
              ReasoningModelFactory.createModel(modelWME.ConvertToIdentifier().GetChild(0));

      models.add(newModel);
      pWmeAdded.ConvertToIdentifier().CreateIntWME("id", models.size() - 1);
    }
  };

  public static final OutputEventInterface trainModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {

      int graphID = Integer
              .parseInt(pWmeAdded.ConvertToIdentifier().FindByAttribute("id", 0).GetValueAsString());
      IReasoningModel modelToTrain = models.get(graphID);

      WMElement features = pWmeAdded.ConvertToIdentifier().FindByAttribute("train", 0);

      List<IFeature> trainingFeatures = ReasoningModelOutputHandlers.parseFeatures(features);

      IEntry trainingExample = new EntryImpl(trainingFeatures);

      modelToTrain.train(trainingExample);
    }
  };

  public static final OutputEventInterface queryModel = new OutputEventInterface() {
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

      ReasoningModelOutputHandlers.handleParams(params, modelToQuery, queryEntry, pWmeAdded);
    }
  };

  private static void handleParams(WMElement params, IReasoningModel modelToQuery,
                                   IEntry queryEntry, WMElement output) {
    String res = null;
    for (int i = 0; i < params.ConvertToIdentifier().GetNumberChildren(); i++) {
      String curParam = params.ConvertToIdentifier().GetChild(i).GetAttribute();
      switch (curParam) {
        case "k":
          int k = -1;
          try {
            k = Integer.parseInt(params.ConvertToIdentifier().GetChild(i).GetValueAsString());
          } catch (Exception e) {
            throw new IllegalArgumentException("Given k must be an integer.");
          }
          res = modelToQuery.query(queryEntry, k);
          output.ConvertToIdentifier().CreateStringWME("result", res);
          break;
        case "target-vars":
          WMElement varWME = params.ConvertToIdentifier().GetChild(i);
          List<IFeature> targetVars = new ArrayList<>();
          for (int j = 0; j < varWME.ConvertToIdentifier().GetNumberChildren(); j++) {
            WMElement curVar = varWME.ConvertToIdentifier().GetChild(j);
            double booleanVal = -1.0;
            try {
              booleanVal = Double.parseDouble(curVar.GetValueAsString());
            } catch (Exception e) {
              throw new IllegalArgumentException("Target variables must be boolean features.");
            }
            targetVars.add(new BooleanFeature(curVar.GetAttribute(), booleanVal));
          }
          double prob = modelToQuery.queryProbability(new EntryImpl(targetVars), queryEntry);
          output.ConvertToIdentifier().CreateFloatWME("result", prob);
          break;
        case "smoothing":
          double smoothing = -1.0;
          try {
            smoothing =
                    Double.parseDouble(params.ConvertToIdentifier().GetChild(i).GetValueAsString());
            if (smoothing <= 0) {
              throw new IllegalArgumentException("Given smoothing must be positive.");
            }
          } catch (Exception e) {
            throw new IllegalArgumentException("Given smoothing must be a positive double");
          }
          res = modelToQuery.query(queryEntry, smoothing);
          output.ConvertToIdentifier().CreateStringWME("result", res);
          break;
          default:
            throw new IllegalArgumentException("Supplied parameter is not supported.");
      }
    }
  }

  private static List<IFeature> parseFeatures(WMElement features) {
    List<IFeature> trainingFeatures = new ArrayList<>();
    for (int i = 0; i < features.ConvertToIdentifier().GetNumberChildren(); i++) {
      IFeature res = null;
      WMElement curFeatureType = features.ConvertToIdentifier().GetChild(i);
      WMElement curFeature = curFeatureType.ConvertToIdentifier().GetChild(0);
      String featureName = curFeature.GetAttribute();
      String featureVal = curFeature.GetValueAsString();
      double featureValNumerical = -1.0;
      try {
        featureValNumerical = Double.parseDouble(featureVal);
      } catch (Exception e) {

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
