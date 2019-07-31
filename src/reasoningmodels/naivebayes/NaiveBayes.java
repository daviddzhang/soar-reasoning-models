package reasoningmodels.naivebayes;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import reasoningmodels.bayesnet.INode;
import reasoningmodels.classifiers.AClassifier;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public class NaiveBayes extends AClassifier {
  // represents the counts of the target feature's enumerations
  private Map<String, Integer> targetCounts = new HashMap<>();
  // key: one of the target feature's enumerations
  // value: mapping of a categorical feature to its count (i.e. If color was a feature, maps {red,
  // blue, green} to their respective counts.
  private Map<String, Map<String, Integer>> categoricalFeatureCounts = new HashMap<>();
  //similar as above, but keeps track of means instead of counts. Also, nested map maps from
  // feature name to mean, as opposed to feature value.
  private Map<String, Map<String, double[]>> numericalFeatureMeans = new HashMap<>();

  public NaiveBayes(String targetFeature) {
    super(targetFeature);
  }

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
        }
        else {
          Map<String, double[]> means = this.numericalFeatureMeans.get(targetFeatureEnum);
          double[] oldVals = means.get(feature.getFeatureName());
          double[] newVals = Arrays.copyOf(oldVals, oldVals.length + 1);
          newVals[oldVals.length] = feature.getValue();
          means.replace(feature.getFeatureName(), newVals);
        }
      }
    }

  }

  @Override
  public boolean hasFlatFeatures() {
    return true;
  }

  private void initData() {
    for (String featureEnum : this.features.get(targetClass)) {
      targetCounts.put(featureEnum, 0);
      categoricalFeatureCounts.put(featureEnum, new HashMap<>());
      numericalFeatureMeans.put(featureEnum, new HashMap<>());
    }

    for (Map.Entry<String, String[]> featureEntry : this.features.entrySet()) {
      if (featureEntry.getValue() == null || featureEntry.getValue().length == 1) {
        for (Map<String, double[]> means : numericalFeatureMeans.values()) {
          means.put(featureEntry.getKey(), new double[0]);
        }
      }
      else {
        for (String featureEnum : featureEntry.getValue()) {
          for (Map<String, Integer> counts : categoricalFeatureCounts.values()) {
            counts.put(featureEnum, 0);
          }
        }
      }
    }
  }

  private String queryHelper(IEntry queryEntry, double smoothing) {
    if (queryEntry.containsFeature(targetClass)) {
      throw new IllegalArgumentException("Query cannot contain target class.");
    }

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
        }
        else {
          double[] vals =
                  this.numericalFeatureMeans.get(targetFeatureEnum).get(queryFeature.getFeatureName());
          double prob = new NormalDistribution(StatUtils.mean(vals),
                  Math.sqrt(StatUtils.variance(vals))).density(queryFeature.getValue());
          overallLogProb += Math.log(prob);
        }
      }
      logProbs.put(targetFeatureEnum, overallLogProb);
    }

    Map.Entry<String, Double> maxEntry = null;

    for (Map.Entry<String, Double> entry : logProbs.entrySet())
    {
      if (maxEntry == null || entry.getValue() > maxEntry.getValue())
      {
        maxEntry = entry;
      }
    }

    return maxEntry.getKey();
  }

  @Override
  public String queryWithParams(IEntry queryEntry, Map<String, Object> queryParams) {
    if (queryEntry == null || queryParams == null) {
      throw new IllegalArgumentException("Cannot query with null arguments.");
    }

    String param = (String)queryParams.get("smoothing");
    if (param == null) {
      throw new IllegalArgumentException("Must provide smoothing when querying a Naive Bayes " +
              "model.");
    }

    double smoothing = Double.parseDouble(param);

    return this.queryHelper(queryEntry, smoothing);
  }

  @Override
  public void parameterizeWithFlatFeatures(Map<String, String[]> features) {
    this.features = features;
  }

  @Override
  public void parameterizeWithGraphicalFeatures(List<INode> nodes) {
    throw new IllegalArgumentException("Naive Bayes uses flat features.");
  }

  private double getPriorProb(String targetClassEnum) {
    int total = 0;
    for (int val : this.targetCounts.values()) {
      total += val;
    }

    return ((double)this.targetCounts.get(targetClassEnum) / total);
  }

  private int getFeatureDimensionality(String featureName) {
    return this.features.get(featureName).length;
  }

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
