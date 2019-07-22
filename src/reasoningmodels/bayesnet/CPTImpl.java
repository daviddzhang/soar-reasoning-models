package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPTImpl implements ICPT {
  private Map<List<IRandomVariable>, Double> hm;

  public CPTImpl(Map<List<IRandomVariable>, Double> hm) {
    this.hm = hm;
  }

  // prints this CPT
  public String printCPT() {
    String str = "";
    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      str += entry.getKey().toString() + "| Probability: " + entry.getValue() + "\n";
    }
    return str;
  }

  @Override
  public Map<List<IRandomVariable>, Double> getCPT() {
    // although this is poor practice, for this implementation, the node and CPT are closely
    // interconnected and thus should have produce a mutable version of the CPT
    return this.hm;
  }

  // checks if this CPT includes the given HashMap key
  private boolean doesCPTIncludeKey(List<IRandomVariable> key) {
    boolean result = false;
    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      if (entry.getKey().equals(key)) {
        result = true;
      }
    }
    return result;
  }

  // constructs a new CPT with new rows belonging to the given node name
  public ICPT toInferenceCPT(String nodeName) {
    Map<List<IRandomVariable>, Double> inferenceCPT = new HashMap<List<IRandomVariable>,
            Double>();

    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {

      // if the current table belongs to a node that has parents
      if (this.hm.entrySet().size() > 1) {
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
      // if it has no parents
      else {
        ArrayList<IRandomVariable> newAssignmentFalse = new ArrayList<IRandomVariable>();
        inferenceCPT.put(entry.getKey(), entry.getValue());

        IRandomVariable falseVar = new RandomVariableImpl(nodeName, false);
        newAssignmentFalse.add(falseVar);
        inferenceCPT.put(newAssignmentFalse, 1 - entry.getValue());
      }
    }
    return new CPTImpl(inferenceCPT);
  }

  // returns a list of the variables in the CPT
  public List<String> variablesInCPT() {
    List<String> assignment = new ArrayList<>();
    Map<List<IRandomVariable>, Double> map = this.hm;
    Map.Entry<List<IRandomVariable>, Double> entry = map.entrySet().iterator().next();
    List<IRandomVariable> key = entry.getKey();
    for (int i = 0; i < key.size(); i++) {
      assignment.add(key.get(i).getName());
    }
    return assignment;
  }

  // outputs a new CPT that has only the given variables
  public ICPT eliminateExcept(List<String> queryVars) {
    Map<List<IRandomVariable>, Double> newCPT = new HashMap<>();
    Map<List<IRandomVariable>, Double> currentCPT = new HashMap<>(this.hm);

    // list of variables to eliminate from the CPT
    List<String> eliminateVars = this.variablesInCPT();
    eliminateVars.removeAll(queryVars);

    // for every variable to eliminate, go through every entry of the currentCPT and
    // remove the current variable
    for (int i = 0; i < eliminateVars.size(); i++) {
      String eliminateVar = eliminateVars.get(i);
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
      newCPT.clear();
    }
    return new CPTImpl(currentCPT);
  }

  // normalizes a CPT by dividing by sum of entries
  public void normalize() {
    Map<List<IRandomVariable>, Double> newCPT = new HashMap<>();
    Map<List<IRandomVariable>, Double> currentCPT = this.hm;
    double sum = 0.0;

    for (Map.Entry<List<IRandomVariable>, Double> entry : currentCPT.entrySet()) {
      sum += entry.getValue();
    }
    for (Map.Entry<List<IRandomVariable>, Double> entry : currentCPT.entrySet()) {
      newCPT.put(entry.getKey(), entry.getValue() / sum);
    }

    this.hm = newCPT;
  }

  // gets the probability associated with the given list of variables
  public double getQueryVar(List<IRandomVariable> queryList) {
    double result = -1.0;
    for (Map.Entry<List<IRandomVariable>, Double> entry : this.hm.entrySet()) {
      if (entry.getKey().containsAll(queryList)) {
        result = entry.getValue();
      }
    }
    return result;
  }
}
