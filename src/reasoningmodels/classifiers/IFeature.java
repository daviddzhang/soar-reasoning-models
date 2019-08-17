package reasoningmodels.classifiers;

/**
 * Classes that implement this interface represent features for a reasoning model. Not all types
 * of features will be able to implement these methods - in the cases where this is true, they
 * should throw an UnsupportedOperationException.
 */
public interface IFeature {

  /**
   * Is this feature categorical?
   *
   * @return true if it is, false otherwise
   */
  boolean isCategorical();

  /**
   * Returns the feature's numerical value as a vector. In non-categorical features, this will
   * simply convert the feature's numerical value into a length 1 array of just the value. In
   * categorical features, this will return the vector coordinate that the feature represents.
   *
   * @param enumerations the possible values for the feature
   *
   * @return the value of the feature as an array
   */
  double[] getValueAsVector(String[] enumerations);

  /**
   * Returns the feature's numerical value. May throw an exception if an implementation of
   * IFeature does not contain a numerical value.
   *
   * @return the value of the feature
   */
  double getValue();

  /**
   * Gets the name of the feature.
   *
   * @return the feature name
   */
  String getFeatureName();

  /**
   * Returns the categorical value of the feature. Non-categorical features do not need to
   * implement this functionality, as they have no categorical value.
   *
   * @return the categorical value of the feature
   */
  String getCategoricalValue();

  /**
   * Scales the feature from 0 to 1 based on it's value and the given min and max. Should throw
   * an exception if the feature's value is outside of the min and max bounds. Sets the resulting
   * value as a cached scaled value.
   *
   * @param min value
   * @param max value
   */
  void scaleFeature(double min, double max);

  /**
   * Gets the scaled value of this feature.
   *
   * @return the scaled feature value.
   */
  double getScaledValue();

  /**
   * Specifics of how the feature is printed as a string is up to specific implementations.
   *
   * @return a string representation of the feature
   */
  String toString();

}
