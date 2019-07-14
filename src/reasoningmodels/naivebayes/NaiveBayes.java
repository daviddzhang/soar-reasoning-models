package reasoningmodels.naivebayes;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.descriptive.moment.Mean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import reasoningmodels.classifiers.AClassifier;
import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public class NaiveBayes extends AClassifier {

  public NaiveBayes() {
    super();
  }

  public void query(IEntry queryEntry) {
    // returns the target feature if possible (assuming target feature can change)
    String targetFeature = this.returnTargetFeatureIfPossible(queryEntry);

    // get the counts of all the appearances of the prior/target feature
    Map<String, Integer> priorCounts = new HashMap<>();

    for (String option : this.features.get(targetFeature)) {
      priorCounts.put(option, 0);
    }

    for (IEntry entry : this.examples) {
      List<IFeature> features = entry.getFeatures();
      for (IFeature feature : features) {
        if (feature.getFeatureName().equals(targetFeature)) {
          priorCounts.replace(feature.getCategoricalValue(),
                  priorCounts.get(feature.getCategoricalValue()) + 1);
        }
      }
    }

    // get the counts of all the categorical features for each prior class yj
    // the nest map maps categorical values from the query features to counts
    Map<String, Map<String, Integer>> jointCountsForQueryFeatures = new HashMap<>();

    for (String option : this.features.get(targetFeature)) {
      Map<String, Integer> featureCountForAPrior = new HashMap<>();
      // p(xi| yj)
      for (IFeature feature : queryEntry.getFeatures()) {
        if (feature.isCategorical()) {
          featureCountForAPrior.put(feature.getCategoricalValue(), 0);
        }
      }
      jointCountsForQueryFeatures.put(option, featureCountForAPrior);
    }

    // get the means of all the numerical features for each prior class yj
    // the nested map maps feature name to a list of all values for the given query feature enum
    Map<String, Map<String, List<Double>>> contVarMeansForQueryFeatures = new HashMap<>();

    for (String option : this.features.get(targetFeature)) {
      Map<String, List<Double>> featureMeans = new HashMap<>();
      // p(xi| yj)
      for (IFeature feature : queryEntry.getFeatures()) {
        if (!feature.isCategorical()) {
          featureMeans.put(feature.getFeatureName(), new ArrayList<>());
        }
      }
      contVarMeansForQueryFeatures.put(option, featureMeans);
    }

    // for every example so far...
    for (IEntry entry : this.examples) {
      // go through each feature in the query ...
      String currentTargetFeatureEnum = this.getTargetFeatureEnum(entry, targetFeature);
      Map<String, Integer> currentFeatureCountForQueryEnum =
              jointCountsForQueryFeatures.get(currentTargetFeatureEnum);
      Map<String, List<Double>> currentNumericalValsForQueryEnum =
              contVarMeansForQueryFeatures.get(currentTargetFeatureEnum);
      for (IFeature queryFeature : queryEntry.getFeatures()) {
        // and check if it appears in the current example and that it is categorical
        if (queryFeature.isCategorical()) {
          for (IFeature entryFeature : entry.getFeatures()) {
            // if it does, update the count
            if (queryFeature.getCategoricalValue().equals(entryFeature.getCategoricalValue())) {
              currentFeatureCountForQueryEnum.replace(entryFeature.getCategoricalValue(),
                      currentFeatureCountForQueryEnum.get(entryFeature.getCategoricalValue()) + 1);
              break;
            }
          }
        }
        // if it is numerical, add it to the list of values
        else {
          for (IFeature entryFeature : entry.getFeatures()) {
            if (queryFeature.getFeatureName().equals(entryFeature.getFeatureName())) {
              currentNumericalValsForQueryEnum.get(queryFeature.getFeatureName()).add(entryFeature.getValue());
            }
          }
        }
      }
    }



    Map<String, Double> logProbs = new HashMap<>();

    for (Map.Entry<String, Map<String, Integer>> entry : jointCountsForQueryFeatures.entrySet()) {
      double current = 0.0;
      Map<String, Integer> featureCounts = entry.getValue();
      Map<String, List<Double>> numericalFeatureVals =
              contVarMeansForQueryFeatures.get(entry.getKey());
      for (Map.Entry<String, Integer> featureCountEntry : featureCounts.entrySet()) {
        double prob =
                (featureCountEntry.getValue() + 1) / (priorCounts.get(entry.getKey())
                        + this.getFeatureDimensionality(featureCountEntry.getKey()));
        current += Math.log(prob);
      }

      for (IFeature queryFeature : queryEntry.getFeatures()) {
        if (!queryFeature.isCategorical()) {
          double queryVal = queryFeature.getValue();
          List<Double> exampleVals = numericalFeatureVals.get(queryFeature.getFeatureName());
          double[] examplesAsArray = this.doubleListAsArray(exampleVals);
          double mean = StatUtils.mean(examplesAsArray);
          double stdDev = Math.sqrt(StatUtils.variance(examplesAsArray));
          double eval = new NormalDistribution(mean, stdDev).density(queryVal);
          current += Math.log(eval);
        }
      }

      logProbs.put(entry.getKey(), current);
    }

    Map.Entry<String, Double> maxEntry = null;

    for (Map.Entry<String, Double> entry : logProbs.entrySet())
    {
      if (maxEntry == null || entry.getValue() > maxEntry.getValue())
      {
        maxEntry = entry;
      }
    }

    this.targetResult = maxEntry.getKey();

  }

  private double[] doubleListAsArray(List<Double> list) {
    double[] res = new double[list.size()];
    for (int i = 0; i < list.size(); i++) {
      res[i] = list.get(i);
    }

    return res;
  }

  private Integer getFeatureDimensionality(String categoricalValue) {
    for (String[] featureEnums : this.features.values()) {
      for (String str : featureEnums) {
        if (str.equals(categoricalValue)) {
          return featureEnums.length;
        }
      }
    }

    // should not reach here
    throw new IllegalArgumentException("Given categoricalValue could not be found.");
  }

  private String getTargetFeatureEnum(IEntry entry, String targetFeature) {
    for (int i = 0; i < entry.getFeatures().size(); i++) {
      if (entry.getFeatures().get(i).equals(targetFeature)) {
        return entry.getFeatures().get(i).getCategoricalValue();
      }
    }

    //shouldn't get here
    throw new IllegalArgumentException("Could not find supplied target feature in the given entry" +
            ".");
  }

  private String returnTargetFeatureIfPossible(IEntry queryEntry) {
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

    return targetFeature;
  }
}
