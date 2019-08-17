package reasoningmodels.bayesnet;

import java.util.List;
import java.util.Map;

/**
 * A class that implements this interface represents a CPT for random variables. Implementations
 * should be able to perform operations on the rows such as elimination and updating values, as
 * well as outputting to a String.
 */
public interface ICPT {

  /**
   * Output the current state of the CPT as a string. Specific implementations decide how to
   * represent the information, and should document specifications.
   *
   * @return string representation of the CPT
   */
  String printCPT();

  /**
   * Return a shallow copy of the CPT in the format of a Map.
   *
   * @return a map containing information of the CPT
   */
  Map<List<IRandomVariable>, Double> getCPT();

  /**
   * Uses this CPT to output a new CPT suitable for inference operations. It should use the given
   * node name as the variable to duplicate each line by, or create a true/false row for each
   * entry in this CPT with complement probabilities. The input will most likely be the name of
   * the node to which this CPT belongs to.
   *
   * @param nodeName node to duplicate by
   * @return an inference-ready CPT
   */
  ICPT toInferenceCPT(String nodeName);

  /**
   * Gets a list of all random variable names in the CPT.
   *
   * @return the list of variable names
   */
  List<String> variablesInCPT();

  /**
   * Eliminates entries of the CPT based on the given list of variables. Entries that are
   * eliminated will have their probabilities appropriately added together until only the query
   * variables are left. Depending on the implementation, names that are not in the CPT may be
   * ignored and an empty list will result in an empty CPT.
   *
   * @param queryVars variables that should remain after elimination
   * @return the eliminated CPT
   */
  ICPT eliminateExcept(List<String> queryVars);

  /**
   * Normalizes all the probabilities in the CPT based on the sum.
   */
  void normalize();

  /**
   * Gets the probability associated with the given list of variables. Throws an exception if it
   * cannot be found.
   *
   * @param queryList the entry of variables in the CPT being queried
   * @return the probability for the given list of variables
   * @throws IllegalArgumentException if the list cannot be found
   */
  double getQueryVar(List<IRandomVariable> queryList) throws IllegalArgumentException;

  /**
   * Replaces the given row with the given probability. If the row cannot be found, nothing is
   * changed. Throws an exception for a negative probability.
   *
   * @param rowToReplace row to replace
   * @param newVal new probability associated to the row
   * @throws IllegalArgumentException if the probability is negative
   */
  void replace(List<IRandomVariable> rowToReplace, double newVal) throws IllegalArgumentException;
}
