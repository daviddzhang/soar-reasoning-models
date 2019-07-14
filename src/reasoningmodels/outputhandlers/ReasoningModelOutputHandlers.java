package reasoningmodels.outputhandlers;


import sml.Agent;
import sml.Agent.OutputEventInterface;
import sml.Identifier;
import sml.Kernel;
import sml.WMElement;
public class ReasoningModelOutputHandlers {

  public static final OutputEventInterface createModel = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {

    }

  };
}
