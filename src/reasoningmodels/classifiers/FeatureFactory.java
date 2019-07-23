package reasoningmodels.classifiers;

import reasoningmodels.knn.KNN;

public class FeatureFactory {

  /**
   * Creates an appropriate feature based on the value parameter. If the value can be parsed into
   * a double, a numerical feature will be created. Else, a boolean or categorical feature will
   * be created.
   *
   * @param featureName name of the feature
   * @param value value of the feature
   * @return the appropriate type of feature
   */
  public static IFeature createFeature(String featureName, String value) {
    double numVal = 0.0;
    try {
      numVal = Double.parseDouble(value);
      return new NumericalFeature(featureName, numVal);
    }
    catch (Exception e) {
      if (value.equalsIgnoreCase("True")) {
        return new BooleanFeature(featureName, 1.0);
      }
      else if (value.equalsIgnoreCase("False")) {
        return new BooleanFeature(featureName, 0.0);
      }
      else {
        return new CategoricalFeature(featureName, value);
      }
    }
  }
}
