package pgen.service;

import pgen.model.EdgeModel;
import pgen.model.GraphModel;
import pgen.model.NodeModel;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Pouya Payandeh on 9/18/2016.
 */
public class LLParser
{
    public List<Message> check(List<GraphModel> graphs)
    {
        List<Message> messages = new ArrayList<>();
        Set<String> extractExpectedGraphs = graphs.stream().flatMap(graph -> graph.getEdges().stream()).
                filter(EdgeModel::getGraph).map(EdgeModel::getToken).collect(Collectors.toSet());
        Set<String> givenGraphs = graphs.stream().map(GraphModel::getName).collect(Collectors.toSet());

        extractExpectedGraphs.removeAll(givenGraphs);

        extractExpectedGraphs.
                forEach(s -> messages.add(new Message(Message.ERROR, String.format("Graph %s doesn't Exist", s))));

        List<GraphModel> woFinals = graphs.stream().
                filter(graph -> !graph.getNodes().stream().anyMatch(node -> node.getFinal())).collect(Collectors.toList());
        woFinals.
                forEach(graph -> messages.add(new Message(Message.ERROR, String.format("Graph %s doesn't have final  Node", graph.getName()))));

        List<GraphModel> woStart = graphs.stream().
                filter(graph -> graph.getStart() == null).collect(Collectors.toList());
        woStart.
                forEach(graph -> messages.add(new Message(Message.ERROR, String.format("Graph %s doesn't have start Node", graph.getName()))));

        messages.add(new Message(Message.INFO,""));
        return messages;
    }
    public void buildTable(List<GraphModel> graphs)
    {
        Set<String> tokens = graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> !edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet());
        tokens.add("$");

        Set<String> vars = graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet());


        Map<String , GraphModel> varGraph = new HashMap<>();

        for (String s : vars)
        {
            for (GraphModel g:graphs)
            {
                if(g.getName().equals(s))
                {
                    varGraph.put(s ,g);
                            break;
                }
            }
        }
        
        List<NodeModel> nodes = graphs.stream().
                flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());

        for(int i = 0  ; i < nodes.size() ; i++)
        {
            nodes.get(i).setId(i+1);
        }
        Map<String , Integer>  tokensInt = new HashMap<>();


        int seti = 0 ;
        for( String s : tokens)
        {
            tokensInt.put(s,seti);
            seti++;
        }

        String[][] table = new String[nodes.size() + 1][ tokens.size()];
        nodes.stream().forEach(node -> {
            node.getAdjacent().forEach(edge -> {
                if(edge.getGraph())
                {

                }else
                {
                    table[node.getId()][tokensInt.get(edge.getToken())] = String.valueOf(edge.getEnd().getId());
                }
            });
        });
    }
}
