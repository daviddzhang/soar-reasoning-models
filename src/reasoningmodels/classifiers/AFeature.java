package reasoningmodels.classifiers;

import java.io.Serializable;

/**
 * An abstract class representing fields and methods that all non-categorical features for a
 * reasoning model should have. Contains information like the feature name and value.
 */
public abstract class AFeature implements IFeature, Serializable {
  protected final String featureName;
  protected final double value;
  protected double scaled;

  /**
   * Constructs an instance of a feature with the given feature name and double value.
   * @param featureName name of the feature
   * @param value of the feature
   */
  protected AFeature(String featureName, double value) {
    if (featureName == null) {
      throw new IllegalArgumentException("Name cannot be null.");
    }
    this.featureName = featureName;
    this.value = value;
  }

  @Override
  public boolean isCategorical() {
    return false;
  }

  /**
   * Throws an exception as non-categorical features should not need to represent values as vectors.
   */
  @Override
  public double[] getValueAsVector(String[] enumerations) {
    throw new UnsupportedOperationException("Non-categorical features do not need to convert " +
            "values into a vector.");
  }

  @Override
  public double getValue() {
    return this.value;
  }

  @Override
  public String getFeatureName() {
    return this.featureName;
  }

  /**
   * Throws an unsupported exception as this abstract class represents non-categorical features,
   * thus it does not have a categorical value.
   *
   * @throws UnsupportedOperationException for being non-categorical
   */
  @Override
  public String getCategoricalValue() throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Non-categorical features do not have categorical " +
            "values.");
  }

  @Override
  public abstract void scaleFeature(double min, double max);

  @Override
  public abstract double getScaledValue();
}
