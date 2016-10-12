package pgen.service;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.StringProperty;
import pgen.model.EdgeModel;
import pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 9/6/2016.
 */
public class EdgeJSON
{
    int start , end;
    String token,func;
    double anchorX;
    double anchorY;

    public EdgeJSON()
    {
    }
    public EdgeJSON(EdgeModel model)
    {
        start = model.getStart().getId();
        end = model.getEnd().getId();
        token = model.getToken();
        func = model.getFunc();
        anchorX = model.getAnchorX();
        anchorY = model.getAnchorY();
    }

}
