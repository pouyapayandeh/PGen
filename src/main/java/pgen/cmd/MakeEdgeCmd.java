package pgen.cmd;

import pgen.model.EdgeModel;
import pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class MakeEdgeCmd implements Command
{
    NodeModel start, end;
    EdgeModel edge;

    public MakeEdgeCmd(NodeModel start, NodeModel end)
    {
        this.start = start;
        this.end = end;
    }

    @Override
    public void apply()
    {

        edge = new EdgeModel(start, end);
        start.getAdjacent().add(edge);

    }

    @Override

    public void rollBack()
    {

    }
}
