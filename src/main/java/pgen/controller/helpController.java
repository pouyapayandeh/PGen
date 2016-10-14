package pgen.controller;

import javafx.event.ActionEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Created by Pouya Payandeh on 10/14/2016.
 */
public class helpController
{
    public AnchorPane pane;

    public void close(ActionEvent actionEvent)
    {
        ((Stage)pane.getScene().getWindow()).close();
    }
}
