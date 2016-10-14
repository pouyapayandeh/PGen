package pgen.cmd;

import pgen.model.EdgeModel;
import pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 10/14/2016.
 */
public class DeleteEdgeCmd implements Command
{

    EdgeModel edge;

    public DeleteEdgeCmd(EdgeModel edge)
    {
        this.edge = edge;
    }

    @Override
    public void apply()
    {
        NodeModel node = edge.getStart();
        node.getAdjacent().remove(edge);

    }

    @Override
    public void rollBack()
    {
        NodeModel node = edge.getStart();
        node.getAdjacent().add(edge);
    }
}
