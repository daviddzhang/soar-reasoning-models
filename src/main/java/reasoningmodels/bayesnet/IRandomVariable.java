package reasoningmodels.bayesnet;

/**
 * This interface represents a random variable. Classes that implement this interface should be
 * able to represent a variable with a name and a boolean value indicating whether it occurred or
 * not.
 */
public interface IRandomVariable {

  /**
   * Returns a string representation of this random variable. Specific implementations of this
   * method should specify the format.
   *
   * @return the string representing the random variable
   */
  String toString();

  @Override
  boolean equals(Object other);

  /**
   * Gets the name of the random variable.
   *
   * @return the name of the random variable
   */
  String getName();

  /**
   * Gets whether the RV occurred or not.
   *
   * @return true if it occurred, false otherwise
   */
  boolean getHasOccurred();

  @Override
  int hashCode();
}
