package reasoningmodels.bayesnet;

import java.util.Objects;

import reasoningmodels.IReasoningModel;

public class RandomVariableImpl implements IRandomVariable {
  private final String name;
  private final boolean hasOccured;

  public RandomVariableImpl(String name, boolean hasOccured) {
    this.name = name;
    this.hasOccured = hasOccured;
  }

  public String toString() {
    if (this.hasOccured) {
      return "+" + this.name;
    }
    else {
      return "-" + this.name;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RandomVariableImpl)) {
      return false;
    }
    IRandomVariable test = (IRandomVariable) obj;
    return this.name.equalsIgnoreCase(test.getName()) && this.hasOccured == test.getHasOccurred();
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public boolean getHasOccurred() {
    return this.hasOccured;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.name, this.hasOccured);
  }
}
