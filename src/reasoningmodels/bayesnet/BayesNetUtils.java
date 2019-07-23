package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.List;

import sml.WMElement;

public class BayesNetUtils {
  // removes the duplicates from a list
  static <T> List<T> removeDuplicates(List<T> list) {
    List<T> filteredList = new ArrayList<T>();

    for (T element : list) {
      if (!filteredList.contains(element)) {

        filteredList.add(element);
      }
    }
    return filteredList;
  }

  // converts a string to a random variable
  static IRandomVariable stringToVar(String varString) {
    if (varString.length() != 2
            && (!varString.substring(0, 1).equals("+") || !varString.substring(0, 1).equals("-"))) {
      throw new IllegalArgumentException("String in wrong format");
    }
    if (varString.substring(0, 1).equals("+")) {
      return new RandomVariableImpl(varString.substring(1).toUpperCase(), true);
    }
    else {
      return new RandomVariableImpl(varString.substring(1).toUpperCase(), false);
    }
  }

  // gets the number of edges given a WME for a graph
  static int numEdges(WMElement wme) {
    int edgeCount = 0;

    for (int i = 0; i < wme.ConvertToIdentifier().GetNumberChildren(); i++) {
      if (wme.ConvertToIdentifier().GetChild(i).GetAttribute().equals("edge")) {
        edgeCount++;
      }
    }
    return edgeCount;
  }
}
