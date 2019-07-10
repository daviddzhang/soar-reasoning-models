package reasoningmodels.knn;

public abstract class AFeature implements IFeature {
  protected String featureName;
  protected double value;

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
  public abstract IFeature getScaled(double max, double min);
}
