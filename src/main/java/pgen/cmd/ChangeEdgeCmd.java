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
    Boolean graph;
    Boolean global;


    String _token;
    String _func;
    Boolean _graph;
    Boolean _global;

    public ChangeEdgeCmd(EdgeModel edgeModel, String token, String func,Boolean graph,Boolean global)
    {
        this.edgeModel = edgeModel;
        this.token = token;
        this.func = func;
        this.global = global;
        this.graph = graph;
    }

    @Override
    public void apply()
    {
        _token = edgeModel.getToken();
        _func = edgeModel.getFunc();
        _graph = edgeModel.getGraph();
        _global  =edgeModel.getGlobal();



        edgeModel.setFunc(func);
        edgeModel.setToken(token);
        edgeModel.setGlobal(global);
        edgeModel.setGraph(graph);

    }

    @Override
    public void rollBack()
    {

        edgeModel.setFunc(_func);
        edgeModel.setToken(_token);
        edgeModel.setGlobal(_graph);
        edgeModel.setGraph(_graph);

    }
}
