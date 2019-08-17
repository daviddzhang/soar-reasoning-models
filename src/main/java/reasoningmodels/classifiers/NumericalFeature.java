package reasoningmodels.classifiers;

/**
 * Represents a numerical feature. It contains information about the feature name and numerical
 * value.
 */
public class NumericalFeature extends AFeature {

  /**
   * Constructs an instance of a NumericalFeature with the given feature name and value.
   *
   * @param featureName name of the feature
   * @param value numerical value of the feature
   */
  public NumericalFeature(String featureName, double value) {
    super(featureName, value);
  }

  @Override
  public void scaleFeature(double min, double max) {
    if (this.value < min || this.value > max) {
      throw new IllegalArgumentException("Feature's value must be within supplied bounds.");
    }

    this.scaled = (this.value - min) / (max - min);
  }

  @Override
  public double getScaledValue() {
    return this.scaled;
  }


  @Override
  public String toString() {
    return Double.toString(this.value);
  }
}
