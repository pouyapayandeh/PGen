package pgen.model;

import javafx.beans.property.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pouya Payandeh on 7/22/2016.
 */
public class NodeModel
{

    static int _id = 0;
    StringProperty name;
    DoubleProperty x = new SimpleDoubleProperty();

    List<EdgeModel> adjacent = new ArrayList<>();
    DoubleProperty y = new SimpleDoubleProperty();
    BooleanProperty final_ = new SimpleBooleanProperty(false);
    GraphModel graph;
    int id;

    public double getX()
    {
        return x.get();
    }

    public DoubleProperty xProperty()
    {
        return x;
    }

    public double getY()
    {
        return y.get();
    }

    public DoubleProperty yProperty()
    {
        return y;
    }

    public boolean getFinal()
    {
        return final_.get();
    }

    public BooleanProperty finalProperty()
    {
        return final_;
    }

    public void setFinal(boolean final_)
    {
        this.final_.set(final_);
    }



    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public NodeModel(double x, double y, GraphModel graph)
    {
        this.x.setValue(x);
        this.y.setValue(y);
        this.graph = graph;
        id=_id++;
    }



    public List<EdgeModel> getAdjacent()
    {
        return adjacent;
    }

    public GraphModel getGraph()
    {
        return graph;
    }

    public void setGraph(GraphModel graph)
    {
        this.graph = graph;
    }
}
