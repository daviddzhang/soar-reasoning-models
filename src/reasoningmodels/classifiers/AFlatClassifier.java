package reasoningmodels.classifiers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.bayesnet.INode;

/**
 * An abstract class representing a classifier. Contains a set of all features the classifier should
 * know about, existing examples already trained, and a target class. Extending classes may modify
 * how certain actions are performed, like training and querying.
 */
public abstract class AFlatClassifier implements IReasoningModel, Serializable {
  protected final Map<String, String[]> features;
  protected final List<IEntry> examples;
  protected final String targetClass;

  /**
   * Constructs an instance of a classifier with the given target class.
   *
   * @param targetClass class for the classifier
   */
  protected AFlatClassifier(String targetClass) {
    if (targetClass == null) {
      throw new IllegalArgumentException("Target class cannot be null.");
    }

    this.features = new HashMap<>();
    this.examples = new ArrayList<>();
    this.targetClass = targetClass;
  }

  /**
   * Adds the given entry into the examples list. Will throw an error if the entry doesn't match up
   * with the features of the classifier.
   *
   * @param entry to train the model with
   */
  @Override
  public void train(IEntry entry) {
    if (entry == null) {
      throw new IllegalArgumentException("Training example cannot be null.");
    }

    List<String> entryToBeAdded = new ArrayList<>();
    for (IFeature feature : entry.getFeatures()) {
      entryToBeAdded.add(feature.getFeatureName());
    }

    if (entry.getFeatures().size() != this.features.size() || !this.features.keySet().containsAll(entryToBeAdded)) {
      throw new IllegalArgumentException("Given entry does not have the same features as the " +
              "specified features");
    }

    this.examples.add(entry);
  }

  /**
   * Formats the string as follows: [names of features] [example 1] ... [example x]
   */
  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    List<String> header = new ArrayList<>();
    if (this.examples.size() == 0) {
      header = new ArrayList<>(this.features.keySet());
    } else {
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
  public boolean hasFlatFeatures() {
    return true;
  }

  @Override
  public void parameterizeWithFlatFeatures(Map<String, String[]> features) {
    if (features == null) {
      throw new IllegalArgumentException("Features cannot be null.");
    }

    if (!features.containsKey(this.targetClass)) {
      throw new IllegalArgumentException("Supplied features must contain the target class.");
    }

    this.features.clear();
    this.features.putAll(features);
  }

  @Override
  public void parameterizeWithGraphicalFeatures(List<INode> nodes) {
    throw new UnsupportedOperationException("Flat-featured classifiers do not take graphical features.");
  }

}
