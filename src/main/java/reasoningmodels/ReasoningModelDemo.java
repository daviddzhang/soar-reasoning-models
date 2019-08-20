package reasoningmodels;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import reasoningmodels.bayesnet.BayesNetDemo;
import reasoningmodels.bayesnet.IRandomVariable;
import reasoningmodels.bayesnet.RandomVariableImpl;
import reasoningmodels.outputhandlers.ReasoningModels;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;

/**
 * 
 */
public class ReasoningModelDemo {
  public static void main(String[] args) throws IOException {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("reasoning-models");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("/agents/reasoning-models-demo" +
            ".soar").getPath());

    ReasoningModels.addReasoningOutputHandlersToAgent(agent, "create",
            "training-ex", "query-handler");

    Identifier il = agent.GetInputLink();

    Identifier create = il.CreateIdWME("init");

    agent.RunSelf(2);

    create.DestroyWME();

    agent.RunSelf(10);

    int num = 0;

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
      System.out.print("How many times do you want to train in total?");
      num = Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      e.printStackTrace();
    }


    // samplers for burglary detection
    BayesNetDemo.SingleVarSample bData = new BayesNetDemo.SingleVarSample(.9, 20);
    BayesNetDemo.SingleVarSample eData = new BayesNetDemo.SingleVarSample(.3, 14);

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

    BayesNetDemo.MultiVarSample aData = new BayesNetDemo.MultiVarSample(alarmProbs, 23);

    IRandomVariable aPlus = new RandomVariableImpl("A", true);
    IRandomVariable aMinus = new RandomVariableImpl("A", false);

    List<IRandomVariable> aPlusList =
            new ArrayList<IRandomVariable>(Collections.singletonList(aPlus));
    List<IRandomVariable> aMinusList =
            new ArrayList<IRandomVariable>(Collections.singletonList(aMinus));

    Map<List<IRandomVariable>, Double> johnProbs = new HashMap<>();

    johnProbs.put(aPlusList, .9);
    johnProbs.put(aMinusList, .1);

    BayesNetDemo.MultiVarSample jData = new BayesNetDemo.MultiVarSample(johnProbs, 4);

    Map<List<IRandomVariable>, Double> maryProbs = new HashMap<>();

    maryProbs.put(aPlusList, .7);
    maryProbs.put(aMinusList, .15);

    BayesNetDemo.MultiVarSample mData = new BayesNetDemo.MultiVarSample(maryProbs, 1);

    Random chooseModel = new Random(0);

    Reader data1File = new FileReader(ReasoningModelDemo.class.getResource("/data1.csv").getPath());
    CSVReader reader1 = new CSVReader(data1File);

    Iterator<String[]> iterator1 = reader1.iterator();
    String[] headers1 = iterator1.next();

    // train sign classifier
    Reader data2File = new FileReader(ReasoningModelDemo.class.getResource("/data2.csv").getPath());
    CSVReader reader2 = new CSVReader(data2File);

    Iterator<String[]> iterator2 = reader2.iterator();

    String[] headers2 = iterator2.next();

    // set up to train randomly between three different models - stops training classifiers if
    // they run out of csv examples
    List<Integer> modelIndices = new ArrayList<>(Arrays.asList(0, 1, 2));

    for (int i = num; i > 0; i--) {
      if (modelIndices.contains(1) && !iterator1.hasNext()) {
        modelIndices.remove((Integer) 1);
      }
      if (modelIndices.contains(2) && !iterator2.hasNext()) {
        modelIndices.remove((Integer) 2);
      }
      int randModel = modelIndices.get(chooseModel.nextInt(modelIndices.size()));
      if (randModel == 0) {
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

        boolean mOccur = mData.sample(jmGivens);
        train.CreateFloatWME("b", booleanToDouble(bOccur));
        train.CreateFloatWME("e", booleanToDouble(eOccur));
        train.CreateFloatWME("a", booleanToDouble(aOccur));
        train.CreateFloatWME("j", booleanToDouble(jOccur));
        train.CreateFloatWME("m", booleanToDouble(mOccur));

        agent.RunSelf(2);
        train.DestroyWME();
      } else if (randModel == 1) {
        Identifier train = il.CreateIdWME("training");
        train.CreateStringWME("name", "play");
        String[] current = iterator1.next();
        for (int j = 0; j < current.length; j++) {
          train.CreateStringWME(headers1[j], current[j]);
        }

        agent.RunSelf(2);
        train.DestroyWME();
      } else {
        Identifier train = il.CreateIdWME("training");
        train.CreateStringWME("name", "sign");
        String[] current = iterator2.next();
        for (int k = 0; k < current.length; k++) {
          train.CreateStringWME(headers2[k], current[k]);
        }

        agent.RunSelf(2);
        train.DestroyWME();
      }
    }

    reader1.close();
    reader2.close();


    Identifier queryAlarm = il.CreateIdWME("query-signal");
    queryAlarm.CreateFloatWME("john", 1.0);
    queryAlarm.CreateFloatWME("mary", 1.0);
    queryAlarm.CreateStringWME("name", "alarm");

    agent.RunSelf(2);
    queryAlarm.DestroyWME();
    agent.RunSelf(2);

    Identifier queryPlay = il.CreateIdWME("query-signal");
    queryPlay.CreateStringWME("outlook", "sunny");
    queryPlay.CreateFloatWME("temp", 74);
    queryPlay.CreateFloatWME("humidity", 71);
    queryPlay.CreateFloatWME("windy", 1.0);
    queryPlay.CreateStringWME("name", "play");

    agent.RunSelf(2);
    queryPlay.DestroyWME();
    agent.RunSelf(2);

    Identifier querySign = il.CreateIdWME("query-signal");
    querySign.CreateStringWME("shape", "square");
    querySign.CreateStringWME("color", "blue");
    querySign.CreateStringWME("name", "sign");

    agent.RunSelf(2);
    querySign.DestroyWME();
    agent.RunSelf(2);

    System.out.println("BAYES NET RESULT: " + agent.GetOutputLink().FindByAttribute("alarm-result"
            , 0).ConvertToFloatElement().GetValue());
    System.out.println("KNN RESULT: " + agent.GetOutputLink().FindByAttribute("play-result"
            , 0).GetValueAsString());
    System.out.println("NAIVE BAYES RESULT: " + agent.GetOutputLink().FindByAttribute("sign-result"
            , 0).GetValueAsString());

    System.out.println(ReasoningModels.printModels());


  }

  private static double booleanToDouble(boolean occurred) {
    if (occurred) {
      return 1.0;
    } else {
      return 0.0;
    }
  }
}
