package reasoningmodels.naivebayes;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;

import reasoningmodels.ReasoningModelDemo;
import reasoningmodels.outputhandlers.ReasoningModels;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;

/**
 * A main method that runs a demonstration of the NB model through a Soar agent. Similarly to the
 * KNN demo, it reads in the same CSV data to produce a result. See ReasoningModelDemo for query
 * results.
 */
public class NBDemo {
  public static void main(String[] args) throws IOException {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("nb");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("agents/nb-demo.soar").getPath());

    ReasoningModels.addReasoningOutputHandlersToAgent(agent, "create", "training-ex"
            , "query-handler");

    Identifier il = agent.GetInputLink();

    Identifier create = il.CreateIdWME("init");

    agent.RunSelf(2);

    create.DestroyWME();

    agent.RunSelf(10);

    // train play classifier
    Reader data1File = new FileReader("/Users/davidzhang/Downloads/data1.csv");
    CSVReader reader1 = new CSVReader(data1File);

    Iterator<String[]> iterator1 = reader1.iterator();
    String[] headers1 = iterator1.next();
    while (iterator1.hasNext()) {
      Identifier train = il.CreateIdWME("training");
      train.CreateStringWME("name", "play");
      String[] current = iterator1.next();
      for (int i = 0; i < current.length; i++) {
        train.CreateStringWME(headers1[i], current[i]);
      }

      agent.RunSelf(1);
      train.DestroyWME();
    }

    // train sign classifier
    Reader data2File = new FileReader("/Users/davidzhang/Downloads/data2.csv");
    CSVReader reader2 = new CSVReader(data2File);

    Iterator<String[]> iterator2 = reader2.iterator();

    String[] headers2 = iterator2.next();
    while (iterator2.hasNext()) {
      Identifier train = il.CreateIdWME("training");
      train.CreateStringWME("name", "sign");
      String[] current = iterator2.next();
      for (int i = 0; i < current.length; i++) {
        train.CreateStringWME(headers2[i], current[i]);
      }

      agent.RunSelf(1);
      train.DestroyWME();
    }

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

    System.out.println(ReasoningModels.printModels());

  }
}
