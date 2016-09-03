package pgen.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class GraphModel
{
    public GraphModel(String name)
    {
        this.name = name;
    }

    String name;
    List<NodeModel> nodes = new ArrayList<NodeModel>();

    public List<NodeModel> getNodes()
    {
        return nodes;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
