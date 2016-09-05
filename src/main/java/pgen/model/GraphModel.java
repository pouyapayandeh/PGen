package pgen.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class GraphModel
{
    public String getName()
    {
        return name.get();
    }

    public StringProperty nameProperty()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name.set(name);
    }

    public GraphModel(String name)
    {
        this.name = new SimpleStringProperty(name);
    }

    StringProperty name;
    List<NodeModel> nodes = new ArrayList<NodeModel>();

    public List<NodeModel> getNodes()
    {
        return nodes;
    }


    @Override
    public String toString()
    {
        return name.getValue();
    }
    public List<EdgeModel> getEdges()
    {
        ArrayList<EdgeModel> edges = new ArrayList<>();
        List<EdgeModel> res = nodes.stream().map(NodeModel::getAdjacent).flatMap(Collection::stream).collect(Collectors.toList());
        return res;
    }
}
