package reasoningmodels.classifiers;

import java.io.Serializable;

/**
 * Represents a categorical feature. It contains the name of the feature, and the actual
 * categorical value for the feature. The enumerations for the category should be specified
 * elsewhere, like in a classifier. Throws unsupported exceptions for numerical related methods,
 * like scaling.
 */
public class CategoricalFeature implements IFeature, Serializable {
  private final String categoryName;
  private final String categoricalValue;

  /**
   * Constructs an instance of a CategoricalFeature with the given category name and value.
   *
   * @param categoryName name of the categorical feature
   * @param categoricalValue value of the category
   */
  public CategoricalFeature(String categoryName,  String categoricalValue) {
    if (categoryName == null || categoricalValue == null) {
      throw new IllegalArgumentException("Categorical features cannot have a null name/value.");
    }

    this.categoryName = categoryName;
    this.categoricalValue = categoricalValue;
  }

  @Override
  public boolean isCategorical() {
    return true;
  }

  @Override
  public double[] getValueAsVector(String[] enumerations) {
    if (enumerations == null) {
      throw new IllegalArgumentException("Enumerations cannot be null");
    }

    if (enumerations.length == 0) {
      throw new IllegalArgumentException("Enumerations cannot be empty.");
    }

    double[] res = new double[enumerations.length];
    for (int i = 0; i < enumerations.length; i++) {
      if (enumerations[i].equalsIgnoreCase(this.categoricalValue)) {
        res[i] = 1.0;
      }
    }
    return res;
  }

  /**
   * Throws an exception as a categorical feature does not have a numerical value.
   */
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
  public void scaleFeature(double min, double max) {
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
