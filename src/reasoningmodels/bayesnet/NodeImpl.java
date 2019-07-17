package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NodeImpl implements INode {
  private String name;
  private List<String> parents;
  private ICPT cpt;
  private IFreqTable frequencies;
  private IFreqTable relFrequencies;

  public NodeImpl(String name, List<String> parents, ICPT cpt, IFreqTable frequencies,
       IFreqTable relFrequencies) {
    this.name = name;
    this.parents = parents;
    this.cpt = cpt;
    this.frequencies = frequencies;
    this.relFrequencies = relFrequencies;
  }

  public NodeImpl(String name, List<String> parents) {
    this.name = name;
    this.parents = parents;
    this.initializeCPT();
    this.initializeFreqTable();
    this.initializeRelFreqTable();
  }

  // prints a Node
  @Override
  public String toString() {
    return this.name + ", " + this.parents + "\n" + "CPT: \n" + this.cpt.printCPT() + "\n"
            + "Frequencies: \n" + this.frequencies.printFreqTable() + "\n" + "Relative Frequencies: \n"
            + this.relFrequencies.printFreqTable();
  }

  // initializes a CPT for this Node
  @Override
  public void initializeCPT() {
    // initializes a hashmap/cpt for the current node
    Map<List<IRandomVariable>, Double> currentAssignment = new HashMap<>();

    // creates cpt for the given node
    if (this.parents.isEmpty()) {
      List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
      variableAssignment.add(new RandomVariableImpl(this.name, true));
      currentAssignment.put(variableAssignment, 0.0);

      this.cpt = new CPTImpl(currentAssignment);
    }
    else {
      int n = this.parents.size();
      int rows = (int) Math.pow(2, n);

      for (int j = 0; j < rows; j++) {
        List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
        for (int k = n - 1; k >= 0; k--) {
          if ((j / ((int) Math.pow(2, k))) % 2 == 0) {
            variableAssignment.add(new RandomVariableImpl(this.parents.get(k), true));
          }
          else {
            variableAssignment.add(new RandomVariableImpl(this.parents.get(k), false));
          }
        }
        currentAssignment.put(variableAssignment, 0.0);
      }

      this.cpt = new CPTImpl(currentAssignment);
    }
  }

  // initializes a frequency table for this node
  @Override
  public void initializeFreqTable() {
    // initializes a hashmap for the current node
    HashMap<List<IRandomVariable>, Integer> currentFrequency = new HashMap<>();

    // creates a freq table for the given node
    if (this.parents.isEmpty()) {
      List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
      variableAssignment.add(new RandomVariableImpl(this.name, true));
      currentFrequency.put(variableAssignment, 0);

      this.frequencies = new FreqTableImpl(currentFrequency);
    }
    else {
      int n = this.parents.size();
      int rows = (int) Math.pow(2, n);

      for (int j = 0; j < rows; j++) {
        List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
        for (int k = n - 1; k >= 0; k--) {
          if ((j / ((int) Math.pow(2, k))) % 2 == 0) {
            variableAssignment.add(new RandomVariableImpl(this.parents.get(k), true));
          }
          else {
            variableAssignment.add(new RandomVariableImpl(this.parents.get(k), false));
          }
        }
        currentFrequency.put(variableAssignment, 0);
      }

      this.frequencies = new FreqTableImpl(currentFrequency);
    }
  }

  // initializes a relative frequency table
  @Override
  public void initializeRelFreqTable() {
    // initializes a hashmap for the current node
    HashMap<List<IRandomVariable>, Integer> currentFrequency = new HashMap<>();

    // creates a freq table for the given node
    if (this.parents.isEmpty()) {
      List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
      variableAssignment.add(new RandomVariableImpl(this.name, true));
      currentFrequency.put(variableAssignment, 0);

      this.relFrequencies = new FreqTableImpl(currentFrequency);
    }
    else {
      int n = this.parents.size();
      int rows = (int) Math.pow(2, n);

      for (int j = 0; j < rows; j++) {
        List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
        for (int k = n - 1; k >= 0; k--) {
          if ((j / ((int) Math.pow(2, k))) % 2 == 0) {
            variableAssignment.add(new RandomVariableImpl(this.parents.get(k), true));
          }
          else {
            variableAssignment.add(new RandomVariableImpl(this.parents.get(k), false));
          }
        }
        currentFrequency.put(variableAssignment, 0);
      }
      this.relFrequencies = new FreqTableImpl(currentFrequency);
    }
  }

  // updates the frequencies in the frequency tables
  public void updateFrequency(List<IRandomVariable> trainingEx) {
    // gets the freq table for the current node
    IFreqTable currentFreqTable = this.frequencies;
    Map<List<IRandomVariable>, Integer> currentFrequency = currentFreqTable.getFreqTable();

    // updates the frequency assignment for this node
    if (this.parents.isEmpty()) {

      for (Map.Entry<List<IRandomVariable>, Integer> entry : currentFrequency.entrySet()) {
        currentFrequency.replace(entry.getKey(), entry.getValue() + 1);
      }

    }
    else {
      for (Map.Entry<List<IRandomVariable>, Integer> entry : currentFrequency.entrySet()) {
        if (trainingEx.containsAll(entry.getKey())) {
          currentFrequency.replace(entry.getKey(), entry.getValue() + 1);
        }
      }
    }
  }

  // updates the values in the relative frequency table
  public void updateRelFrequency(List<IRandomVariable> trainingEx) {
    // gets the freq table for the current node
    IFreqTable currentFreqTable = this.relFrequencies;
    Map<List<IRandomVariable>, Integer> currentFrequency = currentFreqTable.getFreqTable();

    // updates the frequency assignment for this node
    if (this.parents.isEmpty()) {
      for (Map.Entry<List<IRandomVariable>, Integer> entry : currentFrequency.entrySet()) {
        if (trainingEx.containsAll(entry.getKey())) {
          currentFrequency.replace(entry.getKey(), entry.getValue() + 1);
        }

      }
    }
    else {
      for (Map.Entry<List<IRandomVariable>, Integer> entry : currentFrequency.entrySet()) {
        List<IRandomVariable> currentKey = new ArrayList<>(entry.getKey());
        IRandomVariable currentRandVar = new RandomVariableImpl(this.name, true);
        currentKey.add(currentRandVar);

        if (trainingEx.containsAll(currentKey)) {
          entry.getKey().remove(currentRandVar);

          currentFrequency.replace(entry.getKey(), entry.getValue() + 1);
        }
      }
    }

  }

  // updates the CPT of this node
  public void updateCPT() {
    for (Map.Entry<List<IRandomVariable>, Double> entry : this.cpt.getCPT().entrySet()) {
      List<IRandomVariable> currentRow = entry.getKey();
      double frequency = this.frequencies.getFreq(currentRow);
      double relFrequency = this.relFrequencies.getFreq(currentRow);

      double prob;
      if (frequency == 0.0) {
        prob = 0.0;
      }
      else {
        prob = relFrequency / frequency;
      }
      this.cpt.getCPT().replace(currentRow, prob);
    }
  }

  // gets this node's name
  public String getNodeName() {
    return this.name;
  }

  // converts this node's CPT to an inference CPT used for joining
  public INode convertToInferenceCPT() {
    return new NodeImpl(this.name, this.parents, this.cpt.toInferenceCPT(this.name), this.frequencies,
            this.relFrequencies);
  }

  // does this node have the given parent?
  public boolean hasParent(String parentNode) {
    return this.parents.contains(parentNode);
  }

  // does this node have no parents?
  public boolean hasNoParents() {
    return this.parents.isEmpty();
  }

  // joins the CPT of this node with another CPT using the given join variable
  // name
  public ICPT join(ICPT other, String joinVar) {
    Map<List<IRandomVariable>, Double> thisCPT = this.cpt.getCPT();
    Map<List<IRandomVariable>, Double> otherCPT = other.getCPT();
    Map<List<IRandomVariable>, Double> newCPT = new HashMap<>();

    Map<List<IRandomVariable>, Double> smaller = otherCPT;
    Map<List<IRandomVariable>, Double> bigger = thisCPT;

    if (thisCPT.entrySet().size() < otherCPT.entrySet().size()) {
      smaller = thisCPT;
      bigger = otherCPT;
    }
    // iterate through the smaller CPT to look into the larger CPT for join
    // variables
    for (Map.Entry<List<IRandomVariable>, Double> smallEntry : smaller.entrySet()) {
      // retrieve the random variable based on the join variable's name
      IRandomVariable join = null;
      for (int i = 0; i < smallEntry.getKey().size(); i++) {
        List<IRandomVariable> currentAssignment = smallEntry.getKey();
        if (currentAssignment.get(i).getName().equals(joinVar)) {
          join = currentAssignment.get(i);
        }
      }
      // look through the larger CPT for common variables
      for (Map.Entry<List<IRandomVariable>, Double> bigEntry : bigger.entrySet()) {
        if (bigEntry.getKey().contains(join)) {
          List<IRandomVariable> newAssignment = new ArrayList<>(
                  bigEntry.getKey());
          newAssignment.addAll(smallEntry.getKey());
          List<IRandomVariable> noDups = BayesNetUtils.removeDuplicates(newAssignment);
          newCPT.put(noDups, smallEntry.getValue() * bigEntry.getValue());
        }
      }
    }

    return new CPTImpl(newCPT);
  }

  // returns a reference since it will be used in tandem with the BayesNet class, which should be
  // able to mutate the CPT.
  @Override
  public ICPT getCPT() {
    return this.cpt;
  }
}
