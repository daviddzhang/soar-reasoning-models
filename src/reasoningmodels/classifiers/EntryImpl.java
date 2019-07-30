package reasoningmodels.classifiers;

import java.util.List;


public class EntryImpl implements IEntry {
  private final List<IFeature> features;

  public EntryImpl(List<IFeature> features) {
    this.features = features;
  }


  @Override
  public List<IFeature> getFeatures() {
    return this.features;
  }

  @Override
  public boolean containsFeature(String otherFeatureName) {
    for (IFeature curFeature : this.features) {
      String curFeatureName = curFeature.getFeatureName();
      if (curFeatureName.equals(otherFeatureName)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return this.features.toString();
  }

}
