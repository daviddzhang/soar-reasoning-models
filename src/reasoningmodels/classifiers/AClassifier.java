package reasoningmodels.classifiers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.bayesnet.INode;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public abstract class AClassifier implements IReasoningModel {
  protected final Map<String, String[]> features;
  protected final List<IEntry> examples;

  protected AClassifier(Map<String, String[]> features) {
    this.features = features;
    this.examples = new ArrayList<>();
  }

  public void addFeature(String feature, String[] enumerations) {
    this.features.put(feature, enumerations);
  }

  public void train(IEntry entry) {
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

  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    List<String> header = new ArrayList<>(this.features.keySet());
    stringBuilder.append(header.toString());
    stringBuilder.append("\n");

    for (IEntry entry : this.examples) {
      stringBuilder.append(entry.toString());
      stringBuilder.append("\n");
    }

    return stringBuilder.toString();
  }

  public Map<String, String[]> getFeatures() {
    return this.features;
  }

  @Override
  public void addNode(INode node) {
    throw new UnsupportedOperationException("Classifiers do not use nodes.");
  }

  @Override
  public double queryProbability(IEntry query, IEntry evidence) {
    throw new UnsupportedOperationException("Classifiers cannot query with evidence.");
  }
}
