package reasoningmodels.bayesnet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import reasoningmodels.classifiers.IFeature;

/**
 * This class represents a random variable. It uses a boolean value to indicate whether it occurred
 * or not. Specifics of how certain details are implemented are documented below.
 */
public class RandomVariableImpl implements IRandomVariable, Serializable {
  private final String name;
  private final boolean hasOccurred;

  /**
   * Constructs an instance of a RandomVariableImpl. It requires a name and a boolean value.
   *
   * @param name        of the RV
   * @param hasOccurred whether the RV occurred
   * @throws IllegalArgumentException if name given is null
   */
  public RandomVariableImpl(String name, boolean hasOccurred) throws IllegalArgumentException {
    if (name == null) {
      throw new IllegalArgumentException("Name cannot be null");
    }

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
    if (obj == this) {
      return true;
    }

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

  /**
   * Converts a list of features into its equivalent as a list of random variables. An error is
   * thrown if there are non-boolean features.
   *
   * @param listToConvert the list of features to convert
   * @return the list of features as a list of variables
   * @throws IllegalArgumentException if the feature type is incompatible with random variables or
   *                                  if the list is null
   */
  public static List<IRandomVariable> featureListToVarList(List<IFeature> listToConvert) throws IllegalArgumentException {
    if (listToConvert == null) {
      throw new IllegalArgumentException("Provided list cannot be null");
    }
    List<IRandomVariable> res = new ArrayList<>();

    for (IFeature feature : listToConvert) {
      boolean hasOccured;

      if (feature.getValue() == 1.0) {
        hasOccured = true;
      } else if (feature.getValue() == 0.0) {
        hasOccured = false;
      } else {
        throw new IllegalArgumentException("Features must be boolean features.");
      }

      res.add(new RandomVariableImpl(feature.getFeatureName(), hasOccured));
    }

    return res;
  }

  /**
   * Converts a list of random variables into the corresponding list of names.
   *
   * @param listToConvert list of random variables to convert
   * @return a list of RV names
   * @throws IllegalArgumentException if the list provided is null
   */
  public static List<String> listOfVarsToListOfNames(List<IRandomVariable> listToConvert) {
    if (listToConvert == null) {
      throw new IllegalArgumentException("List provided cannot be null");
    }
    List<String> res = new ArrayList<>();
    for (IRandomVariable var : listToConvert) {
      res.add(var.getName().toUpperCase());
    }
    return res;
  }
}
