package pgen;

import javafx.beans.property.*;

import java.util.List;

/**
 * Created by Pouya Payandeh on 7/22/2016.
 */
public class NodeModel
{

    StringProperty name;
    DoubleProperty x = new SimpleDoubleProperty();
    DoubleProperty y = new SimpleDoubleProperty();
    BooleanProperty isFinal = new SimpleBooleanProperty(false);

    public NodeModel(double x, double y)
    {
        this.x.setValue(x);
        this.y.setValue(y);
    }

    BooleanProperty isStart;
    List<EdgeModel> adjacent;


}
