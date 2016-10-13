package pgen.service;

import pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 9/6/2016.
 */
public class NodeJSON
{
    double x,y;
    int id;
    boolean isFinal;
    public NodeJSON(NodeModel n)
    {
        x=n.getX();
        y=n.getY();
        id= n.getId();
        isFinal = n.getFinal();
    }
    public NodeJSON()
    {

    }
}
