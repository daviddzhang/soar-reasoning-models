package reasoningmodels.bayesnet;

import java.util.ArrayList;
import java.util.List;

import sml.Agent.OutputEventInterface;
import sml.WMElement;

public class BayesNetOutputHandlers {

  public static final OutputEventInterface idNetCreation = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      ArrayList<BayesNet> graphs = (ArrayList<BayesNet>) data;
      ArrayList<INode> nodesInNewGraph = new ArrayList<>();

      WMElement graphWME = pWmeAdded.ConvertToIdentifier().FindByAttribute("graph", 0);
      // String graphName = pWmeAdded.ConvertToIdentifier().FindByAttribute("name",
      // 0).GetValueAsString();

      WMElement nodes = graphWME.ConvertToIdentifier().FindByAttribute("nodes", 0);
      int edgeCount = BayesNetUtils.numEdges(graphWME);

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

        INode currentNode = new NodeImpl(aNode.GetAttribute(), parents);
        // System.out.println(currentNode.name + "," + currentNode.parents);
        nodesInNewGraph.add(currentNode);

      }
      BayesNet newGraph = new BayesNet(nodesInNewGraph);
      graphs.add(newGraph);

      pWmeAdded.ConvertToIdentifier().CreateIntWME("id", graphs.size() - 1);
    }

  };

  public static final OutputEventInterface multiTrainingHandler = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      ArrayList<BayesNet> graphs = (ArrayList<BayesNet>) data;

      int graphID = Integer
              .parseInt(pWmeAdded.ConvertToIdentifier().FindByAttribute("id", 0).GetValueAsString());
      BayesNet updatedGraph = graphs.get(graphID);

      ArrayList<IRandomVariable> trainingExample = new ArrayList<>();
      for (int i = 0; i < pWmeAdded.ConvertToIdentifier().GetNumberChildren(); i++) {
        String currentVariable = pWmeAdded.ConvertToIdentifier().GetChild(i).GetAttribute();
        String variableName = Character.toString(currentVariable.charAt(1));
        String trueOrFalse = Character.toString(currentVariable.charAt(0));

        IRandomVariable current = null;
        if (trueOrFalse.equals("+")) {
          current = new RandomVariableImpl(variableName, true);
        }
        else {
          current = new RandomVariableImpl(variableName, false);
        }

        trainingExample.add(current);
      }

      updatedGraph.updateFrequencies(trainingExample);
      updatedGraph.updateRelFrequencies(trainingExample);
      updatedGraph.updateCPTs();
    }
  };

  public static final OutputEventInterface multiQueryHandler = new OutputEventInterface() {
    public void outputEventHandler(Object data, String agentName, String attributeName,
                                   WMElement pWmeAdded) {
      ArrayList<BayesNet> graphs = (ArrayList<BayesNet>) data;
      int graphID = Integer
              .parseInt(pWmeAdded.ConvertToIdentifier().FindByAttribute("id", 0).GetValueAsString());
      BayesNet net = graphs.get(graphID);

      BayesNetUtils util = new BayesNetUtils();

      // gets the queried variable and its name
      String queryVar = "";
      for (int i = 0; i < pWmeAdded.ConvertToIdentifier().GetNumberChildren(); i++) {
        if (pWmeAdded.ConvertToIdentifier().GetChild(i).GetAttribute().equals("query")) {
          queryVar = pWmeAdded.ConvertToIdentifier().GetChild(i).GetValueAsString();
        }
      }
      String queryVarName = queryVar.substring(1).toUpperCase();

      // gets a list of the given variables
      ArrayList<String> evidence = new ArrayList<String>();
      for (int i = 0; i < pWmeAdded.ConvertToIdentifier().GetNumberChildren(); i++) {
        if (pWmeAdded.ConvertToIdentifier().GetChild(i).GetAttribute().equals("given")) {
          evidence.add(pWmeAdded.ConvertToIdentifier().GetChild(i).GetValueAsString());
        }
      }

      // gets a list of the given variable names
      ArrayList<String> evidenceNames = new ArrayList<String>();
      for (int i = 0; i < evidence.size(); i++) {
        evidenceNames.add(evidence.get(i).substring(1).toUpperCase());
      }

      // gets a list with the evidence and the query variable
      ArrayList<String> queryList = new ArrayList<String>(evidenceNames);
      queryList.add(queryVarName);

      // gets a list of all the variable names in the graph
      List<String> graphNodeNames = net.toNodeNames();

      List<INode> graphNodes = net.getNodes();
      int numNodes = net.getNodes().size();

      // gets a node in the graph with no parents and sets it as the starting CPT,
      // joining necessary variables
      INode startingNode = net.getNoParentNode();
      ICPT joinedCPT = startingNode.convertToInferenceCPT().getCPT();
      for (int i = 0; i < graphNodes.size(); i++) {
        if (graphNodes.get(i).hasParent(startingNode.getNodeName())) {
          // join their CPTs
          joinedCPT = graphNodes.get(i).convertToInferenceCPT().join(joinedCPT, startingNode.getNodeName());

          graphNodes.remove(graphNodes.get(i));
        }
      }
      graphNodeNames.remove(startingNode.getNodeName());

      // finishes joining on the rest of the variables
      List<String> cptVariables = joinedCPT.variablesInCPT();
      while (cptVariables.size() < numNodes) {
        // goes through the nodes that aren't yet joined and in the current joined
        // cpt
        for (int j = 0; j < cptVariables.size(); j++) {
          String nodeName = cptVariables.get(j);
          // if still not joined yet
          if (graphNodeNames.contains(nodeName)) {
            // go through the graph and find nodes that have the selected node as its
            // parent
            for (int k = 0; k < graphNodes.size(); k++) {
              if (graphNodes.get(k).hasParent(nodeName)) {
                // join their CPTs
                joinedCPT = graphNodes.get(k).convertToInferenceCPT().join(joinedCPT, nodeName);
              }
            }
            graphNodeNames.remove(nodeName);
          }
        }
        cptVariables = joinedCPT.variablesInCPT();
      }

      joinedCPT.eliminateExcept(queryList);
      joinedCPT.normalize();

      System.out.println(joinedCPT.printCPT());

      ArrayList<String> queryVarsList = new ArrayList<>(evidence);
      queryVarsList.add(queryVar);
      List<IRandomVariable> queryVars = BayesNetUtils.stringListToVar(queryVarsList);
      net.setResult(joinedCPT.getQueryVar(queryVars));

      //pWmeAdded.ConvertToIdentifier().CreateFloatWME("result", joinedCPT.getQueryVar(queryVars));

    }
  };

}
