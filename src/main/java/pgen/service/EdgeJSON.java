package pgen.service;

import pgen.model.EdgeModel;

/**
 * Created by Pouya Payandeh on 9/6/2016.
 */
public class EdgeJSON
{
    int start , end;
    String token,func;
    double anchorX;
    double anchorY;
    boolean isGraph;
    boolean isGlobal;

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
        isGraph = model.getGraph();
        isGlobal = model.getGlobal();
    }

}
