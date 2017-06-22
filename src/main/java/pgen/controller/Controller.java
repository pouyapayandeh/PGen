package pgen.controller;

import javafx.beans.Observable;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pgen.cmd.CommandManager;
import pgen.graphics.MessageAlert;
import pgen.model.GraphModel;
import pgen.model.NodeModel;
import pgen.service.ExportService;
import pgen.service.LLParser;
import pgen.service.Message;
import pgen.service.SaveLoadService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Controller
{

    @FXML
    public ListView<GraphModel> list;
    @FXML
    public AnchorPane pane;
    @FXML
    public VBox mainContainer;
    @FXML
    public MenuItem exportMenuItem;
    @FXML
    public MenuItem checkMenuItem;
    @FXML
    public MenuItem saveMenuItem;
    @FXML
    public MenuItem saveSortedMenuItem;
    @FXML
    public MenuItem loadMenuItem;
    public ScrollPane scrollpane;
    public Button addGraphBtn;
    public MenuItem aboutMenuItem;
    public MenuItem exportTableMenuItem;
    public MenuItem exportCSVTableMenuItem;

    DrawPaneController drawPaneController;

    ObservableList<GraphModel> graphs  = FXCollections.observableArrayList();
    @FXML
    private void initialize()
    {
        drawPaneController = new DrawPaneController(pane);
        CommandManager.init(drawPaneController);
        GraphModel graph = new GraphModel("MAIN");
        drawPaneController.graph = graph;
        list.setItems(graphs);
        graphs.addAll(graph);
        list.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            drawPaneController.graph = newValue;
            drawPaneController.refresh();
        });
        list.setCellFactory(param ->
        {
            ListCell<GraphModel> cell = new ListCell<GraphModel>()
            {
                @Override
                protected void updateItem(GraphModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null ) {
                        setText(null);
                    } else {
                        textProperty().bind(item.nameProperty());
                    }
                }
            };

            ContextMenu contextMenu = new ContextMenu();
            MenuItem deleteBtn = new MenuItem("Delete");
            MenuItem renameBtn = new MenuItem("Rename");
//
            deleteBtn.setOnAction(event -> {

                cell.getListView().getItems().remove(cell.getItem());
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
           // cell.textProperty().bind(cell.itemProperty());
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
                    if (cell.getItem().getName().equals("MAIN"))
                    {
                        deleteBtn.setDisable(true);
                        renameBtn.setDisable(true);
                    }
                    cell.setContextMenu(contextMenu);
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
        mainContainer.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        mainContainer.addEventHandler(KeyEvent.KEY_RELEASED, event -> drawPaneController.firstNode = null);
        exportMenuItem.setOnAction(this::export);
        saveMenuItem.setOnAction(this::save);
        saveSortedMenuItem.setOnAction(this::saveSorted);
        loadMenuItem.setOnAction(this::load);
        checkMenuItem.setOnAction(this::build);
        exportTableMenuItem.setOnAction(this::prettyTable);
        exportCSVTableMenuItem.setOnAction(this::csvTable);
        addGraphBtn.setOnAction(event ->graphs.addAll(new GraphModel("New Graph")));
    }

    private void build(ActionEvent actionEvent)
    {
        renumber(null);
        LLParser parser = new LLParser();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Table To");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("npt File", "*.npt"));
        File selectedFile = chooser.showSaveDialog(pane.getScene().getWindow());
        if (selectedFile != null)
        {
            List<Message> msgs = parser.buildTable(graphs, selectedFile);
            ShowMessages(msgs);

        }
    }

    private void prettyTable(ActionEvent actionEvent)
    {
        renumber(null);
        LLParser parser = new LLParser();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save Pretty Table to");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("prt File", "*.prt"));
        File selectedFile = chooser.showSaveDialog(pane.getScene().getWindow());
        if (selectedFile != null)
        {
            List<Message> msgs = parser.buildPrettyTable(graphs, selectedFile);
            ShowMessages(msgs);

        }
    }

    private void csvTable(ActionEvent actionEvent)
    {
        renumber(null);
        LLParser parser = new LLParser();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save CSV Table to");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("csv File", "*.csv"));
        File selectedFile = chooser.showSaveDialog(pane.getScene().getWindow());
        if (selectedFile != null)
        {
            List<Message> msgs = parser.buildCSVTable(graphs, selectedFile);
            ShowMessages(msgs);

        }
    }

    private void ShowMessages(List<Message> msgs)
    {
        if (msgs.size() > 0)
        {
            String msg = msgs.stream().map(message -> message.getMessage() + "\n").reduce(String::concat).get();
            MessageAlert alert = new MessageAlert(Alert.AlertType.ERROR, msg, "Error");
            alert.showAndWait();
        } else
        {
            MessageAlert alert = new MessageAlert(Alert.AlertType.INFORMATION, "", "Success");
            alert.showAndWait();
        }
    }

    private void load(ActionEvent actionEvent)
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("JavaFX Projects");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PGEN Save File (PGS)", "*.pgs")
                ,new FileChooser.ExtensionFilter("ALL", "*")
        );
        File selectedFile = chooser.showOpenDialog(pane.getScene().getWindow());
        if (selectedFile != null)
        {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.load(list);
            drawPaneController.graph = list.getItems().get(0);
            drawPaneController.refresh();
        }
    }

    private void save(ActionEvent actionEvent)
    {
        renumber(null);
        FileChooser chooser = new FileChooser();
        chooser.setTitle("JavaFX Projects");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PGEN Save File (PGS)", "*.pgs")
                ,new FileChooser.ExtensionFilter("ALL", "*")
        );
        File selectedFile = chooser.showSaveDialog(pane.getScene().getWindow());
        if (selectedFile != null)
        {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.save(graphs);
        }
    }

    private void saveSorted(ActionEvent actionEvent)
    {
        renumber(null);
        FileChooser chooser = new FileChooser();
        chooser.setTitle("JavaFX Projects");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PGEN Save File (PGS)", "*.pgs")
                ,new FileChooser.ExtensionFilter("ALL", "*")
        );
        File selectedFile = chooser.showSaveDialog(pane.getScene().getWindow());
        if (selectedFile != null)
        {
            SaveLoadService exportService = new SaveLoadService(selectedFile);
            exportService.saveSorted(graphs);
        }
    }

    private void export(ActionEvent actionEvent)
    {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");

        File selectedDirectory = chooser.showDialog(pane.getScene().getWindow());
        if (selectedDirectory != null)
        {
            ExportService exportService = new ExportService(selectedDirectory);
            exportService.exportGraphs(graphs);
            MessageAlert alert = new MessageAlert(Alert.AlertType.INFORMATION, "", "Success");
            alert.showAndWait();

        }
    }

    private void onKeyPressed(KeyEvent keyEvent)
    {
        if (keyEvent.isControlDown())
        {
            if (keyEvent.getCode().equals(KeyCode.N))
            {
                list.getItems().addAll(new GraphModel("2"));
            }
            if (keyEvent.getCode().equals(KeyCode.Z))
            {
                CommandManager.getInstance().rollBack();
            }
        }
    }

    public void aboutMenu(ActionEvent actionEvent)
    {
        showModal(getClass().getResource("/fxml/AboutPage.fxml"), "About");


    }

    public void licenseMenu(ActionEvent actionEvent)
    {
        showModal(getClass().getResource("/fxml/LicensePage.fxml"), "License");

    }

    private void showModal(URL resource, String title)
    {
        final FXMLLoader loader = new FXMLLoader(resource);

        Parent root = null;
        try
        {
            root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.showAndWait();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void renumber(ActionEvent actionEvent)
    {
        List<NodeModel> nodes = list.getItems().stream().
                flatMap(graphModel -> graphModel.getNodes().stream()).collect(Collectors.toList());
        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).setId(i);
        }
        NodeModel.setCounter(nodes.size());
        drawPaneController.refresh();
    }

    public void manualMenu(ActionEvent actionEvent)
    {
        showModal(getClass().getResource("/fxml/HelpPage.fxml"), "Help");
    }
}
