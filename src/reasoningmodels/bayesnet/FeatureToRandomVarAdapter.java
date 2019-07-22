package reasoningmodels.bayesnet;

import java.util.List;

import reasoningmodels.classifiers.IFeature;

public class FeatureToRandomVarAdapter implements IRandomVariable {
  private final IFeature adaptee;

  public FeatureToRandomVarAdapter(IFeature adaptee) {
    this.adaptee = adaptee;
  }

  private boolean valToHasOccured() {
    if (this.adaptee.getValue() == 1.0) {
      return true;
    }
    else if (this.adaptee.getValue() == 0.0) {
      return false;
    }
    else {
      throw new IllegalArgumentException("Adaptee must be a boolean feature.");
    }
  }

  @Override
  public String toString() {
    if (this.valToHasOccured()) {
      return "+" + this.adaptee.getFeatureName();
    } else {
      return "-" + this.adaptee.getFeatureName();
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
    return this.adaptee.getFeatureName().equalsIgnoreCase(test.getName()) && this.valToHasOccured() == test.getHasOccurred();
  }


  @Override
  public String getName() {
    return adaptee.getFeatureName();
  }

  @Override
  public boolean getHasOccurred() {
    return this.valToHasOccured();
  }
}
