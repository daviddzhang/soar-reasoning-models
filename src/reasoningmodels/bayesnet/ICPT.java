package reasoningmodels.bayesnet;

import java.util.List;
import java.util.Map;

public interface ICPT {

  String printCPT();

  Map<List<IRandomVariable>, Double> getCPT();

  ICPT toInferenceCPT(String nodeName);

  List<String> variablesInCPT();

  ICPT eliminateExcept(List<String> queryVars);

  void normalize();

  double getQueryVar(List<IRandomVariable> queryList);
}
