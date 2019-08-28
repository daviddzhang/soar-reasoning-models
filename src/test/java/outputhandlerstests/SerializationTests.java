package outputhandlerstests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import reasoningmodels.IReasoningModel;
import reasoningmodels.ReasoningModelDemo;
import reasoningmodels.bayesnet.BayesNet;
import reasoningmodels.bayesnet.INode;
import reasoningmodels.bayesnet.NodeImpl;
import reasoningmodels.knn.KNN;
import reasoningmodels.naivebayes.NaiveBayes;
import reasoningmodels.outputhandlers.ReasoningModels;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the methods used when serializing and deserializing the models.
 */
public class SerializationTests {

  @BeforeAll
  public static void init() {
    try {
      Field models = ReasoningModels.class.getDeclaredField("models");
      models.setAccessible(true);
      models.set(null, new ArrayList<IReasoningModel>());
    } catch (Exception e) {
      e.printStackTrace();
    }

    IReasoningModel bayes = new BayesNet();
    INode a = new NodeImpl("A", new ArrayList<>());
    INode b = new NodeImpl("B", new ArrayList<>(Collections.singletonList("A")));
    bayes.parameterizeWithGraphicalFeatures(new ArrayList<>(Arrays.asList(a, b)));

    Map<String, String[]> features = new HashMap<>();
    features.put("Boolean", null);
    features.put("Numerical", null);
    features.put("Target", new String[]{"val1", "val2"});
    IReasoningModel knn = new KNN("Target");
    knn.parameterizeWithFlatFeatures(features);

    IReasoningModel nb = new NaiveBayes("Target");
    nb.parameterizeWithFlatFeatures(features);

    Serializable userData = new ArrayList<>(Arrays.asList(bayes, knn, nb));

    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("test");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("/agents/reasoning-models-demo" +
            ".soar").getPath());
    ReasoningModels.addReasoningOutputHandlersToAgent(agent, userData, "create",
            "training-ex", "query-handler");

    // tests creation output handler
    Identifier init = agent.GetInputLink().CreateIdWME("init");
    agent.RunSelf(2);
    init.DestroyWME();
    agent.RunSelf(10);

    Identifier train = agent.GetInputLink().CreateIdWME("training-vars");
    train.CreateStringWME("name", "alarm");
    train.CreateFloatWME("b", 1.0);
    train.CreateFloatWME("e", 0.0);
    train.CreateFloatWME("a", 1.0);
    train.CreateFloatWME("j", 1.0);
    train.CreateFloatWME("m", 0.0);

    agent.RunSelf(2);
    train.DestroyWME();
    agent.RunSelf(2);

    Identifier trainFlat = agent.GetInputLink().CreateIdWME("training");
    trainFlat.CreateStringWME("name", "play");
    trainFlat.CreateStringWME("outlook", "sunny");
    trainFlat.CreateStringWME("windy", "FALSE");
    trainFlat.CreateStringWME("temp", "87");
    trainFlat.CreateStringWME("humidity", "85");
    trainFlat.CreateStringWME("play", "yes");

    agent.RunSelf(2);
    trainFlat.DestroyWME();
    agent.RunSelf(2);

