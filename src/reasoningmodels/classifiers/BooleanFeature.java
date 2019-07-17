package reasoningmodels.classifiers;

public class BooleanFeature extends AFeature {

  public BooleanFeature(String featureName, double value) {
    super(featureName, value);
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
    if (.0001 > Math.abs(this.value - 1.0)) {
      return "TRUE";
    }
    else {
      return "FALSE";
    }
  }
}
