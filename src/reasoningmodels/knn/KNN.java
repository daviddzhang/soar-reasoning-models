package reasoningmodels.knn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.classifiers.AClassifier;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

import static java.util.stream.Collectors.toMap;

public class KNN extends AClassifier {
  private DistanceFunction distanceFunction = DistanceFunction.EUCLIDEAN;

  public KNN() {
    super();
  }

  public void setDistanceFunction(DistanceFunction distanceFunction) {
    this.distanceFunction = distanceFunction;
  }

  public void query(IEntry queryEntry, int k) {
    // returns the target feature if possible
    String targetFeature = this.returnTargetFeatureIfPossible(queryEntry, k);

    Map<IEntry, Double> sorted = this.getSortedMapping(queryEntry);

    Iterator<IEntry> sortedEntries = sorted.keySet().iterator();
    List<IEntry> firstKEntries = new ArrayList<>();

    for (int i = 0; i < k; i++) {
      firstKEntries.add(sortedEntries.next());
    }

    Map<String, Integer> voteCounts = new HashMap<>();
    for (String option : this.features.get(targetFeature)) {
      voteCounts.put(option, 0);
    }

    for (IEntry entry : firstKEntries) {
      List<IFeature> features = entry.getFeatures();
      for (IFeature feature : features) {
        if (feature.getFeatureName().equals(targetFeature)) {
          voteCounts.replace(feature.getCategoricalValue(),
                  voteCounts.get(feature.getCategoricalValue()) + 1);
        }
      }
    }

    String result = null;
    int max = -1;

    for (String string : voteCounts.keySet()) {
      if (voteCounts.get(string) > max) {
        max = voteCounts.get(string);
        result = string;
      }
    }

    this.targetResult = result;
  }

  private String returnTargetFeatureIfPossible(IEntry queryEntry, int k) {
    if (this.examples.isEmpty()) {
      throw new IllegalArgumentException("Cannot query when there are no examples");
    }

    if (queryEntry.getFeatures().size() >= this.examples.get(0).getFeatures().size()) {
      throw new IllegalArgumentException("Supplied query cannot have all the features/more " +
              "features than the provided training examples.");
    }

    // get which feature is being predicted
    String targetFeature = null;
    for (IFeature feature : this.examples.get(0).getFeatures()) {
      if (!queryEntry.containsFeature(feature)) {
        targetFeature = feature.getFeatureName();
      }
    }

    if (k % 2 == 0 || k % this.features.get(targetFeature).length == 0) {
      throw new IllegalArgumentException("Pick a k that is not even and not a multiple of the " +
              "number of possibilities of desired feature.");
    }

    if (k > this.examples.size()) {
      throw new IllegalArgumentException("Pick a k that is less than or equal to the number of " +
              "training examples.");
    }
    return targetFeature;
  }

  private Map<IEntry, Double> getSortedMapping(IEntry queryEntry) {
    Map<IEntry, Double> distanceMapping = new HashMap<>();

    for (IEntry entry : this.examples) {
      distanceMapping.put(entry, queryEntry.calcDistance(this.distanceFunction, entry));
    }

    Map<IEntry, Double> sorted =
            distanceMapping.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
                    LinkedHashMap::new));

    return sorted;
  }
}
