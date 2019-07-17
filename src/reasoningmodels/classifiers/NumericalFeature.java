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
  public void scaleFeature(double max, double min) {
    double scaledVal = (this.value - min) / (max - min);
    this.scaled = scaledVal;
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
