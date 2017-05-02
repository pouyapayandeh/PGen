package pgen.model;

import javafx.beans.property.*;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class EdgeModel
{
    NodeModel start , end;
    StringProperty token,func;
    BooleanProperty graph,global;
    DoubleProperty anchorX;
    DoubleProperty anchorY;
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

    public boolean getGraph()
    {
        return graph.get();
    }

    public BooleanProperty graphProperty()
    {
        return graph;
    }

    public void setGraph(boolean graph)
    {
        this.graph.set(graph);
    }

    public boolean getGlobal()
    {
        return global.get();
    }

    public BooleanProperty globalProperty()
    {
        return global;
    }

    public void setGlobal(boolean global)
    {
        this.global.set(global);
    }

    public EdgeModel(NodeModel start, NodeModel end)
    {
        this.start = start;
        this.end = end;
        anchorX = new SimpleDoubleProperty((start.x.get() + end.x.get()) /2) ;
        anchorY = new SimpleDoubleProperty((start.y.get() + end.y.get()) /2);

        if(start == end)
        {
            int pad = 40;
            anchorX = new SimpleDoubleProperty((start.x.get())) ;
            if(start.y.get() > 4.0/3.0 * pad)
                anchorY = new SimpleDoubleProperty((start.y.get() -pad));
            else
                anchorY = new SimpleDoubleProperty((start.y.get() +pad));
        }
        token = new SimpleStringProperty("");
        func = new SimpleStringProperty("");
        graph = new SimpleBooleanProperty(false);
        global = new SimpleBooleanProperty(false);
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
