package reasoningmodels.knn;

import com.opencsv.CSVReader;

import java.io.File;
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
 * A main method that runs a demonstration of the KNN model with a Soar agent. Reads in CSV data
 * for two problems (sign of the shape and whether to play or not) - files are located in the
 * resources package. To see query results, see ReasoningModelDemo.
 */
public class KNNDemo {
  public static void main(String[] args) throws IOException {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("knn");
    agent.LoadProductions(
            new File(ReasoningModelDemo.class.getResource("/agents/knn-demo.soar").getFile())
                    .getAbsolutePath());

    ReasoningModels.addReasoningOutputHandlersToAgent(agent, "create", "training-ex"
            , "query-handler");

    Identifier il = agent.GetInputLink();

    Identifier create = il.CreateIdWME("init");

    agent.RunSelf(2);

    create.DestroyWME();

    agent.RunSelf(10);

    // train play classifier
    Reader data1File = new FileReader(ReasoningModelDemo.class.getResource("/data1.csv").getPath());
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
    Reader data2File =
            new FileReader(ReasoningModelDemo.class.getResource("/data2.csv").getPath());
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

    kernel.Shutdown();
  }
}
