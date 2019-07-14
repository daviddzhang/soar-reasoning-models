package reasoningmodels.classifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public abstract class AClassifier implements IReasoningModel {
  protected final Map<String, String[]> features;
  protected final List<IEntry> examples;
  protected String targetResult;

  protected AClassifier() {
    this.features = new HashMap<>();
    this.examples = new ArrayList<>();
  }

  public void addFeature(String feature, String[] enumerations) {
    this.features.put(feature, enumerations);
  }

  public void addEntry(IEntry entry) {
    List<String> entryToBeAdded = new ArrayList<>();
    for (IFeature feature : entry.getFeatures()) {
      entryToBeAdded.add(feature.getFeatureName());
    }
    if (entry.getFeatures().size() != this.features.size() && !this.features.keySet().containsAll(entryToBeAdded)) {
      throw new IllegalArgumentException("Given entry does not have the same features as the " +
              "specified features");
    }

    this.examples.add(entry);
  }
}
