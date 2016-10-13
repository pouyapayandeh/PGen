package pgen.service;

import pgen.model.EdgeModel;
import pgen.model.GraphModel;
import pgen.model.NodeModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
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

        messages.add(new Message(Message.INFO, ""));
        return messages;
    }

    public void buildTable(List<GraphModel> graphs, File file)
    {
        ArrayList<String> tokens = new ArrayList<>(graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> !edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet()));
        tokens.add(0, "$");

        Set<String> vars = graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet());


        Map<String, GraphModel> varGraph = getVarGraphs(graphs, vars);

        Map<String, Set<String>> firsts = getFirstSets(varGraph);
        List<NodeModel> nodes = graphs.stream().
                flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());

        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).setId(i);
        }
        Map<String, Integer> tokensInt = new HashMap<>();


        int seti = 0;
        for (String s : tokens)
        {
            tokensInt.put(s, seti);
            seti++;
        }

        for (String s : vars)
        {
            tokensInt.put(s, seti);
            seti++;
        }
        int tw = tokens.size() + vars.size();
        LLCell[][] table = new LLCell[nodes.size()][tw];
        for (int i = 0; i < table.length; i++)
            for (int j = 0; j < table[0].length; j++)
                table[i][j] = new LLCell(LLCell.ERROR, -1, "");

        for (GraphModel graphModel : graphs)
        {


            graphModel.getNodes().stream().filter(NodeModel::getFinal).
                    forEach(nodeModel -> {
                        for (int i = 0; i < table[nodeModel.getId()].length; i++)
                            table[nodeModel.getId()][i] = new LLCell(LLCell.RETURN, tokensInt.get(graphModel.getName()), "");
                    });
            if (graphModel.getName().equals("MAIN"))
                graphModel.getNodes().stream().filter(NodeModel::getFinal).
                        forEach(nodeModel -> table[nodeModel.getId()][0] = new LLCell(LLCell.ACCEPT,-1, ""));

        }
        nodes.stream().forEach(node -> {
            node.getAdjacent().forEach(edge -> {
                if (edge.getGraph())
                {
                    table[node.getId()][tokensInt.get(edge.getToken())] = new LLCell(LLCell.GOTO, edge.getEnd().getId(), edge.getFunc());
                    Set<String> first = firsts.get(edge.getToken());
                    first.forEach(s -> table[node.getId()][tokensInt.get(s)] = new LLCell(LLCell.PUSH_GOTO, varGraph.get(edge.getToken()).getStart().getId(), ""));
                } else
                {
                    table[node.getId()][tokensInt.get(edge.getToken())] = new LLCell(LLCell.SHIFT, edge.getEnd().getId(), edge.getFunc());
                }
            });
        });

        try (PrintWriter writer = new PrintWriter(file))
        {
            writer.printf("%d %d\n", nodes.size(), tw);
            List<String> list = tokensInt.keySet().stream().sorted((o1, o2) -> tokensInt.get(o1) - tokensInt.get(o2)).collect(Collectors.toList());
            list.forEach(s -> writer.printf("%s ", s));
            writer.println();
            for (LLCell[] llCells : table)
            {
                for (LLCell llCell : llCells)
                {
                    writer.print(llCell + " ");
                }
                writer.println();
            }
            writer.println();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }


    }

    private Map<String, GraphModel> getVarGraphs(List<GraphModel> graphs, Set<String> vars)
    {
        Map<String, GraphModel> varGraph = new HashMap<>();

        for (String s : vars)
        {
            for (GraphModel g : graphs)
            {
                if (g.getName().equals(s))
                {
                    varGraph.put(s, g);
                    break;
                }
            }
        }
        return varGraph;
    }

    public Map<String, Set<String>> getFirstSets(Map<String, GraphModel> varGraph)
    {
        Map<String, Set<String>> firsts = new HashMap<>();
        for (Map.Entry<String, GraphModel> entry : varGraph.entrySet())
        {
            firsts.put(entry.getKey(), entry.getValue().getStart().getAdjacent().stream().filter(edgeModel -> !edgeModel.getGraph())
                    .map(EdgeModel::getToken).collect(Collectors.toSet()));
        }
        boolean flag = true;
        while (flag)
        {
            flag = false;
            for (Map.Entry<String, GraphModel> entry : varGraph.entrySet())
            {
                for (EdgeModel edge : entry.getValue().getEdges())
                {
                    if (edge.getGraph())
                    {
                        Set<String> first = firsts.get(entry.getKey());
                        Set<String> first2 = firsts.get(edge.getToken());
                        if (first.addAll(first2))
                            flag = true;
                    }
                }
            }
        }
        return firsts;
    }
}
