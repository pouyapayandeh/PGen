package pgen.service;

import javafx.scene.Node;
import pgen.model.GraphModel;
import pgen.model.NodeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pouya Payandeh on 9/6/2016.
 */
public class GraphJSON
{
    String name;
    List<NodeJSON> nodes;
    List<EdgeJSON> edges;

    Integer start = -1;
    public GraphJSON()
    {
    }
    public GraphJSON(GraphModel g)
    {
        name = g.getName();
        nodes = g.getNodes().stream().map(NodeJSON::new).collect(Collectors.toList());
        edges = g.getEdges().stream().map(EdgeJSON::new).collect(Collectors.toList());
        if(g.getStart() != null)
            start = g.getStart().getId();
    }

}
