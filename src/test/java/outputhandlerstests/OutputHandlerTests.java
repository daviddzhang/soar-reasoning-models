package outputhandlerstests;

import org.junit.jupiter.api.Test;

import java.io.File;

import reasoningmodels.ReasoningModelDemo;
import reasoningmodels.outputhandlers.ReasoningModels;
import sml.Agent;
import sml.Identifier;
import sml.Kernel;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the output handlers found in the ReasoningModels class. These tests are essentially
 * unit test versions of the demos.
 */
public class OutputHandlerTests {
  /**
   * Note - all output handlers are put into one test due to the usage of static variables to
   * maintain the list. Normally, this would be desired behavior, but across tests, we would want
   * to reset the static variables, which cannot be done without access to the field.
   */
  @Test
  public void testOutputHandlers() {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("test");
    agent.LoadProductions(new File(ReasoningModelDemo.class.getResource("/agents/reasoning-models" +
            "-demo.soar").getFile()).getAbsolutePath());
    ReasoningModels.addReasoningOutputHandlersToAgent(agent, "create",
            "training-ex", "query-handler");

    // tests creation output handler
    Identifier init = agent.GetInputLink().CreateIdWME("init");
    agent.RunSelf(2);
    init.DestroyWME();
    agent.RunSelf(10);

    assertEquals("Model: 0\n" +
            "[M, [A]\n" +
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
            ", J, [A]\n" +
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
            ", A, [E, B]\n" +
            "CPT: \n" +
            "[+B, -E]| Probability: 0.0\n" +
            "[-B, +E]| Probability: 0.0\n" +
            "[+B, +E]| Probability: 0.0\n" +
            "[-B, -E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B, -E]| Frequencies: 0\n" +
            "[-B, +E]| Frequencies: 0\n" +
            "[+B, +E]| Frequencies: 0\n" +
            "[-B, -E]| Frequencies: 0\n" +
            ", E, []\n" +
            "CPT: \n" +
            "[+E]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+E]| Frequencies: 0\n" +
            ", B, []\n" +
            "CPT: \n" +
            "[+B]| Probability: 0.0\n" +
            "\n" +
            "Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "\n" +
            "Relative Frequencies: \n" +
            "[+B]| Frequencies: 0\n" +
            "]\n" +
            "Model: 1\n" +
            "[sign, shape, color]\n" +
            "\n" +
            "Model: 2\n" +
            "[play, humidity, temp, outlook, windy]\n" +
            "\n", ReasoningModels.printModels());

    // tests training output handlers
    // training on a graphical model
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
            "[play, humidity, temp, outlook, windy]\n" +
            "\n", ReasoningModels.printModels());

    // training on a flat model
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

    // tests query output handler
    Identifier queryAlarm = agent.GetInputLink().CreateIdWME("query-signal");
    queryAlarm.CreateFloatWME("john", 1.0);
    queryAlarm.CreateFloatWME("mary", 1.0);
    queryAlarm.CreateStringWME("name", "alarm");

    agent.RunSelf(2);
    queryAlarm.DestroyWME();
    agent.RunSelf(2);

    assertEquals(0.0, agent.GetOutputLink().FindByAttribute("alarm-result"
            , 0).ConvertToFloatElement().GetValue());

    kernel.Shutdown();
  }

}
