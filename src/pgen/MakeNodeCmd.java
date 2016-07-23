package pgen;

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
        graphModel.nodes.add(new NodeModel(x,y));
    }

    @Override
    public void rollBack()
    {

    }
}
