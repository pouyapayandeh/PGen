package pgen.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import pgen.cmd.CommandManager;
import pgen.model.GraphModel;
import pgen.service.ExportService;

import java.io.File;
import java.util.Optional;

public class Controller
{

    @FXML
    public ListView<GraphModel> list;
    @FXML
    public AnchorPane pane;
    public AnchorPane pane2;
    @FXML
    public VBox mainContainer;
    @FXML
    public MenuItem exportMenuItem;

    DrawPaneController drawPaneController;

    @FXML
    private void initialize()
    {
        drawPaneController = new DrawPaneController(pane);
        CommandManager.init(drawPaneController);
        GraphModel graph = new GraphModel("1");
        drawPaneController.graph = graph;
        list.getItems().addAll(graph, new GraphModel("2"));
        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            drawPaneController.graph = newValue;
            drawPaneController.refresh();
        });
        list.setCellFactory(param ->
        {
            ListCell<GraphModel> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteBtn = new MenuItem("Delete");
            MenuItem renameBtn = new MenuItem("Rename");
            deleteBtn.setOnAction(event -> {

                cell.getListView().getItems().remove(cell.getIndex());
            });
            renameBtn.setOnAction(event ->
            {
                TextInputDialog dialog = new TextInputDialog(cell.getItem().getName());
                dialog.setTitle("Dialog");
                dialog.setHeaderText("Enter a Name");
                Optional<String> result = dialog.showAndWait();
                result.ifPresent(s -> cell.getItem().setName(s));
            });
            contextMenu.getItems().addAll(deleteBtn, renameBtn);
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty)
                {
                    cell.setContextMenu(null);
                    if (cell.getItem() == null)
                    {
                        System.out.println(cell.getIndex());
                        cell.textProperty().unbind();
//                        cell.setGraphic(null);
                        cell.setText("");
                    }
                } else
                {
                    cell.setContextMenu(contextMenu);
                    cell.textProperty().bind(cell.getItem().nameProperty());
                }
            });
            return cell;

        });
//
//        ContextMenu contextMenu = new ContextMenu();
//        MenuItem deleteBtn = new MenuItem("Delete");
//        CheckMenuItem finalBtn = new CheckMenuItem("Rename");
//        node.finalProperty().bind(finalBtn.selectedProperty());
//        CheckMenuItem startBtn = new CheckMenuItem("Start");
//        contextMenu.getItems().addAll(deleteBtn, finalBtn, startBtn);
//        contextMenu.show(this, event.getScreenX(), event.getScreenY());
//
//        list.setContextMenu();
        mainContainer.addEventHandler(KeyEvent.KEY_PRESSED,this::onKeyPressed);
        exportMenuItem.setOnAction(this::export);
    }

    private void export(ActionEvent actionEvent)
    {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File selectedDirectory = chooser.showDialog(pane.getScene().getWindow());
        if(selectedDirectory != null)
        {
            ExportService exportService = new ExportService(selectedDirectory);
            exportService.exportGraphs(list.getItems());

        }
    }

    private void onKeyPressed(KeyEvent keyEvent)
    {
        System.out.println("YO");
        if (keyEvent.isControlDown())
        {
            if (keyEvent.getCode().equals(KeyCode.N))
            {
                list.getItems().addAll(new GraphModel("2"));
            }
        }
    }

}
