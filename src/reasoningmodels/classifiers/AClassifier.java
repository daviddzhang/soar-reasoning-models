package reasoningmodels.classifiers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.bayesnet.INode;

public abstract class AClassifier implements IReasoningModel {
  protected Map<String, String[]> features;
  protected final List<IEntry> examples;
  protected final String targetClass;

  protected AClassifier(String targetClass) {
    this.examples = new ArrayList<>();
    this.targetClass = targetClass;
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
    List<String> header = new ArrayList<>();
    if (this.examples.size() == 0) {
      header = new ArrayList<>(this.features.keySet());
    }
    else {
      List<IFeature> firstEntry = this.examples.get(0).getFeatures();
      for (IFeature feature : firstEntry) {
        header.add(feature.getFeatureName());
      }
    }
    stringBuilder.append(header.toString());
    stringBuilder.append("\n");

    for (IEntry entry : this.examples) {
      stringBuilder.append(entry.toString());
      stringBuilder.append("\n");
    }

    return stringBuilder.toString();
  }

  @Override
  public abstract boolean hasFlatFeatures();

  @Override
  public abstract void parameterizeWithFlatFeatures(Map<String, String[]> features);

  @Override
  public abstract void parameterizeWithGraphicalFeatures(List<INode> nodes);

}
