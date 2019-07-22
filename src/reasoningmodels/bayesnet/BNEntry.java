package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.List;

import reasoningmodels.classifiers.IEntry;
import reasoningmodels.classifiers.IFeature;

public class BNEntry implements IEntry {
  private final List<IFeature> vars;

  public BNEntry(List<IFeature> vars) {
    this.vars = vars;
  }


  @Override
  public List<IFeature> getFeatures() {
    return new ArrayList<>(this.vars);
  }

  @Override
  public boolean containsFeature(String feature) {
    return false;
  }
}
