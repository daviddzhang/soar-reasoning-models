package reasoningmodels.naivebayes;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.classifiers.AFlatClassifier;
import reasoningmodels.classifiers.EntryImpl;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

/**
 * This class extends from AFlatClassifier. On top of the details specified in the abstract
 * class, this class also contains many data structures to hold on to counts of certain features
 * to make calculating probability at query time faster. As training examples are added to the
 * model, counts are updated and will be queried for during a query. Querying also must provide a
 * smoothing value for calculations. For numerical features, a normal distribution is assumed for
 * finding probability. In the off chance that standard deviation for a numerical feature is 0, a
 * small standard deviation is assumed that scales with the number of examples seen so far for
 * the feature.
 */
public class NaiveBayes extends AFlatClassifier {
  // represents the counts of the target feature's enumerations
  private Map<String, Integer> targetCounts = new HashMap<>();

  // key: one of the target feature's enumerations
  // value: mapping of a categorical feature to its count (i.e. If color was a feature, maps {red,
  // blue, green} to their respective counts.
  private Map<String, Map<String, Integer>> categoricalFeatureCounts = new HashMap<>();

  //similar as above, but keeps track of means instead of counts. Also, nested map maps from
  // feature name to mean, as opposed to feature value.
  private Map<String, Map<String, double[]>> numericalFeatureMeans = new HashMap<>();

  /**
   * Constructs an instance of NaiveBayes with the given target class.
   *
   * @param targetFeature the target class
   */
  public NaiveBayes(String targetFeature) {
    super(targetFeature);
  }

  /**
   * On top of what is specified in the abstract class, this method also initializes/maintains
   * counts of specific features to use at query time.
   */
  @Override
  public void train(IEntry entry) {
    super.train(entry);
    if (this.examples.size() == 1) {
      this.initData();
    }

    String targetFeatureEnum = this.getTargetFeatureEnum(entry);
    this.targetCounts.replace(targetFeatureEnum, this.targetCounts.get(targetFeatureEnum) + 1);

    for (IFeature feature : entry.getFeatures()) {
      if (!feature.getFeatureName().equalsIgnoreCase(this.targetClass)) {
        if (feature.isCategorical()) {
          Map<String, Integer> counts = this.categoricalFeatureCounts.get(targetFeatureEnum);
          counts.replace(feature.getCategoricalValue(),
                  counts.get(feature.getCategoricalValue()) + 1);
        } else {
          Map<String, double[]> means = this.numericalFeatureMeans.get(targetFeatureEnum);
          double[] oldVals = means.get(feature.getFeatureName());
          double[] newVals = Arrays.copyOf(oldVals, oldVals.length + 1);
          newVals[oldVals.length] = feature.getValue();
          means.replace(feature.getFeatureName(), newVals);
        }
      }
    }

  }

  /**
   * Initializes the maps used to keep track of counts.
   */
  private void initData() {
    for (String featureEnum : this.features.get(targetClass)) {
      targetCounts.put(featureEnum, 0);
      categoricalFeatureCounts.put(featureEnum, new HashMap<>());
      numericalFeatureMeans.put(featureEnum, new HashMap<>());
    }

    for (Map.Entry<String, String[]> featureEntry : this.features.entrySet()) {
      if (featureEntry.getValue() == null || featureEntry.getValue().length == 0) {
        for (Map<String, double[]> means : numericalFeatureMeans.values()) {
          means.put(featureEntry.getKey(), new double[0]);
        }
      } else {
        for (String featureEnum : featureEntry.getValue()) {
          for (Map<String, Integer> counts : categoricalFeatureCounts.values()) {
            counts.put(featureEnum, 0);
          }
        }
      }
    }
  }

  /**
   * Naive Bayes requires a smoothing parameter to be provided as a String, labeled "smoothing",
   * from the parameters.
   */
  @Override
  public String queryWithParams(IEntry queryEntry, Map<String, Object> queryParams) {
    if (queryEntry == null || queryParams == null) {
      throw new IllegalArgumentException("Cannot query with null arguments.");
    }

    if (this.examples.isEmpty()) {
      throw new IllegalArgumentException("Cannot query without training first.");
    }

    String param = (String) queryParams.get("smoothing");
    if (param == null) {
      throw new IllegalArgumentException("Must provide smoothing when querying a Naive Bayes " +
              "model.");
    }

    double smoothing = Double.parseDouble(param);

    if (smoothing <= 0) {
      throw new IllegalArgumentException("Smoothing value must be positive.");
    }

    return this.queryHelper(queryEntry, smoothing);
  }

