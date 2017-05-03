package pgen.graphics;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import pgen.cmd.CommandManager;
import pgen.cmd.DeleteNodeCmd;
import pgen.model.NodeModel;

/**
 * Created by Pouya Payandeh on 8/20/2016.
 */
public class GraphNode extends StackPane
{
    private final double radius = 10;
    Circle circle;
    Text text;

    public NodeModel getNode()
    {
        return node;
    }

    NodeModel node;
    Color color = Color.AQUA;

    EventHandler<MouseEvent> onShiftClick;


    double mouseX;
    double mouseY;

    public GraphNode(NodeModel n)
    {
        this.node = n;
        circle = new Circle(n.getX(), n.getY(), radius);

        circle.setFill(color.deriveColor(1, 1, 1,1));
        circle.setStroke(color);
        circle.setStrokeWidth(3);
        circle.setStrokeType(StrokeType.OUTSIDE);

        setLayoutX(n.getX() -radius);
        setLayoutY(n.getY() -radius);
        n.xProperty().bind(layoutXProperty().add(radius));
        n.yProperty().bind(layoutYProperty().add(radius));

        if(n.getFinal())
        {
            circle.setFill(Color.RED);
        }else
        {
            circle.setFill(color.deriveColor(1, 1, 1,1));
        }
        if(n.getGraph() != null && n.getGraph().getStart() ==n)
        {
            circle.setStroke(Color.GREEN);
        }else
        {
            circle.setStroke(color);
        }

        n.finalProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue)
            {
                circle.setFill(Color.RED);
            }else
            {
                circle.setFill(color.deriveColor(1, 1, 1,1));
            }
        });

        n.startProperty().addListener((observable, oldValue, newValue) ->
        {
            if(newValue)
            {
                circle.setStroke(Color.GREEN);
            }else
            {
                circle.setStroke(color);
            }
        });
        text = new Text(String.valueOf(n.getId()));
        text.setBoundsType(TextBoundsType.VISUAL);
        getChildren().addAll(circle, text);
        setAlignment(Pos.CENTER);
        //  setPrefSize(30,30);
//        setStyle("-fx-background-color:crimson");

        enableDrag();
        setMouseTransparent(false);


// ...

    }
//
//    public void setOnClick(EventHandler<MouseEvent> onClick)
//    {
//        this.onClick = onClick;
//    }

    public EventHandler<MouseEvent> getOnShiftClick()
    {
        return onShiftClick;
    }

    public void setOnShiftClick(EventHandler<MouseEvent> onShiftClick)
    {
        this.onShiftClick = onShiftClick;
    }

    // make a node movable by dragging it around with the mouse.
    private void enableDrag()
    {
        setOnMousePressed(event -> {
            if (!event.isShiftDown())
            {
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                System.out.println(mouseY);
            } else
            {
                  //  System.out.println(node.id);

            }
            event.consume();
        });
        setOnMouseClicked(event -> {

//                onClick.handle(event);
            System.out.println("Here");
            if(event.isShiftDown() && onShiftClick != null)
            {
                onShiftClick.handle(event);
            }
            if(event.getButton().equals(MouseButton.SECONDARY))
            {

                showContextMenu(event);
            }
            event.consume();
        });
        setOnMouseDragged(event -> {
            if (!event.isShiftDown())
            {
                double deltaX = event.getSceneX() - mouseX;
                double deltaY = event.getSceneY() - mouseY;
                Bounds b = getBoundsInParent();
                if (b.getMinX() + deltaX < 0 || b.getMaxX() + deltaX > getParent().getLayoutBounds().getWidth())
                    deltaX = 0;
                if (b.getMinY() + deltaY < 0 || b.getMaxY() + deltaY > getParent().getLayoutBounds().getHeight())
                    deltaY = 0;
                relocate(getLayoutX() + deltaX, getLayoutY() + deltaY);
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
            }
            else
            {

            }
        });
        setOnMouseReleased(mouseEvent -> {
            getScene().setCursor(Cursor.HAND);
            mouseEvent.consume();
        });
        setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown())
            {
                getScene().setCursor(Cursor.HAND);
            }
        });
        setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown())
            {
                getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }
    ContextMenu contextMenu = new ContextMenu();
    private void showContextMenu(MouseEvent event)
    {

        MenuItem deleteBtn = new MenuItem("Delete");

        deleteBtn.setOnAction(this::delete);
        CheckMenuItem finalBtn = new CheckMenuItem("Final");
        finalBtn.selectedProperty().set(node.getFinal());
        node.finalProperty().bind(finalBtn.selectedProperty());

        CheckMenuItem startBtn = new CheckMenuItem("Start");
        startBtn.setOnAction(event1 -> {
            node.getGraph().setStart(node);
        });
        contextMenu.getItems().clear();
        contextMenu.getItems().addAll(deleteBtn, finalBtn, startBtn);
        contextMenu.show(this, event.getScreenX(), event.getScreenY());
    }

    private void delete(ActionEvent actionEvent)
    {
        CommandManager.getInstance().applyCommand(new DeleteNodeCmd(node));
    }

}
