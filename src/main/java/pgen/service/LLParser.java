package pgen.service;

import com.sun.xml.internal.fastinfoset.algorithm.IntegerEncodingAlgorithm;
import com.sun.xml.internal.fastinfoset.util.DuplicateAttributeVerifier;
import pgen.model.EdgeModel;
import pgen.model.GraphModel;
import pgen.model.NodeModel;
import wagu.Block;
import wagu.Board;
import wagu.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static com.sun.xml.internal.fastinfoset.alphabet.BuiltInRestrictedAlphabets.table;

/**
 * Created by Pouya Payandeh on 9/18/2016.
 */
public class LLParser
{
    private List<Message> check(List<GraphModel> graphs)
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

        //   messages.add(new Message(Message.INFO, ""));
        return messages;
    }

    public LLCell[][] buildTable(List<GraphModel> graphs, Map<String, Integer> tokensInt) throws TableException
    {
        List<Message> msgs = check(graphs);
        ArrayList<String> tokens = new ArrayList<>(graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> !edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet()));
        tokens.add(0, "$");

        Set<String> vars = graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet());

        Set<String> givenGraphs = graphs.stream().map(GraphModel::getName).collect(Collectors.toSet());

        vars.addAll(givenGraphs);

        Map<String, GraphModel> varGraph = getVarGraphs(graphs, vars);

        Map<String, Set<String>> firsts = getFirstSets(varGraph);
        List<NodeModel> nodes = graphs.stream().
                flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());

        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).setId(i);
        }


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

            if (graphModel.getName().equals("MAIN"))
            {
                graphModel.getNodes().stream().filter(NodeModel::getFinal).
                        forEach(nodeModel -> table[nodeModel.getId()][0] = new LLCell(LLCell.ACCEPT, -1, ""));
            } else
            {
                graphModel.getNodes().stream().filter(NodeModel::getFinal).
                        forEach(nodeModel ->
                        {
                            for (int i = 0; i < table[nodeModel.getId()].length; i++)
                                table[nodeModel.getId()][i] = new LLCell(LLCell.RETURN, tokensInt.get(graphModel.getName()), "");

                        });
            }

        }

        nodes.stream().forEach(node ->
        {
            node.getAdjacent().forEach(edge ->
            {
                if (edge.getGraph())
                {
                    table[node.getId()][tokensInt.get(edge.getToken())] = new LLCell(LLCell.GOTO, edge.getEnd().getId(), edge.getFunc());
                    Set<String> first = firsts.get(edge.getToken());
                    first.forEach(s ->
                    {
                        if (table[node.getId()][tokensInt.get(s)].action == LLCell.PUSH_GOTO ||
                                table[node.getId()][tokensInt.get(s)].action == LLCell.SHIFT)
                            msgs.add(new Message(Message.ERROR, String.format("First Set Collision in node %d and token \"%s\"", node.getId(), s)));
                        table[node.getId()][tokensInt.get(s)] = new LLCell(LLCell.PUSH_GOTO, varGraph.get(edge.getToken()).getStart().getId(), "");
                    });
                } else
                {
                    if (table[node.getId()][tokensInt.get(edge.getToken())].action == LLCell.PUSH_GOTO ||
                            table[node.getId()][tokensInt.get(edge.getToken())].action == LLCell.SHIFT)
                        msgs.add(new Message(Message.ERROR, String.format("First Set Collision in node %d and token \"%s\"", node.getId(), edge.getToken())));
                    table[node.getId()][tokensInt.get(edge.getToken())] = new LLCell(LLCell.SHIFT, edge.getEnd().getId(), edge.getFunc());
                }
            });
        });
        if (msgs.size() != 0)
            throw new TableException(msgs);
        return table;
    }

    public List<Message> buildTable(List<GraphModel> graphs, File file)
    {
        Map<String, Integer> tokensInt = new HashMap<>();
        try
        {
            LLCell[][] table = buildTable(graphs, tokensInt);
            List<NodeModel> nodes = graphs.stream().
                    flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());
            try (PrintWriter writer = new PrintWriter(file))
            {
                writer.printf("%d %d\n", nodes.size(), table[0].length);
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
        } catch (TableException e)
        {
            e.printStackTrace();
            return e.getMsg();
        }

        return new ArrayList<>();
    }

    public List<Message> buildPrettyTable(List<GraphModel> graphs, File file)
    {
        Map<String, Integer> tokensInt = new HashMap<>();
        try
        {
            LLCell[][] table = buildTable(graphs, tokensInt);
            List<NodeModel> nodes = graphs.stream().
                    flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());
            try (PrintWriter writer = new PrintWriter(file))
            {
                List<String> list = tokensInt.keySet().stream().sorted((o1, o2) -> tokensInt.get(o1) - tokensInt.get(o2)).collect(Collectors.toList());
                List<String> headersList = new ArrayList<>(list);
                headersList.add(0,"States");
                writer.println();
                int state = 0;
                List<List<String>> rowsList = new ArrayList<>();
                List<Integer> colAlignList = new ArrayList<>();
                for (int i = 0; i < headersList.size(); i++)
                {
                    colAlignList.add(Block.DATA_CENTER);
                }
                for (LLCell[] llCells : table)
                {
                    List<String> row = new ArrayList<>();
                    row.add(String.valueOf(state++));
                    for (LLCell llCell : llCells)
                    {
                        String r = llCell.getActionString();
                        if(llCell.action == LLCell.RETURN)
                        {
                            for(Map.Entry<String,Integer> pair : tokensInt.entrySet())
                            {
                                if(pair.getValue() == llCell.target)
                                {
                                    r += " " + pair.getKey();
                                    break;
                                }
                            }

                        }
                        if(llCell.action == LLCell.GOTO || llCell.action == LLCell.PUSH_GOTO || llCell.action == LLCell.SHIFT)
                            r+=" S" +llCell.target;
                        if(llCell.action != LLCell.ERROR && llCell.action != LLCell.RETURN)
                            r += " " +llCell.func;
                        row.add(r);

                    }
                    rowsList.add(row);
                }

                Board board = new Board(headersList.size()*30);
                Table ta = new Table(board, headersList.size() * 30, headersList, rowsList);
                ta.setColAlignsList(colAlignList);
                String tableString = board.setInitialBlock(ta.tableToBlocks()).build().getPreview();
                writer.println(tableString);
                writer.println();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        } catch (TableException e)
        {
            e.printStackTrace();
            return e.getMsg();
        }

        return new ArrayList<>();
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
