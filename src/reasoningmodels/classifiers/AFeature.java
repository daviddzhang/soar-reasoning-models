package reasoningmodels.classifiers;

public abstract class AFeature implements IFeature {
  protected final String featureName;
  protected final double value;
  protected double scaled;

  protected AFeature(String featureName, double value) {
    this.featureName = featureName;
    this.value = value;
  }

  @Override
  public abstract boolean isCategorical();

  @Override
  public double[] getValueAsVector() {
    double[] res = {this.value};
    return res;
  }

  @Override
  public double getValue() {
    return this.value;
  }

  @Override
  public String getFeatureName() {
    return this.featureName;
  }

  @Override
  public String getCategoricalValue() throws UnsupportedOperationException {
    throw new UnsupportedOperationException("Non-categorical features do not have categorical " +
            "values.");
  }

  @Override
  public abstract void scaleFeature(double max, double min);

  @Override
  public abstract double getScaledValue();
}
