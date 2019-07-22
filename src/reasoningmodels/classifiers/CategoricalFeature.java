package reasoningmodels.classifiers;

public class CategoricalFeature implements IFeature {
  private final String categoryName;
  private final String categoricalValue;

  public CategoricalFeature(String categoryName,  String categoricalValue) {
    this.categoryName = categoryName;
    this.categoricalValue = categoricalValue;
  }

  @Override
  public boolean isCategorical() {
    return true;
  }

  @Override
  public double[] getValueAsVector(String[] enumerations) {
    double[] res = new double[enumerations.length];
    for (int i = 0; i < enumerations.length; i++) {
      if (enumerations[i].equalsIgnoreCase(this.categoricalValue)) {
        res[i] = 1.0;
      }
    }
    return res;
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
