package reasoningmodels.bayesnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import reasoningmodels.ReasoningModelDemo;
import reasoningmodels.outputhandlers.ReasoningModels;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;

/**
 * A class that contains a main method to run a demo for Bayes Nets using a soar agent. It
 * constructs two different bayes nets - alarm and traffic. User must input number of times to
 * train and whether the target variables happened or not, then the models will be printed. To
 * see query result, please see the ReasoningModelDemo demo.
 */
public class BayesNetDemo {
  public static void main(String[] args) throws IOException {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("bayes-nets");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("agents/bn-demo.soar").getPath());

    ReasoningModels.addReasoningOutputHandlersToAgent(agent, "create", "training-ex"
            , "query-handler");

    Identifier il = agent.GetInputLink();

    Identifier create = il.CreateIdWME("init");

    agent.RunSelf(2);

    create.DestroyWME();

    agent.RunSelf(10);

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("How many times do you want to train in total?");
    final int num = Integer.parseInt(reader.readLine());

    System.out.print("Did John call? (Y/N)");
    double johnVar = 0.0;
    if (reader.readLine().equals("Y")) {
      johnVar = 1.0;
    }


    System.out.print("Did Mary call? (Y/N)");
    double maryVar = 0.0;
    if (reader.readLine().equals("Y")) {
      maryVar = 1.0;
    }


    System.out.print("Did it rain? (Y/N)");
    double rainVar = 0.0;
    if (reader.readLine().equals("Y")) {
      rainVar = 1.0;
    }

    // Initializing sampler for each node in the net for traffic training
    SingleVarSample rData = new SingleVarSample(.3, 0);

    HashMap<List<IRandomVariable>, Double> trafficHM = new HashMap<>();
    IRandomVariable rPlus = new RandomVariableImpl("R", true);
    IRandomVariable rMinus = new RandomVariableImpl("R", false);
    trafficHM.put(new ArrayList<>(Arrays.asList(rPlus)), .8);
    trafficHM.put(new ArrayList<>(Arrays.asList(rMinus)), .3);

    MultiVarSample tData = new MultiVarSample(trafficHM, 1);

    HashMap<List<IRandomVariable>, Double> lateHM = new HashMap<>();
    IRandomVariable tPlus = new RandomVariableImpl("T", true);
    IRandomVariable tMinus = new RandomVariableImpl("T", false);
    lateHM.put(new ArrayList<>(Arrays.asList(tPlus)), .75);
    lateHM.put(new ArrayList<>(Arrays.asList(tMinus)), .1);

    MultiVarSample lData = new MultiVarSample(lateHM, 2);

    // Initializing sampler for each node in the net for alarm training

    SingleVarSample bData = new SingleVarSample(.9, 20);
    SingleVarSample eData = new SingleVarSample(.3, 14);

    IRandomVariable eVarPlus = new RandomVariableImpl("E", true);
    IRandomVariable eVarMinus = new RandomVariableImpl("E", false);
    IRandomVariable bVarPlus = new RandomVariableImpl("B", true);
    IRandomVariable bVarMinus = new RandomVariableImpl("B", false);

    List<IRandomVariable> ePlusBPlus = new ArrayList<>(Arrays.asList(eVarPlus, bVarPlus));
    List<IRandomVariable> eMinusBPlus = new ArrayList<>(Arrays.asList(eVarMinus, bVarPlus));
    List<IRandomVariable> eMinusBMinus = new ArrayList<>(Arrays.asList(eVarMinus, bVarMinus));
    List<IRandomVariable> ePlusBMinus = new ArrayList<>(Arrays.asList(eVarPlus, bVarMinus));

    Map<List<IRandomVariable>, Double> alarmProbs = new HashMap<>();

    alarmProbs.put(ePlusBPlus, .99);
    alarmProbs.put(eMinusBPlus, .9);
    alarmProbs.put(eMinusBMinus, .1);
    alarmProbs.put(ePlusBMinus, .15);

    MultiVarSample aData = new MultiVarSample(alarmProbs, 23);

    IRandomVariable aPlus = new RandomVariableImpl("A", true);
    IRandomVariable aMinus = new RandomVariableImpl("A", false);

    List<IRandomVariable> aPlusList = new ArrayList<IRandomVariable>(Collections.singletonList(aPlus));
    List<IRandomVariable> aMinusList = new ArrayList<IRandomVariable>(Collections.singletonList(aMinus));

    Map<List<IRandomVariable>, Double> johnProbs = new HashMap<>();

    johnProbs.put(aPlusList, .9);
    johnProbs.put(aMinusList, .1);

    MultiVarSample jData = new MultiVarSample(johnProbs, 4);

    Map<List<IRandomVariable>, Double> maryProbs = new HashMap<>();

    maryProbs.put(aPlusList, .7);
    maryProbs.put(aMinusList, .15);

    MultiVarSample mData = new MultiVarSample(maryProbs, 1);

    Random chooseNet = new Random(0);

