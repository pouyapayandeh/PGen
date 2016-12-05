package pgen.cmd;

import pgen.model.EdgeModel;
import pgen.model.GraphModel;
import pgen.model.NodeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pouya Payandeh on 9/17/2016.
 */
public class DeleteNodeCmd implements  Command
{
    GraphModel graphModel;
    NodeModel nodeModel;

    List<EdgeModel> removedEdges;
    public DeleteNodeCmd(NodeModel nodeModel)
    {
        this.graphModel = nodeModel.getGraph();
        this.nodeModel = nodeModel;
    }

    @Override
    public void apply()
    {
        graphModel.getNodes().remove(nodeModel);
        removedEdges = graphModel.getEdges().stream().filter(model -> model.getEnd().equals(nodeModel)).collect(Collectors.toList());
        graphModel.getNodes().forEach(node -> node.getAdjacent().removeIf(model ->  model.getEnd().equals(nodeModel)));
    }

    @Override
    public void rollBack()
    {
        graphModel.getNodes().add(nodeModel);
        removedEdges.forEach(edge -> edge.getStart().getAdjacent().add(edge));
    }
}
