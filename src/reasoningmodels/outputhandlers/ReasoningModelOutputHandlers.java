package reasoningmodels.outputhandlers;


import java.util.ArrayList;
import java.util.List;

import reasoningmodels.IReasoningModel;
import reasoningmodels.bayesnet.BayesNet;
import reasoningmodels.bayesnet.IFreqTable;
import reasoningmodels.bayesnet.INode;
import reasoningmodels.bayesnet.IRandomVariable;
import reasoningmodels.bayesnet.RandomVariableImpl;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.CategoricalFeature;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;
import reasoningmodels.classifiers.NumericalFeature;
import sml.Agent;
import sml.Agent.OutputEventInterface;
import sml.Identifier;
import sml.Kernel;
import sml.WMElement;

public class ReasoningModelOutputHandlers {

  public static final OutputEventInterface createModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      List<IReasoningModel> models = (List<IReasoningModel>) data;
      IReasoningModel newModel = null;
      for (int i = 0; i < pWmeAdded.ConvertToIdentifier().GetNumberChildren(); i++) {
        if (!pWmeAdded.ConvertToIdentifier().GetChild(i).GetAttribute().equalsIgnoreCase("name")) {
          newModel = ReasoningModelFactory.createModel(pWmeAdded.ConvertToIdentifier().GetChild(i));
          break;
        }
      }

      models.add(newModel);
      pWmeAdded.ConvertToIdentifier().CreateIntWME("id", models.size() - 1);
    }
  };

  public static final OutputEventInterface trainModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      List<IReasoningModel> models = (List<IReasoningModel>) data;

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
      List<IReasoningModel> models = (List<IReasoningModel>) data;

      int graphID = Integer
              .parseInt(pWmeAdded.ConvertToIdentifier().FindByAttribute("id", 0).GetValueAsString());
      IReasoningModel modelToQuery = models.get(graphID);

      WMElement queryWME = pWmeAdded.ConvertToIdentifier().FindByAttribute("query", 0);
      WMElement features = queryWME.ConvertToIdentifier().FindByAttribute("features", 0);
      List<IFeature> queryFeatures = ReasoningModelOutputHandlers.parseFeatures(features);

      IEntry queryEntry = new EntryImpl(queryFeatures);

      if (queryWME.ConvertToIdentifier().FindByAttribute("k", 0) != null) {
        int k = -1;
        try {
          k = Integer.parseInt(queryWME.ConvertToIdentifier().FindByAttribute("k", 0)
                  .GetValueAsString());
        } catch (Exception e) {
          throw new IllegalArgumentException("Given k must be an integer.");
        }
        String res = modelToQuery.query(queryEntry, k);
        pWmeAdded.ConvertToIdentifier().CreateStringWME("result", res);
      }
      else if (queryWME.ConvertToIdentifier().FindByAttribute("evidence", 0) != null) {
        WMElement evidenceWME = queryWME.ConvertToIdentifier().FindByAttribute("evidence", 0);
        List<IFeature> evidenceVars = new ArrayList<>();
        for (int j = 0; j < evidenceWME.ConvertToIdentifier().GetNumberChildren(); j++) {
          WMElement curVar = evidenceWME.ConvertToIdentifier().GetChild(j);
          double booleanVal = -1.0;
          try {
            booleanVal = Double.parseDouble(curVar.GetValueAsString());
          } catch (Exception e) {
            throw new IllegalArgumentException("Evidence must be boolean features.");
          }
          evidenceVars.add(new BooleanFeature(curVar.GetAttribute(), booleanVal));
        }
        double res = modelToQuery.queryProbability(queryEntry, new EntryImpl(evidenceVars));
        pWmeAdded.ConvertToIdentifier().CreateFloatWME("result", res);
      }
      else {
        String res = modelToQuery.query(queryEntry);
        pWmeAdded.ConvertToIdentifier().CreateStringWME("result", res);
      }
    }
  };

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
    return  trainingFeatures;
  }
}
