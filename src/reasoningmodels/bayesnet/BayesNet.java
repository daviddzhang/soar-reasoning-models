package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.List;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.IEntry;

public class BayesNet implements IReasoningModel {
  private List<INode> nodes;
  private double queryResult;
  private int id = -1;

  public BayesNet(List<INode> nodes, double queryResult) {
    this.nodes = nodes;
    this.queryResult = queryResult;
  }

  public BayesNet(List<INode> nodes) {
    this(nodes, -1.0);
  }

  public BayesNet() {
    this(new ArrayList<>(), -1.0);
  }

  public void updateFrequencies(List<IRandomVariable> trainingEx) {
    // for each node, update frequency table
    for (int i = 0; i < nodes.size(); i++) {
      // current node
      nodes.get(i).updateFrequency(trainingEx);
    }
  }

  public void updateRelFrequencies(List<IRandomVariable> trainingEx) {
    // for each node, update frequency table
    for (int i = 0; i < nodes.size(); i++) {
      // current node
      nodes.get(i).updateRelFrequency(trainingEx);
    }
  }

  public void updateCPTs() {
    // for each node, update the CPT
    for (int i = 0; i < nodes.size(); i++) {
      // current node
      nodes.get(i).updateCPT();
    }

  }

  public String toString() {
    return this.nodes.toString();
  }

  public List<String> toNodeNames() {
    List<String> nodeNames = new ArrayList<String>();

    for (int i = 0; i < this.nodes.size(); i++) {
      nodeNames.add(this.nodes.get(i).getNodeName());
    }

    return nodeNames;
  }

  public INode getNoParentNode() {
    INode result = null;
    for (int i = 0; i < this.nodes.size(); i++) {
      if (this.nodes.get(i).hasNoParents()) {
        result = this.nodes.get(i);
      }
    }
    if (result != null) {
      return result;
    }
    else {
      throw new RuntimeException("No nodes with no parents");
    }
  }

  public List<INode> getNodes() {
    List<INode> res = new ArrayList<>();
    for (INode node : this.nodes) {
      res.add(node);
    }
    return res;
  }

  public double getResult() {
    return this.queryResult;
  }

  public void setResult(double res) {
    this.queryResult = res;
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
    //TODO: fill this in
  }

  @Override
  public void query(IEntry query) {

  }

  @Override
  public void query(IEntry query, int k) throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Bayes nets do not need a k");
  }
}
