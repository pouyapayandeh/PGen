package pgen.cmd;

import pgen.model.GraphModel;
import pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class MakeNodeCmd implements Command
{
    GraphModel graphModel;
    double x;
    double y;
    public MakeNodeCmd(GraphModel graphModel , double x , double y)
    {
        this.graphModel = graphModel;
        this.x=x;
        this.y=y;
    }

    @Override
    public void apply()
    {
        graphModel.getNodes().add(new NodeModel(x,y,graphModel));
    }

    @Override
    public void rollBack()
    {

    }
}
