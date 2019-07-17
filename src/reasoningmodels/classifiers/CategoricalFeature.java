package reasoningmodels.classifiers;

public class CategoricalFeature implements IFeature {
  private final String categoryName;
  private final double[] value;
  private final String categoricalValue;

  public CategoricalFeature(String categoryName, double[] value, String categoricalValue) {
    this.categoryName = categoryName;
    this.value = value;
    this.categoricalValue = categoricalValue;
  }

  @Override
  public boolean isCategorical() {
    return true;
  }

  @Override
  public double[] getValueAsVector() {
    return this.value;
  }

  @Override
  public double getValue() {
    throw new UnsupportedOperationException("Categorical features do not have numerical values.");
  }

  @Override
  public String getFeatureName() {
    return this.categoryName;
  }

  @Override
  public String getCategoricalValue() {
    return this.categoricalValue;
  }

  @Override
  public void scaleFeature(double max, double min) {
    throw new UnsupportedOperationException("Cannot scale categorical features.");
  }

  @Override
  public double getScaledValue() {
    throw new UnsupportedOperationException("Categorical features do not have scaled values.");
  }

  @Override
  public String toString() {
    return this.categoricalValue;
  }
}
