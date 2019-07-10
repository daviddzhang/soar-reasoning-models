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
      ArrayList<Graph> graphs = (ArrayList<Graph>) data;
      ArrayList<Node> nodesInNewGraph = new ArrayList<Node>();

      Utils util = new Utils();

      WMElement graphWME = pWmeAdded.ConvertToIdentifier().FindByAttribute("graph", 0);

      WMElement nodes = graphWME.ConvertToIdentifier().FindByAttribute("nodes", 0);
      int edgeCount = util.numEdges(graphWME);

      // creates the graph based on the edge WMEs and node attributes
      for (int i = 0; i < nodes.ConvertToIdentifier().GetNumberChildren(); i++) {
        WMElement aNode = nodes.ConvertToIdentifier().GetChild(i);
        ArrayList<String> parents = new ArrayList<String>();
        for (int j = 0; j < edgeCount; j++) {
          WMElement from = graphWME.ConvertToIdentifier().FindByAttribute("edge", j)
                  .ConvertToIdentifier().GetChild(1).ConvertToIdentifier().GetChild(0);
          WMElement to = graphWME
                  .ConvertToIdentifier().FindByAttribute("edge", j)
                  .ConvertToIdentifier().GetChild(0).ConvertToIdentifier().GetChild(0);

          if (to.GetValueAsString().equals(aNode.GetValueAsString())) {
            parents.add(from.GetAttribute());
          }
        }

        Node currentNode = new Node(aNode.GetAttribute(), parents);
        // System.out.println(currentNode.name + "," + currentNode.parents);
        nodesInNewGraph.add(currentNode);

      }
      Graph newGraph = new Graph(nodesInNewGraph);
      graphs.add(newGraph);

      pWmeAdded.ConvertToIdentifier().CreateIntWME("id", graphs.size() - 1);
    }

  };
}
