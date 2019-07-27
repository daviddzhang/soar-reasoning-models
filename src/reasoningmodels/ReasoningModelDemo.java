package reasoningmodels;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import reasoningmodels.bayesnet.IRandomVariable;
import reasoningmodels.bayesnet.RandomVariableImpl;
import reasoningmodels.outputhandlers.ReasoningModelOutputHandlers;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;

import static reasoningmodels.bayesnet.BayesSampler.AlarmData;
import static reasoningmodels.bayesnet.BayesSampler.BurglaryData;
import static reasoningmodels.bayesnet.BayesSampler.EarthquakeData;
import static reasoningmodels.bayesnet.BayesSampler.JohnData;
import static reasoningmodels.bayesnet.BayesSampler.MaryData;


public class ReasoningModelDemo {
  public static void main(String[] args) throws IOException {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("bayes-nets");
    agent.LoadProductions("/Users/davidzhang/javaprograms/ResearchProjects/SoarReasoningModels" +
            "/src/reasoningmodels/agents/reasoning-models-demo.soar");

    ReasoningModelOutputHandlers.addReasoningOutputHandlersToAgent(agent, "create", "training-ex"
            , "query-handler");

    Identifier il = agent.GetInputLink();

    Identifier create = il.CreateIdWME("init");

    agent.RunSelf(2);

    create.DestroyWME();

    agent.RunSelf(10);

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("How many times do you want to train in total?");
    final int num = Integer.parseInt(reader.readLine());

    // samplers for burglary detection
    BurglaryData bData = new BurglaryData(3);
    EarthquakeData eData = new EarthquakeData(2);
    AlarmData aData = new AlarmData(6);
    JohnData jData = new JohnData(0);
    MaryData mData = new MaryData(1);

    Random chooseModel = new Random(0);

    Reader data1File = new FileReader("/Users/davidzhang/Downloads/data1.csv");
    CSVReader reader1 = new CSVReader(data1File);

    Iterator<String[]> iterator1 = reader1.iterator();
    String[] headers1 = iterator1.next();

    // train sign classifier
    Reader data2File = new FileReader("/Users/davidzhang/Downloads/data2.csv");
    CSVReader reader2 = new CSVReader(data2File);

    Iterator<String[]> iterator2 = reader2.iterator();

    String[] headers2 = iterator2.next();

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
      else if (randModel == 1) {
        Identifier train = il.CreateIdWME("training");
        train.CreateStringWME("name", "play");
        String[] current = iterator1.next();
        for (int j = 0; j < current.length; j++) {
          train.CreateStringWME(headers1[j], current[j]);
        }

        agent.RunSelf(1);
        train.DestroyWME();
      }
      else {
        Identifier train = il.CreateIdWME("training");
        train.CreateStringWME("name", "sign");
        String[] current = iterator2.next();
        for (int k = 0; k < current.length; k++) {
          train.CreateStringWME(headers2[k], current[k]);
        }

        agent.RunSelf(1);
        train.DestroyWME();
      }
    }

    Identifier queryAlarm = il.CreateIdWME("query-signal");
    queryAlarm.CreateFloatWME("john", 1.0);
    queryAlarm.CreateFloatWME("mary", 1.0);
    queryAlarm.CreateStringWME("name", "alarm");

    agent.RunSelf(3);
    queryAlarm.DestroyWME();
    agent.RunSelf(2);

    Identifier queryPlay = il.CreateIdWME("query-signal");
    queryPlay.CreateStringWME("outlook", "sunny");
    queryPlay.CreateFloatWME("temp", 74);
    queryPlay.CreateFloatWME("humidity", 71);
    queryPlay.CreateFloatWME("windy", 1.0);
    queryPlay.CreateStringWME("name", "play");

    agent.RunSelf(3);
    queryPlay.DestroyWME();
    agent.RunSelf(2);

    Identifier querySign = il.CreateIdWME("query-signal");
    querySign.CreateStringWME("shape", "square");
    querySign.CreateStringWME("color", "blue");
    querySign.CreateStringWME("name", "sign");

    agent.RunSelf(3);
    querySign.DestroyWME();
    agent.RunSelf(2);

    List<IReasoningModel> models = ReasoningModelOutputHandlers.getModels();

    for (int i = 0; i < models.size(); i++) {
      System.out.println("Model: " + i);
      System.out.println(models.get(i).toString());
    }


  }

  private static double booleanToDouble(boolean occurred) {
    if (occurred) {
      return 1.0;
    }
    else {
      return 0.0;
    }
  }
}
