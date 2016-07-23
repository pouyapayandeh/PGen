package pgen;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

public class Controller
{

    @FXML
    public ListView list;
    @FXML
    public AnchorPane pane;
    public AnchorPane pane2;


    ObservableList<String> listData = FXCollections.observableArrayList();
    DrawPaneController drawPaneController;
    @FXML
    private void initialize()
    {
        drawPaneController = new DrawPaneController(pane);
    }

}
