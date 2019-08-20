package reasoningmodels.outputhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.bayesnet.BayesNet;
import reasoningmodels.bayesnet.INode;
import reasoningmodels.bayesnet.NodeImpl;
import reasoningmodels.knn.KNN;
import reasoningmodels.naivebayes.NaiveBayes;
import sml.WMElement;

/**
 * A factory for reasoning models. It supports construction of models through parsing information
 * from a given working memory element.
 */
public class ReasoningModelFactory {

  /**
   * Construct an instance of the appropriate model based on the information in the given working
   * memory element.
   *
   * @param wme working memory element that contains model info
   * @return an instance of a reasoning model
   */
  public static IReasoningModel createModel(WMElement wme) {
    if (wme == null) {
      throw new IllegalArgumentException("Provided WME cannot be null.");
    }

    IReasoningModel res;
    String targetClass = null;
    WMElement maybeTarget = getParamWME(wme).ConvertToIdentifier().FindByAttribute("target",0);
    if (maybeTarget != null) {
      targetClass = maybeTarget.GetValueAsString();
    }

    switch (wme.GetAttribute()) {
      case "bayes-net":
        res = new BayesNet();
        break;
      case "knn":
        res = new KNN(targetClass);
        break;
      case "naive-bayes":
        res = new NaiveBayes(targetClass);
        break;
      default:
        throw new IllegalArgumentException("Provided model type: " + wme.GetAttribute() + " is " +
                "not supported.");
    }

    if (res.hasFlatFeatures()) {
      Map<String, String[]> features = getFlatFeatures(wme);
      res.parameterizeWithFlatFeatures(features);
    }
    else {
      List<INode> nodes = getGraphicalFeatures(wme);
      res.parameterizeWithGraphicalFeatures(nodes);
    }

    return res;
  }

  /**
   * Gets the graphical features found in the WME. Should only be called if the reasoning model
   * requires graphical features.
   *
   * @param wme working memory element that contains the info
   * @return a list of nodes based on the WME info
   */
  private static List<INode> getGraphicalFeatures(WMElement wme) {
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
    return nodesInNewGraph;
  }

  /**
   * Gets the flat features found in the WME. Should only be called if the reasoning model
   * requires flat features.
   *
   * @param wme working memory element that contains the info
   * @return a mapping of features to enums based on the WME info
   */
  private static Map<String, String[]> getFlatFeatures(WMElement wme) {
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

  /**
   * Gets the enumeration associated with the feature found in the given WME.
   *
   * @param featureType the WME containing the feature info
   * @return the enumeration if it exists, null otherwise
   */
  private static String[] getFeatureEnums(WMElement featureType) {
    List<String> res = new ArrayList<>();
    if (featureType.GetValueType().equalsIgnoreCase("id")) {
      for (int i = 0; i < featureType.ConvertToIdentifier().GetNumberChildren(); i++) {
        res.add(featureType.ConvertToIdentifier().GetChild(i).GetAttribute());
      }
      return res.toArray(new String[0]);
    }
    else {
      return null;
    }
  }

  /**
   * Gets the WME labeled "parameters." Returns null if it cannot be found.
   *
   * @param wme wme that contains parameters
   * @return the parameters WME
   */
  private static WMElement getParamWME(WMElement wme) {
    return wme.ConvertToIdentifier().FindByAttribute("parameters", 0);
  }
}
