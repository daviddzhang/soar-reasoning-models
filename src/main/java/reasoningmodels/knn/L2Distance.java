package reasoningmodels.knn;

import org.apache.commons.math3.ml.distance.EuclideanDistance;

/**
 * Represents L2 or Euclidean distance. Evaluates distance by euclidean calculations.
 */
public class L2Distance implements IDistanceFunction {
  EuclideanDistance delegate = new EuclideanDistance();

  /**
   * May throw an exception belonging to EuclideanDistance if inputs are invalid (such as
   * differing sizes).
   */
  @Override
  public double evaluate(double[] a, double[] b) {
    return delegate.compute(a, b);
  }
}
