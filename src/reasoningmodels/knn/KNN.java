package reasoningmodels.knn;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.classifiers.AFlatClassifier;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

import static java.util.stream.Collectors.toMap;

/**
 * This class extends from AFlatClassifier and represents a K-nearest neighbor reasoning model.
 * Aside from details mentioned in AFlatClassifier, this class also contains a min-max table for
 * each numerical feature to allow for easy scaling when calculating distance. It requires a
 * distance function when querying.
 */
public class KNN extends AFlatClassifier {
  private final Map<String, Pair<Double, Double>> minMaxLookup;

  /**
   * Constructs an instance of KNN with the given target class.
   *
   * @param targetClass class to classify for
   */
  public KNN(String targetClass) {
    super(targetClass);
    minMaxLookup = new HashMap<>();
  }

  /**
   * On top of doing what is specified in AFlatClassifier, this train method also
   * initializes/updates the min-max lookup for numerical features as new entries come in.
   *
   * @param entry to train the model with
   */
  @Override
  public void train(IEntry entry) {
    super.train(entry);

    if (this.examples.size() == 1) {
      for (IFeature feature : entry.getFeatures()) {
        if (!feature.isCategorical()) {
          this.minMaxLookup.put(feature.getFeatureName(), new Pair<>(feature.getValue(),
                  feature.getValue()));
        }
      }
    }
    else {
      for (IFeature feature : entry.getFeatures()) {
        if (!feature.isCategorical()) {
          Pair<Double, Double> featureMinMax = this.minMaxLookup.get(feature.getFeatureName());
          if (feature.getValue() < featureMinMax.getKey()) {
            this.minMaxLookup.replace(feature.getFeatureName(), new Pair<>(feature.getValue(),
                    featureMinMax.getValue()));
            this.rescaleFeature(feature.getFeatureName());
          }
          else if (feature.getValue() > featureMinMax.getValue()) {
            this.minMaxLookup.replace(feature.getFeatureName(), new Pair<>(featureMinMax.getKey(),
                    feature.getValue()));
            this.rescaleFeature(feature.getFeatureName());
          }
        }
      }
    }
  }

  /**
   * Rescales the given feature with its newly assigned min-max values from the min-max lookup
   * table.
   *
   * @param featureName feature to rescale
   */
  private void rescaleFeature(String featureName) {
    for (IEntry entry : this.examples) {
      for (IFeature feature : entry.getFeatures()) {
        if (feature.getFeatureName().equalsIgnoreCase(featureName)) {
          Pair<Double, Double> featureMinMax = this.minMaxLookup.get(featureName);
          feature.scaleFeature(featureMinMax.getKey(), featureMinMax.getValue());
        }
      }
    }
  }

  /**
   * KNN requires a distance function, labeled "distance," and a k value as a String, labeled "k,"
   * from the
   * query parameters.
   */
  @Override
  public String queryWithParams(IEntry queryEntry, Map<String, Object> queryParams) {
    if (queryEntry == null || queryParams == null) {
      throw new IllegalArgumentException("Cannot query with null arguments.");
    }

    String paramK = (String)queryParams.get("k");
    if (paramK == null) {
      throw new IllegalArgumentException("Must provide k when querying a KNN " +
              "model.");
    }

    Object paramDistance = queryParams.get("distance");
    if (paramDistance == null) {
      throw new IllegalArgumentException("Must provide a distance function when querying a KNN " +
              "model.");
    }
    int k = Integer.parseInt(paramK);
    IDistanceFunction distanceFunction =
            IDistanceFunction.createDistanceFunction((String)paramDistance);

    return this.queryHelp(queryEntry, k, distanceFunction);
  }

  /**
   * Applies the distance function and gets a sorted list of distances to pick k neighbors from.
   * Also votes if k is greater than one. Gets the top result.
   *
   * @param queryEntry list of features in query
   * @param k number of neighbors to look at
   * @param distanceFunction distance function to apply
   * @return the query result or the resulting class
   */
  private String queryHelp(IEntry queryEntry, int k, IDistanceFunction distanceFunction) {
    if (queryEntry.containsFeature(targetClass)) {
      throw new IllegalArgumentException("Query cannot contain target class.");
    }

    // returns the target feature if possible
    String targetFeature = this.returnTargetFeatureIfPossible(queryEntry, k);

    Map<IEntry, Double> sorted = this.getSortedMapping(queryEntry, distanceFunction);

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

    return result;
  }

  /**
   * Checks to see if the query entry is valid (doesn't contain target class) and that k is valid
   * (not even or multiple of the target class dimension or non-positive). If everything is
   * valid, return the target class.
   *
   * @param queryEntry list of features in the query
   * @param k number of neighbors to look at
   * @return the target class if everything is valid
   */
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
      if (!queryEntry.containsFeature(feature.getFeatureName())) {
        targetFeature = feature.getFeatureName();
      }
    }

    if (k <= 0) {
      throw new IllegalArgumentException("K must be positive.");
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

  /**
   * Returns a map with sorted distances for each entry in this KNN's examples list.
   *
   * @param queryEntry list of features in the query
   * @param distanceFunction the distance function to apply
   * @return a sorted list of distances
   */
  private Map<IEntry, Double> getSortedMapping(IEntry queryEntry, IDistanceFunction distanceFunction) {
    Map<IEntry, Double> distanceMapping = new HashMap<>();

    for (IEntry entry : this.examples) {
      distanceMapping.put(entry, KNN.calcDistance(queryEntry, entry, distanceFunction,
              this.features));
    }

    return distanceMapping.entrySet().stream().sorted(Map.Entry.comparingByValue())
            .collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,
            LinkedHashMap::new));

  }

  /**
   * Calculates the distance between two entries with the given distance function. Also requires
   * a set of features and their enumerations.
   *
   * @param queryEntry the query entry
   * @param exampleEntry an example entry to compute distance with
   * @param distanceFunction the distance function to use
   * @param enumerations the set of features and their enumerations
   * @return the distance between the two entries
   */
  private static double calcDistance(IEntry queryEntry, IEntry exampleEntry,
                                    IDistanceFunction distanceFunction,
                                    Map<String, String[]> enumerations) {
    double res = 0.0;
    List<IFeature> queryFeatures = queryEntry.getFeatures();
    List<IFeature> exampleFeatures = exampleEntry.getFeatures();
    double[] queryVals = new double[queryFeatures.size()];
    double[] exampleVals = new double[queryFeatures.size()];
    for (int i = 0; i < queryFeatures.size(); i++) {
      String currentFeature = queryFeatures.get(i).getFeatureName();
      // iterate through the other entry's features to check for the current feature the loop
      // is on for this entry
      for (IFeature otherFeature : exampleFeatures) {
        if (otherFeature.getFeatureName().equals(currentFeature)) {
          // use vector calculations if the feature is categorical
          if (queryFeatures.get(i).isCategorical()) {
            res += distanceFunction.evaluate(queryFeatures.get(i).getValueAsVector(enumerations.get(currentFeature)),
                    otherFeature.getValueAsVector(enumerations.get(currentFeature)));
          } else {
            queryVals[i] = queryFeatures.get(i).getScaledValue();
            exampleVals[i] = otherFeature.getScaledValue();
          }
        }
      }
    }

    res += distanceFunction.evaluate(queryVals, exampleVals);

    return res;
  }

}
