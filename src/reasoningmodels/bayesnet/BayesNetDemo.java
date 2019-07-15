package reasoningmodels.bayesnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import sml.Agent;
import sml.Identifier;
import sml.Kernel;

public class BayesNetDemo {
  public static void main(String[] args) throws IOException {

    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("bayes-nets");

    agent.LoadProductions("/Users/davidzhang/javaprograms/EclipseWorkspace/MySMLPropject/bin/soar" +
                    "/agent/multi-nets.soar");

    ArrayList<BayesNet> graphs = new ArrayList<>();
    agent.AddOutputHandler("create-net", BayesNetOutputHandlers.idNetCreation,
            graphs);
    agent.AddOutputHandler("training-ex", BayesNetOutputHandlers.multiTrainingHandler, graphs);
    agent.AddOutputHandler("query-handler", BayesNetOutputHandlers.multiQueryHandler, graphs);


    Identifier il = agent.GetInputLink();

    Identifier create = il.CreateIdWME("create");

    agent.RunSelf(4);

    create.DestroyWME();

    agent.RunSelf(10);

//    validIDs.add(Integer.parseInt(il.FindByAttribute("id", 0).GetValueAsString()));
//    validIDs.add(Integer.parseInt(il.FindByAttribute("id", 1).GetValueAsString()));

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("How many times do you want to train in total?");
    final int num = Integer.parseInt(reader.readLine());

    System.out.print("Did John call? (Y/N)");
    boolean johnVar = false;
    if (reader.readLine().equals("Y")) {
      johnVar = true;
    }

    IRandomVariable johnRandVar = new RandomVariableImpl("J", johnVar);

    System.out.print("Did Mary call? (Y/N)");
    boolean maryVar = false;
    if (reader.readLine().equals("Y")) {
      maryVar = true;
    }

    IRandomVariable maryRandVar = new RandomVariableImpl("M", maryVar);

    System.out.print("Did it rain? (Y/N)");
    boolean rainVar = false;
    if (reader.readLine().equals("Y")) {
      rainVar = true;
    }

    IRandomVariable rainRandVar = new RandomVariableImpl("R", rainVar);

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

    // samplers for burglary detection
    BurglaryData bData = new BurglaryData(3);
    EarthquakeData eData = new EarthquakeData(2);
    AlarmData aData = new AlarmData(6);
    JohnData jData = new JohnData(0);
    MaryData mData = new MaryData(1);

    Random chooseNet = new Random(0);
    int alarmTrains = 0;
    int trafficTrains = 0;

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

        train.CreateStringWME("b-var", bRandVar.toString());
        train.CreateStringWME("e-var", eRandVar.toString());
        train.CreateStringWME("a-var", aRandVar.toString());
        train.CreateStringWME("j-var", jRandVar.toString());
        train.CreateStringWME("m-var", mRandVar.toString());

        agent.RunSelf(1);
        train.DestroyWME();
        alarmTrains++;

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

        train.CreateStringWME("r-var", rRandVar.toString());
        train.CreateStringWME("t-var", tRandVar.toString());
        train.CreateStringWME("l-var", lRandVar.toString());

        agent.RunSelf(1);
        train.DestroyWME();
        trafficTrains++;
      }
    }

    Identifier queryAlarm = il.CreateIdWME("query-signal");
    queryAlarm.CreateStringWME("john", johnRandVar.toString());
    queryAlarm.CreateStringWME("mary", maryRandVar.toString());
    queryAlarm.CreateStringWME("name", "alarm");

    agent.RunSelf(3);

    System.out.println(agent.ExecuteCommandLine("p --depth 10 -t s1"));
    queryAlarm.DestroyWME();
    agent.RunSelf(2);


    Identifier queryTraff = il.CreateIdWME("query-signal");
    queryTraff.CreateStringWME("rain", rainRandVar.toString());
    queryTraff.CreateStringWME("name", "traffic");

    agent.RunSelf(1);
    queryTraff.DestroyWME();
    agent.RunSelf(1);

    for (int i = 0; i < graphs.size(); i++) {
      BayesNet current = graphs.get(i);

      System.out.println("Result for graph " + i + ": " + current.getResult());
    }

    kernel.Shutdown();


    for (int i  = 0; i < graphs.size(); i++) {
      System.out.println("GRAPH " + i);
      System.out.println(graphs.get(i).getNodes().toString());
    }
  }
}
