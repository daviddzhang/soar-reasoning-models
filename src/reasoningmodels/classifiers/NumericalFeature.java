package reasoningmodels.classifiers;

public class NumericalFeature extends AFeature {

  public NumericalFeature(String featureName, double value) {
    super(featureName, value);
  }

  @Override
  public boolean isCategorical() {
    return false;
  }

  @Override
  public IFeature getScaled(double max, double min) {
    double scaledVal = (this.value - min) / (max - min);
    return new NumericalFeature(this.featureName, scaledVal);
  }

  @Override
  public String toString() {
    return Double.toString(this.value);
  }
}
