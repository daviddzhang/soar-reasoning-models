package reasoningmodels.bayesnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the ICPT interface. It represents the CPT as a Hashmap of a List of
 * random variables to doubles. Notable specifics of this implementations are specified below.
 */
public class CPTImpl implements ICPT, Serializable {
  private final Map<List<IRandomVariable>, Double> hm;

  /**
   * Constructs an instance of CPTImpl. Requires a mapping of random variables to doubles. This
   * will most likely be the initial structure of the CPT.
   *
   * @param hm the probability table
   * @throws IllegalArgumentException if the provided mapping is null
   */
  public CPTImpl(Map<List<IRandomVariable>, Double> hm) {
    if (hm == null) {
      throw new IllegalArgumentException("Provided mapping cannot be null.");
    }
    this.hm = hm;
  }

  /**
   * Formats the CPT as follows for each entry in the CPT:
   * [Random Variables] | Probability: x
   */
  @Override
  public String printCPT() {
    StringBuilder str = new StringBuilder();
    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      str.append(entry.getKey().toString()).append("| Probability: ").append(entry.getValue()).append("\n");
    }
    return str.toString();
  }

  @Override
  public Map<List<IRandomVariable>, Double> getCPT() {
    return new HashMap<>(this.hm);
  }

  /**
   * Checks if the given list of variables is in the CPT as an entry. Throws an exception if the
   * key provided is null.
   *
   * @param key list of variables to check for
   * @return true if the exact row is in the CPT, false otherwise
   * @throws IllegalArgumentException if the key is null
   */
  private boolean doesCPTIncludeKey(List<IRandomVariable> key) {
    if (key == null) {
      throw new IllegalArgumentException("Provided argument cannot be null.");
    }
    boolean result = false;
    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      if (entry.getKey().equals(key)) {
        result = true;
      }
    }
    return result;
  }

  @Override
  public ICPT toInferenceCPT(String nodeName) {
    if (nodeName == null) {
      throw new IllegalArgumentException("Provided argument cannot be null.");
    }

    Map<List<IRandomVariable>, Double> inferenceCPT = new HashMap<List<IRandomVariable>,
            Double>();

    // has no parents
    if (this.hm.entrySet().size() == 1) {
      Map.Entry<List<IRandomVariable>, Double> entry = this.hm.entrySet().iterator().next();
      List<IRandomVariable> newAssignmentFalse = new ArrayList<IRandomVariable>();
      inferenceCPT.put(entry.getKey(), entry.getValue());

      IRandomVariable falseVar = new RandomVariableImpl(entry.getKey().get(0).getName(), false);
      newAssignmentFalse.add(falseVar);
      inferenceCPT.put(newAssignmentFalse, 1 - entry.getValue());
    }
    else {
      for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
        List<IRandomVariable> newAssignmentTrue = new ArrayList<IRandomVariable>(entry.getKey());
        List<IRandomVariable> newAssignmentFalse = new ArrayList<IRandomVariable>(
                entry.getKey());
        IRandomVariable trueVar = new RandomVariableImpl(nodeName, true);
        newAssignmentTrue.add(trueVar);
        inferenceCPT.put(newAssignmentTrue, entry.getValue());

        IRandomVariable falseVar = new RandomVariableImpl(nodeName, false);
        newAssignmentFalse.add(falseVar);
        inferenceCPT.put(newAssignmentFalse, 1 - entry.getValue());
      }
    }
    return new CPTImpl(inferenceCPT);
  }

  @Override
  public List<String> variablesInCPT() {
    List<String> assignment = new ArrayList<>();
    Map<List<IRandomVariable>, Double> map = this.hm;
    if (this.hm.size() == 0) {
      return assignment;
    }
    Map.Entry<List<IRandomVariable>, Double> entry = map.entrySet().iterator().next();
    List<IRandomVariable> key = entry.getKey();
    for (int i = 0; i < key.size(); i++) {
      assignment.add(key.get(i).getName());
    }
    return assignment;
  }

  @Override
  public ICPT eliminateExcept(List<String> queryVars) {
    if (queryVars == null) {
      throw new IllegalArgumentException("Provided argument cannot be null.");
    }

    Map<List<IRandomVariable>, Double> currentCPT = new HashMap<>(this.hm);

    // list of variables to eliminate from the CPT
    List<String> eliminateVars = this.variablesInCPT();
    eliminateVars.removeAll(queryVars);

    // for every variable to eliminate, go through every entry of the currentCPT and
    // remove the current variable
    for (String eliminateVar : eliminateVars) {
      Map<List<IRandomVariable>, Double> newCPT = new HashMap<>();
      IRandomVariable trueElimVar = new RandomVariableImpl(eliminateVar, true);
      IRandomVariable falseElimVar = new RandomVariableImpl(eliminateVar, false);
      for (Map.Entry<List<IRandomVariable>, Double> entry : currentCPT.entrySet()) {
        List<IRandomVariable> newAssignment = new ArrayList<IRandomVariable>(entry.getKey());
        newAssignment.remove(trueElimVar);
        newAssignment.remove(falseElimVar);

        Map<List<IRandomVariable>, Double> tempCPT = new HashMap<>(currentCPT);
        tempCPT.remove(entry.getKey());

        // ensures there is no double counting for the current entry and curernt
        // variable
        for (Map.Entry<List<IRandomVariable>, Double> entryWoCurr : tempCPT.entrySet()) {
          if (entryWoCurr.getKey().containsAll(newAssignment)
                  && !new CPTImpl(newCPT).doesCPTIncludeKey(newAssignment)) {
            newCPT.put(newAssignment, entry.getValue() + entryWoCurr.getValue());
          }
        }
      }
      currentCPT.clear();
      currentCPT.putAll(newCPT);
    }
    return new CPTImpl(currentCPT);
  }

  @Override
  public void normalize() {
    double sum = 0.0;

    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      sum += entry.getValue();
    }

    if (sum == 0.0 && this.hm.size() != 0) {
      throw new IllegalArgumentException("Sum of CPT probabilities cannot be 0 before normalizing" +
              ".");
    }
    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      this.hm.replace(entry.getKey(), entry.getValue() / sum);
    }
  }

  @Override
  public double getQueryVar(List<IRandomVariable> queryList) {
    if (queryList == null) {
      throw new IllegalArgumentException("Given input cannot be null.");
    }

    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      if (entry.getKey().containsAll(queryList)) {
        return entry.getValue();
      }
    }
    throw new IllegalArgumentException("Given entry could not be found.");
  }

  @Override
  public void replace(List<IRandomVariable> rowToReplace, double newVal) {
    if (rowToReplace == null) {
      throw new IllegalArgumentException("Row to replace cannot be null.");
    }

    if (newVal < 0) {
      throw new IllegalArgumentException("Negative probability cannot be in CPT.");
    }
    this.hm.replace(rowToReplace, newVal);
  }
}
