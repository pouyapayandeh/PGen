package pgen.cmd;

import pgen.model.EdgeModel;

/**
 * Created by Pouya Payandeh on 9/3/2016.
 */
public class ChangeEdgeCmd implements Command
{
    EdgeModel edgeModel;
    String token;
    String func;

    public ChangeEdgeCmd(EdgeModel edgeModel, String token, String func)
    {
        this.edgeModel = edgeModel;
        this.token = token;
        this.func = func;
    }

    @Override
    public void apply()
    {
        edgeModel.setFunc(func);
        edgeModel.setToken(token);
    }

    @Override
    public void rollBack()
    {

    }
}
