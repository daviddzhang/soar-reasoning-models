package reasoningmodels.classifiers;

public class BooleanFeature extends AFeature {

  public BooleanFeature(String featureName, double value) {
    super(featureName, value);
    if (this.value != 0.0 && this.value != 1.0) {
      throw new IllegalArgumentException("Boolean values must be either 1 or 0");
    }
  }

  @Override
  public boolean isCategorical() {
    return false;
  }

  @Override
  public void scaleFeature(double max, double min) {
    this.scaled = this.value;
  }

  @Override
  public double getScaledValue() {
    return this.scaled;
  }

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
