package reasoningmodels.classifiers;

public interface IFeature {

  boolean isCategorical();

  /**
   * Returns the feature's numerical value as a vector. In non-categorical features, this will
   * simply convert the feature's numerical value into a length 1 array of just the value. In
   * categorical features, this will return the vector coordinate that the feature represents.
   *
   * @return the value of the feature as an array
   */
  double[] getValueAsVector();

  /**
   * Returns the feature's numerical value. May throw an exception if an implementation of
   * IFeature does not contain a numerical value.
   *
   * @return the value of the feature
   */
  double getValue() throws UnsupportedOperationException;

  String getFeatureName();

  /**
   * Returns the categorical value of the feature. Non-categorical features do not need to
   * implement this functionality, as they have no categorical value.
   *
   * @return
   * @throws UnsupportedOperationException
   */
  String getCategoricalValue() throws UnsupportedOperationException;

  void scaleFeature(double max, double min);

  double getScaledValue() throws UnsupportedOperationException;

  String toString();

}
