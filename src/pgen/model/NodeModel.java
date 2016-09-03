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

    DoubleProperty y = new SimpleDoubleProperty();
    BooleanProperty final_ = new SimpleBooleanProperty(false);
    int id;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public NodeModel(double x, double y)
    {
        this.x.setValue(x);
        this.y.setValue(y);
        id=_id++;
    }

    BooleanProperty isStart;
    List<EdgeModel> adjacent = new ArrayList<>();

    public List<EdgeModel> getAdjacent()
    {
        return adjacent;
    }
}