  /**
   * Calculates probabilities for each possible classification and returns the enumeration that
   * has the highest probability based on the query entry. Details on how this is done are
   * specified in the class comments.
   *
   * @param queryEntry list of features in the query
   * @param smoothing value used to smooth unobserved features
   * @return the query result, or the classification for the query
   */
  private String queryHelper(IEntry queryEntry, double smoothing) {
    if (queryEntry.containsFeature(targetClass)) {
      throw new IllegalArgumentException("Query cannot contain target class.");
    }

    queryEntry = this.filterUnseenNumericalFeatures(queryEntry);
    Map<String, Double> logProbs = new HashMap<>();

    for (String targetFeatureEnum : this.features.get(this.targetClass)) {
      double overallLogProb = Math.log(this.getPriorProb(targetFeatureEnum));
      for (IFeature queryFeature : queryEntry.getFeatures()) {
        if (queryFeature.isCategorical()) {
          double prob =
                  (this.categoricalFeatureCounts.get(targetFeatureEnum)
                          .get(queryFeature.getCategoricalValue()) + smoothing)
                          / (this.targetCounts.get(targetFeatureEnum) + (smoothing * this.getFeatureDimensionality(queryFeature.getFeatureName())));
          overallLogProb += Math.log(prob);
        } else {
          double[] vals =
                  this.numericalFeatureMeans.get(targetFeatureEnum).get(queryFeature.getFeatureName());

          double stdDev = StatUtils.variance(vals);

          // if standard deviation is 0, assume a small standard deviation that scales to become
          // smaller with more examples
          if (stdDev == 0.0) {
            stdDev = Math.pow(.5, vals.length);
          }

          double prob = new NormalDistribution(StatUtils.mean(vals),
                  Math.sqrt(stdDev)).density(queryFeature.getValue());
          overallLogProb += Math.log(prob);
        }
      }
      logProbs.put(targetFeatureEnum, overallLogProb);
    }

    Map.Entry<String, Double> maxEntry = null;

    for (Map.Entry<String, Double> entry : logProbs.entrySet()) {
      if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
        maxEntry = entry;
      }
    }

    return maxEntry.getKey();
  }

  /**
   * Filters unseen numerical features from the query entry to avoid impossible computations.
   *
   * @param queryEntry list of features in the query
   * @return a new entry with unseen numerical features filtered away
   */
  private IEntry filterUnseenNumericalFeatures(IEntry queryEntry) {
    List<IFeature> res = new ArrayList<>();
    for (IFeature feature : queryEntry.getFeatures()) {
      if (feature.isCategorical()) {
        res.add(feature);
      } else {
        boolean shouldFilter = false;
        for (Map<String, double[]> entry :
                this.numericalFeatureMeans.values()) {
          double[] arrForFeature = entry.get(feature.getFeatureName());
          if (arrForFeature.length <= 1) {
            shouldFilter = true;
          }
        }

        if (!shouldFilter) {
          res.add(feature);
        }
      }
    }

    return new EntryImpl(res);
  }

  /**
   * Gets the prior probability for the target class. Should never divide by 0 as querying with
   * no training is impossible.
   *
   * @param targetClassEnum a specific enumeration of the target class
   * @return the prior probability for the given enumeration
   */
  private double getPriorProb(String targetClassEnum) {
    int total = 0;
    for (int val : this.targetCounts.values()) {
      total += val;
    }

    return ((double) this.targetCounts.get(targetClassEnum) / total);
  }

  /**
   * Gets the dimensionality of the given feature.
   *
   * @param featureName feature to check dimensions of
   * @return the dimensionality of the feature
   */
  private int getFeatureDimensionality(String featureName) {
    return this.features.get(featureName).length;
  }

  /**
   * Gets the categorical value of the feature belonging to the target class from the given entry.
   *
   * @param entry entry to check within
   * @return the categorical value of the target class feature
   */
  private String getTargetFeatureEnum(IEntry entry) {
    for (IFeature feature : entry.getFeatures()) {
      if (feature.getFeatureName().equalsIgnoreCase(this.targetClass)) {
        return feature.getCategoricalValue();
      }
    }

    //shouldn't get here
    throw new IllegalArgumentException("Could not find target feature in the given entry" +
            ".");
  }

}
