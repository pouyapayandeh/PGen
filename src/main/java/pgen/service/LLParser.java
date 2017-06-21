package pgen.service;

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
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Pouya Payandeh on 9/18/2016.
 */
public class LLParser
{
    final String EPSILON = "$epsilon";
    final String EOF = "$";

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
        if (msgs.size() > 0)
            throw new TableException(msgs);
        ArrayList<String> tokens = new ArrayList<>(graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> !edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet()));
        tokens.add(0, EOF);

        Set<String> vars = graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet());

        Set<String> givenGraphs = graphs.stream().map(GraphModel::getName).collect(Collectors.toSet());

        vars.addAll(givenGraphs);

        Map<String, GraphModel> varGraph = getVarGraphs(graphs, vars);

        Map<String, Set<String>> firsts = getFirstSets(graphs);
        Map<String, Set<String>> follows = getFollowSets(graphs, firsts);
        List<NodeModel> nodes = graphs.stream().
                flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());

        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).setId(i);
        }


        int seti = 0;
        for (String s : tokens)
        {
            if (tokensInt.put(s, seti) != null)
            {
                if (s.startsWith("$"))
                    msgs.add(new Message(Message.ERROR, String.format("All string starting with $ are predefined Tokens")));
                else
                    msgs.add(new Message(Message.ERROR, String.format("%s Should be either a Token or a Graph", s)));
            }
            seti++;

        }

        for (String s : vars)
        {
            if (tokensInt.put(s, seti) != null)
            {
                msgs.add(new Message(Message.ERROR, String.format("%s Should be either a Token or a Graph", s)));
            }
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
                            //Todo Can Use Follow Set

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
                    if(first.contains(EPSILON))
                    {
                        first = new HashSet<>(first);
                        first.addAll(firsts.get("$"+edge.getEnd().getId()));
                    }
                    first.forEach(s ->
                    {
                        if (!s.equals(EPSILON))
                        {
                            if (table[node.getId()][tokensInt.get(s)].action == LLCell.PUSH_GOTO ||
                                    table[node.getId()][tokensInt.get(s)].action == LLCell.SHIFT)
                                msgs.add(new Message(Message.ERROR, String.format("First Set Collision in node %d and token \"%s\"", node.getId(), s)));
                            table[node.getId()][tokensInt.get(s)] = new LLCell(LLCell.PUSH_GOTO, varGraph.get(edge.getToken()).getStart().getId(), "");


                        } else
                        {
//                            for (int i = 0; i < table[node.getId()].length; i++)
//                                if (table[node.getId()][i].action == LLCell.ERROR)
//                                    table[node.getId()][i] = new LLCell(LLCell.PUSH_GOTO, varGraph.get(edge.getToken()).getStart().getId(), "");
//

                           if(firsts.get("$"+edge.getEnd().getId()).contains(EPSILON))
                            {
                                if(node.getId() == 274)
                                    System.out.println("HERTE");
                                follows.get("$"+node.getId()).forEach(ss ->
                                        {
                                            if (table[node.getId()][tokensInt.get(ss)].action == LLCell.PUSH_GOTO ||
                                                    table[node.getId()][tokensInt.get(ss)].action == LLCell.SHIFT)
                                                msgs.add(new Message(Message.ERROR, String.format("Follow Set Collision in node %d and token \"%s\"", node.getId(), ss)));
                                                table[node.getId()][tokensInt.get(ss)] =new LLCell(LLCell.PUSH_GOTO, varGraph.get(edge.getToken()).getStart().getId(), "");
                                        }
                                );
                            }
                        }
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
            try (PrintWriter writer = new PrintWriter(file))
            {
                List<String> list = tokensInt.keySet().stream().sorted(Comparator.comparingInt(tokensInt::get)).collect(Collectors.toList());
                List<String> headersList = new ArrayList<>(list);
                headersList.add(0, "States");
                writer.println();
                List<Integer> colAlignList = new ArrayList<>();
                for (int i = 0; i < headersList.size(); i++)
                {
                    colAlignList.add(Block.DATA_CENTER);
                }
                List<List<String>> rowsList = BuildStringTable(tokensInt, table);
                BuildStringTable(tokensInt, table);

                Board board = new Board(headersList.size() * 30);
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
            return e.getMsg();
        }

        return new ArrayList<>();
    }

    private List<List<String>> BuildStringTable(Map<String, Integer> tokensInt, LLCell[][] table)
    {
        int state = 0;
        List<List<String>> rowsList = new ArrayList<>();
        for (LLCell[] llCells : table)
        {
            List<String> row = new ArrayList<>();
            row.add(String.valueOf(state++));
            for (LLCell llCell : llCells)
            {
                String r = llCell.getActionString();
                if (llCell.action == LLCell.RETURN)
                {
                    for (Map.Entry<String, Integer> pair : tokensInt.entrySet())
                    {
                        if (pair.getValue() == llCell.target)
                        {
                            r += " " + pair.getKey();
                            break;
                        }
                    }

                }
                if (llCell.action == LLCell.GOTO || llCell.action == LLCell.PUSH_GOTO || llCell.action == LLCell.SHIFT)
                    r += " S" + llCell.target;
                if (llCell.action != LLCell.ERROR && llCell.action != LLCell.RETURN)
                    r += " " + llCell.func;
                row.add(r);

            }
            rowsList.add(row);
        }
        return rowsList;
    }

    public List<Message> buildCSVTable(List<GraphModel> graphs, File file)
    {
        Map<String, Integer> tokensInt = new HashMap<>();
        try
        {
            LLCell[][] table = buildTable(graphs, tokensInt);
            try (PrintWriter writer = new PrintWriter(file))
            {
                StringBuffer blocksText = new StringBuffer();
                List<String> list = tokensInt.keySet().stream().sorted(Comparator.comparingInt(tokensInt::get)).collect(Collectors.toList());
                List<String> headersList = new ArrayList<>(list);
                headersList.add(0, "States");
                List<Integer> colAlignList = new ArrayList<>();
                for (int i = 0; i < headersList.size(); i++)
                {
                    colAlignList.add(Block.DATA_CENTER);
                }
                List<List<String>> rowsList = BuildStringTable(tokensInt, table);

                Board board = new Board(headersList.size() * 120);
                Table ta = new Table(board, headersList.size() * 120, headersList, rowsList);
                ta.setColAlignsList(colAlignList);
                String tableString = board.setInitialBlock(ta.tableToBlocks()).build().getPreview();
                blocksText.append(tableString);
                blocksText.append(System.lineSeparator());

                Scanner sc = new Scanner(blocksText.toString());
                boolean printedInLine = false;
                while (sc.hasNext())
                {
                    String line = sc.nextLine();
                    line = line.trim();
                    if (line.equals("") || line.charAt(0) == '+')
                        continue;
                    String[] items = line.split(" {3,}" + Pattern.quote("|") + " {3,}" + "|" + Pattern.quote("|") + " {3,}" + "|" + " {3,}" + Pattern.quote("|"));
                    if (items.length == 0)
                        continue;

                    for (String item : items)
                    {
                        String fixedItem = item.trim();
                        if (fixedItem.equals(""))
                            continue;
                        fixedItem = (fixedItem.contains(",") ? "\"" + fixedItem + "\"" : fixedItem);
                        if (printedInLine)
                            writer.write(",");
                        writer.write(fixedItem);
                        printedInLine = true;
                    }
                    writer.write(System.lineSeparator());
                    printedInLine = false;
                }
                writer.write(System.lineSeparator());

            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
        } catch (TableException e)
        {
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

    public Map<String, Set<String>> getFirstSets(List<GraphModel> graphs)
    {
        Set<String> vars = graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet());

        Set<String> givenGraphs = graphs.stream().map(GraphModel::getName).collect(Collectors.toSet());

        List<NodeModel> allNodes = graphs.stream().flatMap(graphModel -> graphModel.getNodes().stream())
                .collect(Collectors.toList());


        vars.addAll(givenGraphs);

        Map<String, GraphModel> varGraph = getVarGraphs(graphs, vars);
        Map<String, Set<String>> firsts = new HashMap<>();

        Map<String, NodeModel> nameNode = new HashMap<>();
        allNodes.forEach(n ->
                {
                    firsts.put("$" + n.getId(), n.getAdjacent().stream().filter(edgeModel -> !edgeModel.getGraph())
                            .map(EdgeModel::getToken).collect(Collectors.toSet()));

                    nameNode.put("$" + n.getId(), n);
                    if (n.getFinal())
                    {
                        Set<String> first = firsts.get("$" + n.getId());
                        first.add(EPSILON);
                    }
                }
        );
        for (Map.Entry<String, GraphModel> entry : varGraph.entrySet())
        {
            try
            {
                firsts.put(entry.getKey(), entry.getValue().getStart().getAdjacent().stream().filter(edgeModel -> !edgeModel.getGraph())
                        .map(EdgeModel::getToken).collect(Collectors.toSet()));
                nameNode.put(entry.getKey(), entry.getValue().getStart());

            } catch (Exception e)
            {
                System.out.println("");
            }

        }

        boolean flag = true;
        while (flag)
        {
            flag = false;

            for (Map.Entry<String, NodeModel> entry : nameNode.entrySet())
            {
                for (EdgeModel edge : entry.getValue().getAdjacent())
                {
                    Set<String> first = firsts.get(entry.getKey());
                    first.addAll(firsts.get("$"+edge.getStart().getId()));
                    firsts.get("$"+edge.getStart().getId()).addAll(first);

                    if (edge.getGraph())
                    {

//                        Set<String> first = firsts.get(entry.getKey());
                        first.addAll(firsts.get("$"+edge.getStart().getId()));
                        firsts.get("$"+edge.getStart().getId()).addAll(first);


                        Set<String> first2 = new HashSet<>(firsts.get(edge.getToken()));
                        Set<String> first3 = firsts.get("$" + edge.getEnd().getId());
                        if (first2.contains(EPSILON))
                        {
                          //  first3.remove(EPSILON);
                            if (first.addAll(first3))
                                flag = true;
                        }
                        first2.remove(EPSILON);
                        if (first.addAll(first2))
                            flag = true;



                    }
                }
            }
        }
        return firsts;
    }

    public Map<String, Set<String>> getFollowSets(List<GraphModel> graphs, Map<String, Set<String>> firsts)
    {

        Map<String, Set<String>> follows = new HashMap<>();
        Set<String> vars = graphs.stream().
                flatMap(graph -> graph.getEdges().stream()).filter(edge -> edge.getGraph()).
                map(EdgeModel::getToken).collect(Collectors.toSet());

        Set<String> givenGraphs = graphs.stream().map(GraphModel::getName).collect(Collectors.toSet());

        List<NodeModel> allNodes = graphs.stream().flatMap(graphModel -> graphModel.getNodes().stream())
                .collect(Collectors.toList());


        vars.addAll(givenGraphs);

        Map<String, GraphModel> varGraph = getVarGraphs(graphs, vars);


        Map<String, NodeModel> nameNode = new HashMap<>();
        allNodes.forEach(n ->
                {
                    nameNode.put("$" + n.getId(), n);
                    follows.put("$" + n.getId(), new HashSet<>());
                }
        );

        for (Map.Entry<String, GraphModel> entry : varGraph.entrySet())
        {
            try
            {
                nameNode.put(entry.getKey(), entry.getValue().getStart());
                follows.put(entry.getKey(), new HashSet<>());
            } catch (Exception e)
            {
                System.out.println("");
            }

        }
        int mainId = nameNode.get("MAIN").getId();
        follows.get("$" + mainId).add(EOF);
        follows.get("MAIN").add(EOF);
        boolean flag = true;
        while (flag)
        {
            flag = false;
            for (Map.Entry<String, NodeModel> entry : nameNode.entrySet())
            {
                for (EdgeModel edge : entry.getValue().getAdjacent())
                {
                    Set<String> follow = follows.get(entry.getKey());
                    follow.addAll(follows.get("$"+edge.getStart().getId()));
                    follows.get("$"+edge.getStart().getId()).addAll(follow);
                    if (edge.getGraph())
                    {
//                        Set<String> follow = follows.get(entry.getKey());
                        Set<String> follow2 = follows.get(edge.getToken());

//                        follow.addAll(follows.get("$"+edge.getStart().getId()));
//                        follows.get("$"+edge.getStart().getId()).addAll(follow);
//                        if(edge.getToken().equals("__expr"))
//                            System.out.println("hre");

                        Set<String> first3 = firsts.get("$" + edge.getEnd().getId());
                        Set<String> follow3 = follows.get("$" + edge.getEnd().getId());

                        if (follow2.addAll(first3))
                        {
                            flag = true;
                        }
                        if (first3.contains(EPSILON))
                        {
                            if (follow2.addAll(follow))
                                flag = true;
                        }
                       // follow2.remove(EPSILON);
                    }
                }
            }
        }
        follows.forEach((s, strings) -> strings.remove(EPSILON));
        return follows;
    }
}
