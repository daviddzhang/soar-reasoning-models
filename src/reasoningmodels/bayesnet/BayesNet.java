package reasoningmodels.bayesnet;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public class BayesNet implements IReasoningModel {
  private List<INode> nodes;

  public BayesNet(List<INode> nodes) {
    this.nodes = nodes;
  }

  private void updateCPTs(List<IRandomVariable> trainingEx) {
    // for each node, update the CPT
    for (INode node : nodes) {
      // current node
      node.updateCPT(trainingEx);
    }

  }

  @Override
  public String toString() {
    return this.nodes.toString();
  }

  private List<String> toNodeNames() {
    List<String> nodeNames = new ArrayList<String>();

    for (INode node : this.nodes) {
      nodeNames.add(node.getNodeName());
    }

    return nodeNames;
  }

  private INode getNoParentNode() {
    INode result = null;
    for (INode node : this.nodes) {
      if (node.getParents().isEmpty()) {
        result = node;
      }
    }
    if (result != null) {
      return result;
    }
    else {
      throw new RuntimeException("No nodes with no parents");
    }
  }

  @Override
  public void train(IEntry entry) {
    List<IRandomVariable> trainingVars =
            RandomVariableImpl.featureListToVarList(entry.getFeatures());

    this.updateCPTs(trainingVars);
  }

  @Override
  public String queryWithParams(IEntry queryEntry, Map<String, Object> queryParams) {
    if (queryEntry == null || queryParams == null) {
      throw new IllegalArgumentException("Cannot query with null arguments.");
    }

    Object param = queryParams.get("target-vars");
    if (param == null) {
      throw new IllegalArgumentException("Must provide smoothing when querying a Naive Bayes " +
              "model.");
    }

    List<IFeature> targetFeatures = new ArrayList<>();
    List<Pair<String, String>> targetVars = (List<Pair<String, String>>)param;
    for (Pair<String, String> pair : targetVars) {
      String varName = pair.getKey();
      try {
        double occur = Double.parseDouble(pair.getValue());
        targetFeatures.add(new BooleanFeature(varName, occur));
      } catch (Exception e) {
        throw new IllegalArgumentException("Must provide a double for boolean value of target " +
                "variables.");
      }
    }

    IEntry targetEntry = new EntryImpl(targetFeatures);

    List<IRandomVariable> queryVars =
            RandomVariableImpl.featureListToVarList(targetEntry.getFeatures());
    List<IRandomVariable> evidenceVars =
            RandomVariableImpl.featureListToVarList(queryEntry.getFeatures());

    return String.valueOf(this.enumeration(queryVars, evidenceVars));
  }

  private double enumeration(List<IRandomVariable> queryVars, List<IRandomVariable> evidenceVars) {
    List<String> queryVarNames = RandomVariableImpl.listOfVarsToListOfNames(queryVars);
    List<String> evidenceVarNames = RandomVariableImpl.listOfVarsToListOfNames(evidenceVars);

    // gets a list with the evidence and the query variable
    List<String> queryList = new ArrayList<>(evidenceVarNames);
    queryList.addAll(queryVarNames);

    // gets a list of all the variable names in the graph
    List<String> graphNodeNames = this.toNodeNames();

    List<INode> graphNodes = new ArrayList<>(this.nodes);
    int numNodes = this.nodes.size();

    // gets a node in the graph with no parents and sets it as the starting CPT,
    // joining necessary variables
    INode startingNode = this.getNoParentNode();
    ICPT joinedCPT = startingNode.convertToInferenceCPT().getCPT();
    for (int i = 0; i < graphNodes.size(); i++) {
      if (graphNodes.get(i).hasParent(startingNode.getNodeName())) {
        // join their CPTs
        joinedCPT = graphNodes.get(i).convertToInferenceCPT().join(joinedCPT, startingNode.getNodeName());

        graphNodes.remove(graphNodes.get(i));
      }
    }
    graphNodeNames.remove(startingNode.getNodeName());

    // finishes joining on the rest of the variables
    List<String> cptVariables = joinedCPT.variablesInCPT();
    while (cptVariables.size() < numNodes) {
      // goes through the nodes that aren't yet joined and in the current joined
      // cpt
      for (int j = 0; j < cptVariables.size(); j++) {
        String nodeName = cptVariables.get(j);
        // if still not joined yet
        if (graphNodeNames.contains(nodeName)) {
          // go through the graph and find nodes that have the selected node as its
          // parent
          for (int k = 0; k < graphNodes.size(); k++) {
            if (graphNodes.get(k).hasParent(nodeName)) {
              // join their CPTs
              joinedCPT = graphNodes.get(k).convertToInferenceCPT().join(joinedCPT, nodeName);
            }
          }
          graphNodeNames.remove(nodeName);
        }
      }
      cptVariables = joinedCPT.variablesInCPT();
    }

    joinedCPT = joinedCPT.eliminateExcept(queryList);
    joinedCPT.normalize();

    List<IRandomVariable> allQueryVars = new ArrayList<>(queryVars);
    allQueryVars.addAll(evidenceVars);

    return joinedCPT.getQueryVar(allQueryVars);
  }
}
