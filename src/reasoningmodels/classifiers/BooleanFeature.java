package reasoningmodels.classifiers;

/**
 * Represents a boolean feature. It extends AFeature. True is represented by 1.0, and false is
 * represented by 0.0.
 */
public class BooleanFeature extends AFeature {

  /**
   * Constructs an instance of BooleanFeature with the given feature name and value. Throws an
   * exception if the value is not 1.0 or 0.0.
   *
   * @param featureName name of the feature
   * @param value 1.0 for true, 0.0 for false
   * @throws IllegalArgumentException if the value is not 1.0 nor 0.0
   */
  public BooleanFeature(String featureName, double value) {
    super(featureName, value);
    if (this.value != 0.0 && this.value != 1.0) {
      throw new IllegalArgumentException("Boolean values must be either 1 or 0");
    }
  }

  /**
   * Sets the scaled value to the existing value. Boolean values do not need to be scaled on a
   * 0-1 scale.
   */
  @Override
  public void scaleFeature(double min, double max) {
    this.scaled = this.value;
  }

  @Override
  public double getScaledValue() {
    return this.scaled;
  }

  /**
   * Returns "TRUE" for true and "FALSE" for false.
   */
  @Override
  public String toString() {
    if (this.value == 1.0) {
      return "TRUE";
    }
    else {
      return "FALSE";
    }
  }

}
