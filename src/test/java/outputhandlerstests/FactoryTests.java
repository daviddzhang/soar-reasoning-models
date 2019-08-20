package outputhandlerstests;

import org.junit.jupiter.api.Test;

import reasoningmodels.ReasoningModelDemo;
import reasoningmodels.outputhandlers.ReasoningModelFactory;
import sml.Agent;
import sml.Kernel;
import sml.WMElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for the methods of ReasoningModelFactory.
 */
public class FactoryTests {
  @Test
  public void testCreateNull() {
    assertThrows(IllegalArgumentException.class, () -> {
      ReasoningModelFactory.createModel(null);
    });
  }

  @Test
  public void testCreateBayesNet() {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("test");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("/agents/bn-demo.soar").getPath());
    agent.GetInputLink().CreateIdWME("init");
    agent.RunSelf(3);
    WMElement model =
            agent.GetOutputLink().FindByAttribute("create", 0).ConvertToIdentifier().FindByAttribute(
            "model", 0).ConvertToIdentifier().FindByAttribute("bayes-net", 0);

    assertEquals("[M, [A]\n" +
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
            "]", ReasoningModelFactory.createModel(model).toString());
    kernel.Shutdown();
  }

  @Test
  public void testCreateKNN() {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("test");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("/agents/knn-demo.soar").getPath());
    agent.GetInputLink().CreateIdWME("init");
    agent.RunSelf(3);
    WMElement model =
            agent.GetOutputLink().FindByAttribute("create", 0).ConvertToIdentifier().FindByAttribute(
                    "model", 0).ConvertToIdentifier().FindByAttribute("knn", 0);
    assertEquals("[play, humidity, temp, outlook, windy]\n",
            ReasoningModelFactory.createModel(model).toString());
    kernel.Shutdown();
  }

  @Test
  public void testCreateNB() {
    Kernel kernel = Kernel.CreateKernelInCurrentThread(true);
    Agent agent = kernel.CreateAgent("test");
    agent.LoadProductions(ReasoningModelDemo.class.getResource("/agents/nb-demo.soar").getPath());
    agent.GetInputLink().CreateIdWME("init");
    agent.RunSelf(3);
    WMElement model =
            agent.GetOutputLink().FindByAttribute("create", 0).ConvertToIdentifier().FindByAttribute(
                    "model", 0).ConvertToIdentifier().FindByAttribute("naive-bayes", 0);
    assertEquals("[play, humidity, temp, outlook, windy]\n",
            ReasoningModelFactory.createModel(model).toString());
    kernel.Shutdown();
  }
}
