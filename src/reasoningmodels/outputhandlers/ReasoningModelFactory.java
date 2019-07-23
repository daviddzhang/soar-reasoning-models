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
      case "bayes-net":
        return createBayesNet(wme);
      case "knn":
        return createKNN(wme);
      case "naive-bayes":
        return createNaiveBayes(wme);
      default:
        throw new IllegalArgumentException("Provided model type: " + wme.GetAttribute() + " is " +
                "not supported.");
    }
  }

  private static IReasoningModel createNaiveBayes(WMElement wme) {
    Map<String, String[]> features = getClassifierFeatures(wme);
    String targetClass = getTargetClass(wme);
    return new NaiveBayes(features, targetClass);
  }

  private static IReasoningModel createKNN(WMElement wme) {
    Map<String, String[]> features = getClassifierFeatures(wme);
    String targetClass = getTargetClass(wme);
    return new KNN(features, targetClass);
  }

  private static String getTargetClass(WMElement wme) {
    WMElement params = getParamWME(wme);
    return params.ConvertToIdentifier().FindByAttribute("target",0).GetValueAsString();
  }

  private static Map<String, String[]> getClassifierFeatures(WMElement wme) {
    WMElement params = getParamWME(wme);
    WMElement features = params.ConvertToIdentifier().FindByAttribute("features", 0);

    Map<String, String[]> modelFeatures = new HashMap<>();

    for (int i = 0; i < features.ConvertToIdentifier().GetNumberChildren(); i++) {
      WMElement curFeature = features.ConvertToIdentifier().GetChild(i);
      String featureName = curFeature.GetAttribute();
      WMElement featureType = curFeature.ConvertToIdentifier().GetChild(0);
      // will be null if there are no enums (boolean or numerical)
      String[] enums = getFeatureEnums(featureType);
      modelFeatures.put(featureName, enums);
    }

    return modelFeatures;
  }

  private static IReasoningModel createBayesNet(WMElement wme) {
    WMElement params = getParamWME(wme);
    WMElement graph = params.ConvertToIdentifier().FindByAttribute("graph", 0);
    
    List<INode> nodesInNewGraph = new ArrayList<>();
    WMElement nodes = graph.ConvertToIdentifier().FindByAttribute("nodes", 0);
    int edgeCount = 0;
    for (int i = 0; i < graph.ConvertToIdentifier().GetNumberChildren(); i++) {
      if (graph.ConvertToIdentifier().GetChild(i).GetAttribute().equals("edge")) {
        edgeCount++;
      }
    }

    // creates the graph based on the edge WMEs and node attributes
    for (int i = 0; i < nodes.ConvertToIdentifier().GetNumberChildren(); i++) {
      WMElement aNode = nodes.ConvertToIdentifier().GetChild(i);
      List<String> parents = new ArrayList<>();
      for (int j = 0; j < edgeCount; j++) {
        WMElement from = graph.ConvertToIdentifier().FindByAttribute("edge", j)
                .ConvertToIdentifier().GetChild(1).ConvertToIdentifier().GetChild(0);
        WMElement to = graph
                .ConvertToIdentifier().FindByAttribute("edge", j)
                .ConvertToIdentifier().GetChild(0).ConvertToIdentifier().GetChild(0);

        if (to.GetValueAsString().equals(aNode.GetValueAsString())) {
          parents.add(from.GetAttribute());
        }
      }

      INode currentNode = new NodeImpl(aNode.GetAttribute(), parents);

      nodesInNewGraph.add(currentNode);

    }
    return new BayesNet(nodesInNewGraph);
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

  private static WMElement getParamWME(WMElement wme) {
    return wme.ConvertToIdentifier().FindByAttribute("parameters", 0);
  }
}
