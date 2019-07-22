package reasoningmodels.outputhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.bayesnet.BayesNet;
import reasoningmodels.bayesnet.BayesNetUtils;
import reasoningmodels.bayesnet.INode;
import reasoningmodels.bayesnet.NodeImpl;
import reasoningmodels.knn.KNN;
import reasoningmodels.naivebayes.NaiveBayes;
import sml.WMElement;

public class ReasoningModelFactory {
  public static IReasoningModel createModel(WMElement wme) {
    switch (wme.GetAttribute()) {
      case "graph":
        return createGraphModel(wme);
      case "classifier":
        return createClassifierModel(wme);
      default:
        throw new IllegalArgumentException("Provided model type: " + wme.GetAttribute() + " is " +
                "not supported.");
    }
  }

  private static IReasoningModel createClassifierModel(WMElement wme) {
    WMElement features = wme.ConvertToIdentifier().FindByAttribute("features", 0);
    WMElement target = wme.ConvertToIdentifier().FindByAttribute("target", 0);
    String targetClass = null;
    if (target != null) {
      targetClass = target.ConvertToIdentifier().GetChild(0).GetAttribute();
    }

    Map<String, String[]> modelFeatures = new HashMap<>();

    for (int i = 0; i < features.ConvertToIdentifier().GetNumberChildren(); i++) {
      WMElement curFeature = features.ConvertToIdentifier().GetChild(i);
      String featureName = curFeature.GetAttribute();
      WMElement featureType = curFeature.ConvertToIdentifier().GetChild(0);
      // will be null if there are no enums (boolean or numerical)
      String[] enums = getFeatureEnums(featureType);
      modelFeatures.put(featureName, enums);
    }

    if (targetClass != null) {
      return new NaiveBayes(modelFeatures, targetClass);
    }
    else {
      return new KNN(modelFeatures);
    }
  }

  private static String[] getFeatureEnums(WMElement featureType) {
    List<String> res = new ArrayList<>();
    switch (featureType.GetAttribute()) {
      case "categorical":
        for (int i = 0; i < featureType.ConvertToIdentifier().GetNumberChildren(); i++) {
          res.add(featureType.ConvertToIdentifier().GetChild(i).GetAttribute());
        }
        return res.toArray(new String[0]);
      case "boolean":
      case "numerical":
        return null;
      default:
        throw new IllegalArgumentException("Please use a valid feature attribute.");
    }
  }

  private static IReasoningModel createGraphModel(WMElement wme) {
    List<INode> nodesInNewGraph = new ArrayList<>();
    WMElement nodes = wme.ConvertToIdentifier().FindByAttribute("nodes", 0);
    int edgeCount = 0;
    for (int i = 0; i < wme.ConvertToIdentifier().GetNumberChildren(); i++) {
      if (wme.ConvertToIdentifier().GetChild(i).GetAttribute().equals("edge")) {
        edgeCount++;
      }
    }

    // creates the graph based on the edge WMEs and node attributes
    for (int i = 0; i < nodes.ConvertToIdentifier().GetNumberChildren(); i++) {
      WMElement aNode = nodes.ConvertToIdentifier().GetChild(i);
      List<String> parents = new ArrayList<>();
      for (int j = 0; j < edgeCount; j++) {
        WMElement from = wme.ConvertToIdentifier().FindByAttribute("edge", j)
                .ConvertToIdentifier().GetChild(1).ConvertToIdentifier().GetChild(0);
        WMElement to = wme
                .ConvertToIdentifier().FindByAttribute("edge", j)
                .ConvertToIdentifier().GetChild(0).ConvertToIdentifier().GetChild(0);

        if (to.GetValueAsString().equals(aNode.GetValueAsString())) {
          parents.add(from.GetAttribute());
        }
      }

      INode currentNode = new NodeImpl(aNode.GetAttribute(), parents);
      ;
      nodesInNewGraph.add(currentNode);

    }
    return new BayesNet(nodesInNewGraph);
  }
}
