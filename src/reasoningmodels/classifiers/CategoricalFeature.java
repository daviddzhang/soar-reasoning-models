package reasoningmodels.classifiers;

public class CategoricalFeature implements IFeature {
  String categoryName;
  double[] value;
  String categoricalValue;

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
    return 1;
  }

  @Override
  public String getFeatureName() {
    return this.getFeatureName();
  }

  @Override
  public String getCategoricalValue() {
    return this.categoricalValue;
  }

  @Override
  public IFeature getScaled(double max, double min) {
    return this;
  }
}
