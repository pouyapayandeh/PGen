package pgen.controller;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import pgen.cmd.ChangeEdgeCmd;
import pgen.cmd.CommandManager;
import pgen.model.EdgeModel;

/**
 * Created by Pouya Payandeh on 8/31/2016.
 */
public class EdgePropertiesController
{
    EdgeModel edge;

    public void init(EdgeModel edge)
    {
        this.edge = edge;
        tokenText.setText(edge.getToken());
        funcText.setText(edge.getFunc());
        globalChk.setSelected(edge.getGlobal());
        graphChk.setSelected(edge.getGraph());
        okBtn.setOnMouseClicked(event ->
        {
            Stage stage = (Stage) okBtn.getScene().getWindow();
            CommandManager.getInstance().applyCommand(new ChangeEdgeCmd(edge ,tokenText.getText() , funcText.getText(),graphChk.isSelected(),globalChk.isSelected()));
            stage.close();
        }
        );
    }


    @FXML
    public Button okBtn;
    @FXML
    public TextField tokenText;
    @FXML
    public TextField funcText;
    @FXML
    public CheckBox graphChk;
    @FXML
    public CheckBox globalChk;

}