    // trains the net based on sampling probability distributions
    for (int i = num; i > 0; i--) {
      if (chooseNet.nextInt(2) == 0) {

        Identifier train = il.CreateIdWME("training-vars");
        train.CreateStringWME("name", "alarm");

        boolean bOccur = bData.sample();
        IRandomVariable bRandVar = new RandomVariableImpl("B", bOccur);

        boolean eOccur = eData.sample();
        IRandomVariable eRandVar = new RandomVariableImpl("E", eOccur);

        ArrayList<IRandomVariable> alarmGivens = new ArrayList<IRandomVariable>(
                Arrays.asList(eRandVar, bRandVar));
        boolean aOccur = aData.sample(alarmGivens);
        IRandomVariable aRandVar = new RandomVariableImpl("A", aOccur);

        ArrayList<IRandomVariable> jmGivens =
                new ArrayList<IRandomVariable>(Arrays.asList(aRandVar));

        boolean jOccur = jData.sample(jmGivens);
        IRandomVariable jRandVar = new RandomVariableImpl("J", jOccur);

        boolean mOccur = mData.sample(jmGivens);
        IRandomVariable mRandVar = new RandomVariableImpl("M", mOccur);

        train.CreateFloatWME("b", booleanToDouble(bOccur));
        train.CreateFloatWME("e", booleanToDouble(eOccur));
        train.CreateFloatWME("a", booleanToDouble(aOccur));
        train.CreateFloatWME("j", booleanToDouble(jOccur));
        train.CreateFloatWME("m", booleanToDouble(mOccur));

        agent.RunSelf(1);
        train.DestroyWME();
      }
      else {
        Identifier train = il.CreateIdWME("training-vars");
        train.CreateStringWME("name", "traffic");

        boolean rOccur = rData.sample();
        IRandomVariable rRandVar = new RandomVariableImpl("R", rOccur);

        ArrayList<IRandomVariable> trafficGivens = new ArrayList<IRandomVariable>(
                Arrays.asList(rRandVar));
        boolean tOccur = tData.sample(trafficGivens);
        IRandomVariable tRandVar = new RandomVariableImpl("T", tOccur);

        ArrayList<IRandomVariable> lGivens =
                new ArrayList<IRandomVariable>(Arrays.asList(tRandVar));

        boolean lOccur = lData.sample(lGivens);
        IRandomVariable lRandVar = new RandomVariableImpl("L", lOccur);

        train.CreateFloatWME("r", booleanToDouble(rOccur));
        train.CreateFloatWME("t", booleanToDouble(tOccur));
        train.CreateFloatWME("l", booleanToDouble(lOccur));

        agent.RunSelf(1);
        train.DestroyWME();
      }
    }

    Identifier queryAlarm = il.CreateIdWME("query-signal");
    queryAlarm.CreateFloatWME("john", johnVar);
    queryAlarm.CreateFloatWME("mary", maryVar);
    queryAlarm.CreateStringWME("name", "alarm");

    agent.RunSelf(3);

    queryAlarm.DestroyWME();
    agent.RunSelf(2);

    Identifier queryTraff = il.CreateIdWME("query-signal");
    queryTraff.CreateFloatWME("rain", rainVar);
    queryTraff.CreateStringWME("name", "traffic");

    agent.RunSelf(1);
    queryTraff.DestroyWME();
    agent.RunSelf(1);



    kernel.Shutdown();

    System.out.println(ReasoningModels.printModels());
  }

  private static double booleanToDouble(boolean occurred) {
    if (occurred) {
      return 1.0;
    }
    else {
      return 0.0;
    }
  }

  /**
   * A single variable sampler. Returns true based on the supplied occurrence rate.
   */
  public static class SingleVarSample {
    Random rand;
    double occurrence;

    public SingleVarSample(double occurrence) {
      this.occurrence = occurrence;
      this.rand = new Random();
    }

    public SingleVarSample(double occurrence, int rand) {
      this.occurrence = occurrence;
      this.rand = new Random(rand);
    }

    public boolean sample() {
      double random = rand.nextDouble();

      if (random <= this.occurrence) {
        return true;
      } else {
        return false;
      }
    }
  }

  /**
   * A multi variable sampler. Returns true based on the supplied probability table and the given
   * list of variables that occurred.
   */
  public static class MultiVarSample {
    Random rand;
    Map<List<IRandomVariable>, Double> prob;

    public MultiVarSample(Map<List<IRandomVariable>, Double> prob) {
      this.prob = prob;
      this.rand = new Random();
    }

    public MultiVarSample(Map<List<IRandomVariable>, Double> prob, int rand) {
      this.prob = prob;
      this.rand = new Random(rand);
    }

    public boolean sample(List<IRandomVariable> givens) {
      double probForGiven = this.prob.get(givens);
      double random = this.rand.nextDouble();

      if (random <= probForGiven) {
        return true;
      } else {
        return false;
      }

    }
  }

}