    kernel.Shutdown();
  }

  @Test
  public void testSerializationAndDeserialization() {
    assertEquals("Model: 0\n" +
            "[M, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 0\n" +
            ", J, [A]\n" +
            "CPT: \n" +
            "[-A]| Probability: 0.0\n" +
            "[+A]| Probability: 1.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[-A]| Frequencies: 0\n" +
            "[+A]| Frequencies: 1\n" +
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 1.0\n" +
            "[-B, +E]| Probability: 0.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 1\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 1\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 1.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 1\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 1\n" +
            "]\n" +
            "Model: 1\n" +
            "[sign, shape, color]\n" +
            "\n" +
            "Model: 2\n" +
            "[play, outlook, windy, humidity, temp]\n" +
            "[yes, sunny, FALSE, 85.0, 87.0]\n" +
            "\n", ReasoningModels.printModels());
    try {
      File test = new File("src/test/resources/test.ser");
      ReasoningModels.serialize(test.getAbsolutePath());
      assertEquals("[[A, []\n" +
              "CPT: \n" +
              "[+A]| Probability: 0.0\n" +
              "\n" +
              "Frequencies: \n" +
              "[+A]| Frequencies: 0\n" +
              "\n" +
              "Relative Frequencies: \n" +
              "[+A]| Frequencies: 0\n" +
              ", B, [A]\n" +
              "CPT: \n" +
              "[-A]| Probability: 0.0\n" +
              "[+A]| Probability: 0.0\n" +
              "\n" +
              "Frequencies: \n" +
              "[-A]| Frequencies: 0\n" +
              "[+A]| Frequencies: 0\n" +
              "\n" +
              "Relative Frequencies: \n" +
              "[-A]| Frequencies: 0\n" +
              "[+A]| Frequencies: 0\n" +
              "], [Target, Numerical, Boolean]\n" +
              ", [Target, Numerical, Boolean]\n" +
              "]", ReasoningModels.deserialize(test.getAbsolutePath()).toString());
      assertEquals("Model: 0\n" +
              "[M, [A]\n" +
              "CPT: \n" +
              "[-A]| Probability: 0.0\n" +
              "[+A]| Probability: 0.0\n" +
              "\n" +
              "Frequencies: \n" +
              "[-A]| Frequencies: 0\n" +
              "[+A]| Frequencies: 1\n" +
              "\n" +
              "Relative Frequencies: \n" +
              "[-A]| Frequencies: 0\n" +
              "[+A]| Frequencies: 0\n" +
              ", J, [A]\n" +
              "CPT: \n" +
              "[-A]| Probability: 0.0\n" +
              "[+A]| Probability: 1.0\n" +
              "\n" +
              "Frequencies: \n" +
              "[-A]| Frequencies: 0\n" +
              "[+A]| Frequencies: 1\n" +
              "\n" +
              "Relative Frequencies: \n" +
              "[-A]| Frequencies: 0\n" +
              "[+A]| Frequencies: 1\n" +
              ", A, [E, B]\n" +
              "CPT: \n" +
              "[+B, -E]| Probability: 1.0\n" +
              "[-B, +E]| Probability: 0.0\n" +
              "[+B, +E]| Probability: 0.0\n" +
              "[-B, -E]| Probability: 0.0\n" +
              "\n" +
              "Frequencies: \n" +
              "[+B, -E]| Frequencies: 1\n" +
              "[-B, +E]| Frequencies: 0\n" +
              "[+B, +E]| Frequencies: 0\n" +
              "[-B, -E]| Frequencies: 0\n" +
              "\n" +
              "Relative Frequencies: \n" +
              "[+B, -E]| Frequencies: 1\n" +
              "[-B, +E]| Frequencies: 0\n" +
              "[+B, +E]| Frequencies: 0\n" +
              "[-B, -E]| Frequencies: 0\n" +
              ", E, []\n" +
              "CPT: \n" +
              "[+E]| Probability: 0.0\n" +
              "\n" +
              "Frequencies: \n" +
              "[+E]| Frequencies: 1\n" +
              "\n" +
              "Relative Frequencies: \n" +
              "[+E]| Frequencies: 0\n" +
              ", B, []\n" +
              "CPT: \n" +
              "[+B]| Probability: 1.0\n" +
              "\n" +
              "Frequencies: \n" +
              "[+B]| Frequencies: 1\n" +
              "\n" +
              "Relative Frequencies: \n" +
              "[+B]| Frequencies: 1\n" +
              "]\n" +
              "Model: 1\n" +
              "[shape, color, sign]\n" +
              "\n" +
              "Model: 2\n" +
              "[play, outlook, windy, humidity, temp]\n" +
              "[yes, sunny, FALSE, 85.0, 87.0]\n" +
              "\n", ReasoningModels.printModels());
    } catch (IOException e) {
      throw new RuntimeException("File could not be written or loaded");
    }
  }

}
