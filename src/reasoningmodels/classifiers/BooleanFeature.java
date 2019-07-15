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
  public IFeature getScaled(double max, double min) {
    return this;
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
