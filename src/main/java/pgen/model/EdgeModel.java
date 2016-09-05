package pgen.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class EdgeModel
{
    NodeModel start , end;
    StringProperty token,func;

    public NodeModel getEnd()
    {
        return end;
    }

    public void setEnd(NodeModel end)
    {
        this.end = end;
    }

    public NodeModel getStart()
    {
        return start;
    }

    public void setStart(NodeModel start)
    {
        this.start = start;
    }

    DoubleProperty anchorX;
    DoubleProperty anchorY;

    public String getFunc()
    {
        return func.get();
    }

    public StringProperty funcProperty()
    {
        return func;
    }

    public void setFunc(String func)
    {
        this.func.set(func);
    }

    public String getToken()
    {
        return token.get();
    }

    public StringProperty tokenProperty()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token.set(token);
    }

    public EdgeModel(NodeModel start, NodeModel end)
    {
        this.start = start;
        this.end = end;
        anchorX = new SimpleDoubleProperty((start.x.get() + end.x.get()) /2) ;

        anchorY = new SimpleDoubleProperty((start.y.get() + end.y.get()) /2);
        token = new SimpleStringProperty("");
        func = new SimpleStringProperty("");
    }

    public double getAnchorX()
    {
        return anchorX.get();
    }

    public DoubleProperty anchorXProperty()
    {
        return anchorX;
    }

    public double getAnchorY()
    {
        return anchorY.get();
    }

    public DoubleProperty anchorYProperty()
    {
        return anchorY;
    }
}
