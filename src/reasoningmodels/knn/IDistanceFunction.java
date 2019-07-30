package reasoningmodels.knn;

public interface IDistanceFunction {

  double evaluate(double[] a, double[] b);

  static IDistanceFunction createDistanceFunction(String name) {
    switch (name) {
      case "euclidean":
        return new L2Distance();
      default:
        throw new IllegalArgumentException("Supplied name is not a supported distance function");
    }
  }
}
