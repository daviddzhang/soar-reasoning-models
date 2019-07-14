package reasoningmodels.bayesnet;

public interface IRandomVariable {

  String toString();

  boolean equals(Object other);

  String getName();

  boolean getHasOccurred();

  int hashCode();
}
