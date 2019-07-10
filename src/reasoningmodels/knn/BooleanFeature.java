package reasoningmodels.knn;

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
}
