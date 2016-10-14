package pgen.controller;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import pgen.model.EdgeModel;
import pgen.model.GraphModel;
import pgen.graphics.GraphNode;
import pgen.model.NodeModel;
import pgen.cmd.Command;
import pgen.cmd.MakeEdgeCmd;
import pgen.cmd.MakeNodeCmd;
import pgen.graphics.BoundLine;


/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class DrawPaneController implements RefreshableController
{
    Pane pane;
    GraphModel graph;
    public DrawPaneController(Pane pane)
    {
        this.pane = pane;
        pane.setOnMouseReleased(this::mouseClickEvent);

    }
    public void refresh()
    {
        pane.getChildren().clear();

        for(NodeModel nodeModel : graph.getNodes())
        {
            for(EdgeModel edge : nodeModel.getAdjacent())
            {
                BoundLine line = new BoundLine(edge);
                pane.getChildren().addAll(line , line.getAnchor(),line.getText(),line.getArrowEnd());
            }
        }


        for(NodeModel nodeModel : graph.getNodes())
        {
            GraphNode graphNode = new GraphNode(nodeModel);
            graphNode.setOnShiftClick(this::onShiftClick);
            pane.getChildren().add(graphNode);
        }


    }

    NodeModel firstNode =null;
    private void onShiftClick(MouseEvent mouseEvent)
    {
        NodeModel node = ((GraphNode) mouseEvent.getSource()).getNode();
        if(firstNode == null)
            firstNode = node;
        else
        {
            Command cmd = null;
            cmd = new MakeEdgeCmd(firstNode,node);
            cmd.apply();
            refresh();
            firstNode = node;
            System.out.println("new Edge");
        }
        System.out.println(node.getId());
    }

    private void mouseClickEvent(MouseEvent mouseEvent)
    {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY))
        {
            Command cmd = null;
            cmd = new MakeNodeCmd(graph, mouseEvent.getX(), mouseEvent.getY());
            cmd.apply();
            refresh();
        }
    }
}
