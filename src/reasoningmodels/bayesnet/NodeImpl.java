package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is an implementation of the INode interface. It represents the CPT as an ICPT and
 * keeps track of what the probabilities should be with the usage of frequency tables in the form
 * of IFreqTables for frequencies and relative frequencies. Specifics of this implementation are
 * further documented below.
 */
public class NodeImpl implements INode {
  private final String name;
  private final List<String> parents;
  private ICPT cpt;
  private IFreqTable frequencies;
  private IFreqTable relFrequencies;

  /**
   * Constructs an instance of a NodeImpl. Required the name, parents, CPT, and both frequency
   * tables to be constructed. Most likely used when creating copies, or for similar uses.
   *
   * @param name of the node
   * @param parents list of parents
   * @param cpt holds the node's CPT
   * @param frequencies holds the node's parents' frequencies
   * @param relFrequencies holds the node's parents' relative frequencies
   * @throws IllegalArgumentException for null parameters
   */
  public NodeImpl(String name, List<String> parents, ICPT cpt, IFreqTable frequencies,
       IFreqTable relFrequencies) {
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null.");
    }
    else if (parents == null) {
      throw new IllegalArgumentException("Parents cannot be null.");
    }
    else if (cpt == null) {
      throw new IllegalArgumentException("CPT cannot be null.");
    }
    else if (frequencies == null) {
      throw new IllegalArgumentException("Frequencies cannot be null.");
    }
    else if (relFrequencies == null) {
      throw new IllegalArgumentException("Relative frequencies cannot be null.");
    }
    this.name = name;
    this.parents = parents;
    this.cpt = cpt;
    this.frequencies = frequencies;
    this.relFrequencies = relFrequencies;
  }

  /**
   * Constructs an instance of NodeImpl. Required name and parents and initializes CPT and
   * frequency tables based on parents.
   *
   * @param name of the node
   * @param parents list of the parents
   * @throws IllegalArgumentException for null parameters
   */
  public NodeImpl(String name, List<String> parents) {
    this(name, parents, new CPTImpl(new HashMap<>()), new FreqTableImpl(new HashMap<>()),
            new FreqTableImpl(new HashMap<>()));
    this.cpt = this.initializeCPT();
    this.frequencies = this.initializeAFreqTable();
    this.relFrequencies = this.initializeAFreqTable();
  }

  /**
   * Formats the node text as the following:
   * Name, [parents]
   * CPT:
   * ICPT.printCPT()
   * Frequencies:
   * IFreqTable.printFreqTable()
   * Relative Frequencies:
   * IFreqTable.printFreqTable()
   */
  @Override
  public String toString() {
    return this.name + ", " + this.parents + "\n" + "CPT: \n" + this.cpt.printCPT() + "\n"
            + "Frequencies: \n" + this.frequencies.printFreqTable() + "\n" + "Relative Frequencies: \n"
            + this.relFrequencies.printFreqTable();
  }

  /**
   * Initializes and creates an empty CPT with the structure based on this node's name and parents.
   *
   * @return the cpt
   */
  private ICPT initializeCPT() {
    // initializes a hashmap/cpt for the current node
    Map<List<IRandomVariable>, Double> currentAssignment = new HashMap<>();

    // creates cpt for the given node
    if (this.parents.isEmpty()) {
      List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
      variableAssignment.add(new RandomVariableImpl(this.name, true));
      currentAssignment.put(variableAssignment, 0.0);

      return new CPTImpl(currentAssignment);
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

      return new CPTImpl(currentAssignment);
    }
  }

  /**
   * Initializes and creates an empty frequency table with the structure based on this node's name
   * and parents.
   *
   * @return a frequency table
   */
  private IFreqTable initializeAFreqTable() {
    // initializes a hashmap for the current node
    HashMap<List<IRandomVariable>, Integer> currentFrequency = new HashMap<>();

    // creates a freq table for the given node
    if (this.parents.isEmpty()) {
      List<IRandomVariable> variableAssignment = new ArrayList<IRandomVariable>();
      variableAssignment.add(new RandomVariableImpl(this.name, true));
      currentFrequency.put(variableAssignment, 0);

      return new FreqTableImpl(currentFrequency);
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

      return new FreqTableImpl(currentFrequency);
    }
  }

  /**
   * Updates this nodes frequency table with the given training example.
   *
   * @param trainingEx training example
   */
  private void updateFrequency(List<IRandomVariable> trainingEx) {
    Map<List<IRandomVariable>, Integer> currentFrequency = this.frequencies.getFreqTable();

    // updates the frequency assignment for this node
    if (this.parents.isEmpty()) {

      for (Map.Entry<List<IRandomVariable>, Integer> entry : currentFrequency.entrySet()) {
        this.frequencies.replace(entry.getKey(), entry.getValue() + 1);
      }

    }
    else {
      for (Map.Entry<List<IRandomVariable>, Integer> entry : currentFrequency.entrySet()) {
        if (trainingEx.containsAll(entry.getKey())) {
          this.frequencies.replace(entry.getKey(), entry.getValue() + 1);
        }
      }
    }
  }

  /**
   * Updates this nodes relative frequency table with the given training example.
   *
   * @param trainingEx training example
   */
  private void updateRelFrequency(List<IRandomVariable> trainingEx) {
    Map<List<IRandomVariable>, Integer> currentFrequency = this.relFrequencies.getFreqTable();

    // updates the frequency assignment for this node
    if (this.parents.isEmpty()) {
      for (Map.Entry<List<IRandomVariable>, Integer> entry : currentFrequency.entrySet()) {
        if (trainingEx.containsAll(entry.getKey())) {
          this.relFrequencies.replace(entry.getKey(), entry.getValue() + 1);
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

          this.relFrequencies.replace(entry.getKey(), entry.getValue() + 1);
        }
      }
    }

  }

  /**
   * Updates the node's CPT by updating the frequency tables and dividing the corresponding rows
   * to get a probability for each row.
   */
  @Override
  public void updateCPT(List<IRandomVariable> trainingEx) {
    if (trainingEx == null) {
      throw new IllegalArgumentException("Given list to train with cannot be null.");
    }

    this.updateFrequency(trainingEx);
    this.updateRelFrequency(trainingEx);
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
      this.cpt.replace(currentRow, prob);
    }
  }

  @Override
  public String getNodeName() {
    return this.name;
  }

  @Override
  public INode convertToInferenceCPT() {
    return new NodeImpl(this.name, this.parents, this.cpt.toInferenceCPT(this.name), this.frequencies,
            this.relFrequencies);
  }

  @Override
  public List<String> getParents() {
    return new ArrayList<>(this.parents);
  }

  @Override
  public boolean hasParent(String parentNode) {
    if (parentNode == null) {
      throw new IllegalArgumentException("Given parent cannot be null.");
    }
    return this.parents.contains(parentNode);
  }

  @Override
  public ICPT join(ICPT other, String joinVar) {
    if (other == null || joinVar == null) {
      throw new IllegalArgumentException("Given parameters cannot be null.");
    }
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

      if (join == null) {
        throw new IllegalArgumentException("Variable to join on must be in both CPTs");
      }

      // look through the larger CPT for common variables
      for (Map.Entry<List<IRandomVariable>, Double> bigEntry : bigger.entrySet()) {
        if (bigEntry.getKey().contains(join)) {
          List<IRandomVariable> newAssignment = new ArrayList<>(
                  bigEntry.getKey());
          newAssignment.addAll(smallEntry.getKey());
          List<IRandomVariable> noDups = this.removeDuplicates(newAssignment);
          newCPT.put(noDups, smallEntry.getValue() * bigEntry.getValue());
        }
      }
    }

    return new CPTImpl(newCPT);
  }

  @Override
  public ICPT getCPT() {
    return new CPTImpl(this.cpt.getCPT());
  }

  /**
   * Removes duplicates from a list of random variables.
   *
   * @param list of variables to filter
   * @return a filtered unique list
   */
  private List<IRandomVariable> removeDuplicates(List<IRandomVariable> list) {
    List<IRandomVariable> filteredList = new ArrayList<IRandomVariable>();

    for (IRandomVariable var : list) {
      if (!filteredList.contains(var)) {
        filteredList.add(var);
      }
    }
    return filteredList;
  }
}


