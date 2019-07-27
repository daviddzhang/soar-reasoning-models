package reasoningmodels.bayesnet;

import java.util.List;
import java.util.Map;

/**
 * This interface represents a frequency table. Classes that implement this interface should have
 * a way of keeping track a count for a combination of random variables. They should be able to
 * represent the table as a string, update the table, and query for certain rows.
 */
public interface IFreqTable {

  /**
   * Prints the frequency table. Specifics of how this string is formatted should be specified in
   * implementations.
   *
   * @return the string representation of the frequency table
   */
  String printFreqTable();

  /**
   * Returns the frequency of the given list of variables.
   *
   * @param entry the list of variables of the entry to check for
   * @return the frequency of the entry
   * @throws IllegalArgumentException if the entry could not be found in the table
   */
  int getFreq(List<IRandomVariable> entry) throws IllegalArgumentException;

  /**
   * Returns a copy of the frequency table in the form of a mapping.
   *
   * @return the map representing the frequency table
   */
  Map<List<IRandomVariable>, Integer> getFreqTable();

  /**
   * Replaces the given row with the given probability. If the row cannot be found, nothing is
   * changed. Throws an exception for a negative probability.
   *
   * @param rowToReplace row to replace
   * @param newVal new probability associated to the row
   * @throws IllegalArgumentException if the probability is negative
   */
  void replace(List<IRandomVariable> rowToReplace, int newVal) throws IllegalArgumentException;
}
