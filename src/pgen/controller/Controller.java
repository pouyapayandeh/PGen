package pgen.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import pgen.cmd.CommandManager;
import pgen.model.GraphModel;

public class Controller
{

    @FXML
    public ListView<GraphModel> list;
    @FXML
    public AnchorPane pane;
    public AnchorPane pane2;


    DrawPaneController drawPaneController;
    @FXML
    private void initialize()
    {
        drawPaneController = new DrawPaneController(pane);
        CommandManager.init(drawPaneController);
        GraphModel graph = new GraphModel("1");
        drawPaneController.graph = graph;
        list.getItems().addAll(graph,new GraphModel("2"));
        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            drawPaneController.graph = newValue;
            drawPaneController.refresh();
        });
    }

}
