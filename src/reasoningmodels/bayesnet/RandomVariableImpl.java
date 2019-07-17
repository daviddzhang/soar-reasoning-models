package reasoningmodels.bayesnet;

import java.util.Objects;

import reasoningmodels.IReasoningModel;
import reasoningmodels.classifiers.BooleanFeature;
import reasoningmodels.classifiers.IFeature;

public class RandomVariableImpl implements IRandomVariable {
  private final String name;
  private final boolean hasOccurred;
  private final IFeature adaptee;

  public RandomVariableImpl(String name, boolean hasOccurred) {
    this.name = name;
    this.hasOccurred = hasOccurred;
    this.adaptee = new BooleanFeature(this.name, this.hasOccurredToVal());
  }

  private double hasOccurredToVal() {
    if (this.hasOccurred) {
      return 1.0;
    }
    else {
      return 0.0;
    }
  }

  @Override
  public boolean isCategorical() {
    return this.adaptee.isCategorical();
  }

  @Override
  public double[] getValueAsVector() {
    return this.adaptee.getValueAsVector();
  }

  @Override
  public double getValue() throws UnsupportedOperationException {
    return this.adaptee.getValue();
  }

  @Override
  public String getFeatureName() {
    return this.adaptee.getFeatureName();
  }

  @Override
  public String getCategoricalValue() throws UnsupportedOperationException {
    return this.adaptee.getCategoricalValue();
  }

  @Override
  public void scaleFeature(double max, double min) {
    this.adaptee.scaleFeature(max, min);
  }

  @Override
  public double getScaledValue() throws UnsupportedOperationException {
    return this.adaptee.getScaledValue();
  }

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
}
