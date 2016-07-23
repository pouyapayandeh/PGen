package pgen;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public class DrawPaneController
{
    Pane pane;
    GraphModel graph;
    public DrawPaneController(Pane pane)
    {
        this.pane = pane;
        pane.setOnMouseClicked(this::mouseClickEvent);
        graph = new GraphModel();
        graph.nodes.addListener((ListChangeListener<? super NodeModel>) c -> {this.nodeChanges(c);});
    }

    private  void nodeChanges(ListChangeListener.Change<? extends NodeModel> c)
    {
        while (c.next())
        {
            if(c.wasAdded())
            {
                List added = c.getAddedSubList();
                for(Object obj : added)
                {
                    NodeModel n = (NodeModel) obj;
                    CircleAnchor anchor = new CircleAnchor(n);
                    pane.getChildren().add(anchor);
                }

            }
            if(c.wasUpdated())
            {
                System.out.println("Updated");
            }
        }
    }
    private void mouseClickEvent(MouseEvent mouseEvent)
    {
        Command cmd = new MakeNodeCmd(graph, mouseEvent.getX(), mouseEvent.getY());
        cmd.apply();
    }
}
