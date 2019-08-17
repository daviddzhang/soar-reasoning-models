package reasoningmodels.knn;

/**
 * This interface represents a distance function to be used during KNN calculations. Implementing
 * classes must implement an evaluation method.
 */
public interface IDistanceFunction {

  /**
   * Evaluates the distance between the two vectors in whatever way the implementing class does so.
   *
   * @param a one vector of values
   * @param b other vector of values
   * @return the distance between the two
   */
  double evaluate(double[] a, double[] b);

  /**
   * Constructs an instance of an IDistanceFunction based on the supplied name.
   *
   * @param name of the distance function
   * @return the corresponding distance function
   */
  static IDistanceFunction createDistanceFunction(String name) {
    switch (name) {
      case "euclidean":
        return new L2Distance();
      default:
        throw new IllegalArgumentException("Supplied name is not a supported distance function");
    }
  }
}
