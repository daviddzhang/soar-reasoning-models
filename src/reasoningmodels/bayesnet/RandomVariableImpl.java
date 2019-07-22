package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.IFeature;

public class RandomVariableImpl implements IRandomVariable {
  private final String name;
  private final boolean hasOccurred;

  public RandomVariableImpl(String name, boolean hasOccurred) {
    this.name = name;
    this.hasOccurred = hasOccurred;
  }

  @Override
  public String toString() {
    if (this.hasOccurred) {
      return "+" + this.name;
    } else {
      return "-" + this.name;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RandomVariableImpl)) {
      return false;
    }
    IRandomVariable test = (IRandomVariable) obj;
    return this.name.equalsIgnoreCase(test.getName()) && this.hasOccurred == test.getHasOccurred();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public boolean getHasOccurred() {
    return this.hasOccurred;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.hasOccurred);
  }

  public static List<IRandomVariable> featureListToVarList(List<IFeature> listToConvert) {
    List<IRandomVariable> res = new ArrayList<>();

    for (IFeature feature : listToConvert) {
      boolean hasOccured;

      if (feature.getValue() == 1.0) {
        hasOccured = true;
      } else if (feature.getValue() == 0.0) {
        hasOccured = false;
      }
      else {
        throw new IllegalArgumentException("Features must be boolean features.");
      }

      res.add(new RandomVariableImpl(feature.getFeatureName(), hasOccured));
    }

    return res;
  }

  public static List<String> listOfVarsToListOfNames(List<IRandomVariable> listToConvert) {
    List<String> res = new ArrayList<>();
    for (IRandomVariable var : listToConvert) {
      res.add(var.getName().substring(1).toUpperCase());
    }
    return res;
  }
}
