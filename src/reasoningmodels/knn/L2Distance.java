package reasoningmodels.knn;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

public class L2Distance implements IDistanceFunction {
  EuclideanDistance delegate = new EuclideanDistance();

  @Override
  public double evaluate(double[] a, double[] b) {
    return delegate.compute(a, b);
  }
}
