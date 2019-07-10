package reasoningmodels.knn;

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
   * Returns the feature's numerical value. For categorical features, depending on the
   * implementation, may return 1 to represent its distance from the origin.
   *
   * @return the value of the feature
   */
  double getValue();

  String getFeatureName();

  /**
   * Returns the categorical value of the feature. Non-categorical features do not need to
   * implement this functionality, as they have no categorical value.
   *
   * @return
   * @throws UnsupportedOperationException
   */
  String getCategoricalValue() throws UnsupportedOperationException;

  IFeature getScaled(double max, double min);

}
