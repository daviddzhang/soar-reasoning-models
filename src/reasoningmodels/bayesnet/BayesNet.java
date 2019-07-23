package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.List;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.IEntry;

public class BayesNet implements IReasoningModel {
  private List<INode> nodes;

  public BayesNet(List<INode> nodes) {
    this.nodes = nodes;
  }

  private void updateFrequencies(List<IRandomVariable> trainingEx) {
    // for each node, update frequency table
    for (INode node : nodes) {
      // current node
      node.updateFrequency(trainingEx);
    }
  }

  private void updateRelFrequencies(List<IRandomVariable> trainingEx) {
    // for each node, update frequency table
    for (INode node : nodes) {
      // current node
      node.updateRelFrequency(trainingEx);
    }
  }

  private void updateCPTs() {
    // for each node, update the CPT
    for (INode node : nodes) {
      // current node
      node.updateCPT();
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
      if (node.hasNoParents()) {
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
  public void addNode(INode node) {
    this.nodes.add(node);
  }

  @Override
  public void addFeature(String feature, String[] enumerations) {
    throw new UnsupportedOperationException("Bayes nets do not have features.");
  }

  @Override
  public void train(IEntry entry) {
    List<IRandomVariable> trainingVars =
            RandomVariableImpl.featureListToVarList(entry.getFeatures());

    this.updateFrequencies(trainingVars);
    this.updateRelFrequencies(trainingVars);
    this.updateCPTs();
  }

  @Override
  public String query(IEntry query) {
    throw new UnsupportedOperationException("Bayes nets can only return probabilities.");
  }

  @Override
  public String query(IEntry query, double smoothing) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Bayes nets do not need smoothing.");
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

    System.out.println(joinedCPT.printCPT());

    List<IRandomVariable> queryListVars = BayesNetUtils.stringListToVar(queryList);
    return joinedCPT.getQueryVar(queryListVars);
  }

  @Override
  public String query(IEntry query, int k) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Bayes nets do not need a k");
  }

  @Override
  public double queryProbability(IEntry query, IEntry evidence) {
    if (query.getFeatures().size() == 0) {
      throw new IllegalArgumentException("Query must have at least one variable.");
    }
    List<IRandomVariable> queryVars =
            RandomVariableImpl.featureListToVarList(query.getFeatures());
    List<IRandomVariable> evidenceVars =
            RandomVariableImpl.featureListToVarList(evidence.getFeatures());
    return this.enumeration(queryVars, evidenceVars);
  }
}
