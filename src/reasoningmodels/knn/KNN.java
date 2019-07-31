package reasoningmodels.knn;

import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.bayesnet.INode;
import reasoningmodels.classifiers.AClassifier;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

import static java.util.stream.Collectors.toMap;

public class KNN extends AClassifier {
  private final Map<String, Pair<Double, Double>> minMaxLookup;

  public KNN(String targetClass) {
    super(targetClass);
    minMaxLookup = new HashMap<>();
  }

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
          } else if (feature.getValue() > featureMinMax.getValue()) {
            this.minMaxLookup.replace(feature.getFeatureName(), new Pair<>(featureMinMax.getKey(),
                    feature.getValue()));
            this.rescaleFeature(feature.getFeatureName());
          }
        }
      }
    }

  }

  @Override
  public boolean hasFlatFeatures() {
    return true;
  }

  @Override
  public void parameterizeWithFlatFeatures(Map<String, String[]> features) {
    this.features = features;
  }

  @Override
  public void parameterizeWithGraphicalFeatures(List<INode> nodes) {
    throw new IllegalArgumentException("KNN uses flat features.");
  }

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

  private String queryHelp(IEntry queryEntry, int k, IDistanceFunction distanceFunction) {
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
