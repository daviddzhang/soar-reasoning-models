package reasoningmodels.classifiers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of IEntry. Holds its features as a list of features.
 */
public class EntryImpl implements IEntry, Serializable {
  private final List<IFeature> features;

  /**
   * Constructs an instance of an EntryImpl with the given list of features.
   *
   * @param features list of features of the entry
   */
  public EntryImpl(List<IFeature> features) {
    if (features == null) {
      throw new IllegalArgumentException("Given features cannot be null.");
    }
    this.features = features;
  }

  @Override
  public List<IFeature> getFeatures() {
    return new ArrayList<>(this.features);
  }

  @Override
  public boolean containsFeature(String otherFeatureName) {
    if (otherFeatureName == null) {
      throw new IllegalArgumentException("Given feature name cannot be null.");
    }

    for (IFeature curFeature : this.features) {
      String curFeatureName = curFeature.getFeatureName();
      if (curFeatureName.equals(otherFeatureName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Formatted as follows:
   * [feature1, feature2, ..., featurex]
   */
  @Override
  public String toString() {
    return this.features.toString();
  }

}
