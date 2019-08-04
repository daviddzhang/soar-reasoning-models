package reasoningmodels.knn;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import reasoningmodels.IReasoningModel;
import reasoningmodels.ReasoningModelDemo;
import reasoningmodels.outputhandlers.ReasoningModelOutputHandlers;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;

public class KNNDemo {
  public static void main(String[] args) throws IOException {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("knn");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("agents/knn-demo.soar").getPath());

    ReasoningModelOutputHandlers.addReasoningOutputHandlersToAgent(agent, "create", "training-ex"
            , "query-handler");

    Identifier il = agent.GetInputLink();

    Identifier create = il.CreateIdWME("init");

    agent.RunSelf(2);

    create.DestroyWME();

    agent.RunSelf(10);

    System.out.println(agent.ExecuteCommandLine("p --depth 10 -t s1"));


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

    List<IReasoningModel> models = ReasoningModelOutputHandlers.getModels();

    for (int i = 0; i < models.size(); i++) {
      System.out.println("Model: " + i);
      System.out.println(models.get(i).toString());
    }


  }
}
