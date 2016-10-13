package pgen.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import pgen.cmd.CommandManager;
import pgen.graphics.MessageAlert;
import pgen.model.GraphModel;
import pgen.service.ExportService;
import pgen.service.LLParser;
import pgen.service.Message;
import pgen.service.SaveLoadService;

import java.io.File;
import java.util.List;
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
    @FXML
    public MenuItem checkMenuItem;
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    public MenuItem loadMenuItem;
    public ScrollPane scrollpane;

    DrawPaneController drawPaneController;

    @FXML
    private void initialize()
    {
        drawPaneController = new DrawPaneController(pane);
        CommandManager.init(drawPaneController);
//        final double SCALE_DELTA = 1.1;
//        scrollpane.setOnScroll(new EventHandler<ScrollEvent>() {
//        @Override public void handle(ScrollEvent event) {
//            if(event.isControlDown())
//            {
//                event.consume();
//
//
//                double scaleFactor =
//                        (event.getDeltaY() > 0)
//                                ? SCALE_DELTA
//                                : 1 / SCALE_DELTA;
//
//                pane.setScaleX(pane.getScaleX() * scaleFactor);
//                pane.setScaleY(pane.getScaleY() * scaleFactor);
//                drawPaneController.refresh();
//            }
//        }
//    });
        GraphModel graph = new GraphModel("MAIN");
        drawPaneController.graph = graph;
        list.getItems().addAll(graph);
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
        mainContainer.addEventHandler(KeyEvent.KEY_RELEASED,event -> drawPaneController.firstNode =null);
        exportMenuItem.setOnAction(this::export);
        saveMenuItem.setOnAction(this::save);
        loadMenuItem.setOnAction(this::load);
        checkMenuItem.setOnAction(this::check);
    }

    private void check(ActionEvent actionEvent)
    {
        LLParser parser = new LLParser();
        List<Message> msgs = parser.check(list.getItems());
        String msg = msgs.stream().map(message -> message.getMessage() + "\n").reduce(String::concat).get();
        MessageAlert alert = new MessageAlert(Alert.AlertType.ERROR,msg);
        alert.showAndWait();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("JavaFX Projects");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("npt File","*.npt"));
        File selectedFile = chooser.showSaveDialog(pane.getScene().getWindow());
        if(selectedFile != null)
        {
            parser.buildTable(list.getItems(),selectedFile);
        }
    }

    private void load(ActionEvent actionEvent)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("JavaFX Projects");
        chooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("PGEN Save File","*.pgs"));
        File selectedFile = chooser.showOpenDialog(pane.getScene().getWindow());
        if(selectedFile != null)
        {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.load(list);
            drawPaneController.graph  = list.getItems().get(0);
            drawPaneController.refresh();
        }
    }

    private void save(ActionEvent actionEvent)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("JavaFX Projects");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PGEN Save File","*.pgs"));
        File selectedFile = chooser.showSaveDialog(pane.getScene().getWindow());
        if(selectedFile != null)
        {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.save(list.getItems());
        }
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
            if(keyEvent.getCode().equals(KeyCode.Z))
            {
                CommandManager.getInstance().rollBack();
            }
        }
    }

}
