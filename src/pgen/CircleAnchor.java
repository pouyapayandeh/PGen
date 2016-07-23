package pgen;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

/**
 * Created by Pouya Payandeh on 7/10/2016.
 */
public class CircleAnchor extends Circle
{
    BooleanProperty isFinal = new SimpleBooleanProperty();
    CircleAnchor(Color color, DoubleProperty x, DoubleProperty y)
    {
        super(x.get(), y.get(), 10);
        setFill(color.deriveColor(1, 1, 1, 0.5));
        setStroke(color);
        setStrokeWidth(2);
        setStrokeType(StrokeType.OUTSIDE);
        x.bind(centerXProperty());
        y.bind(centerYProperty());
        enableDrag();
        setMouseTransparent(false);
    }
    CircleAnchor(NodeModel node)
    {
        this(Color.AQUA,node.x,node.y);
       //
        isFinal.addListener((observable, oldValue, newValue) ->
        {
            setStroke(Color.BLACK);
            getStrokeDashArray().add(2d);
        });
        isFinal.bindBidirectional(node.isFinal);

    }
    EventHandler<MouseEvent> onClick;
//
//    public void setOnClick(EventHandler<MouseEvent> onClick)
//    {
//        this.onClick = onClick;
//    }

    // make a node movable by dragging it around with the mouse.
    private void enableDrag()
    {
        final Delta dragDelta = new Delta();
        setOnMouseClicked(event -> {
//                onClick.handle(event);
                isFinal.set(true);
            System.out.println("Here");
                event.consume();
        });

        setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = getCenterX() - mouseEvent.getX();
            dragDelta.y = getCenterY() - mouseEvent.getY();
            getScene().setCursor(Cursor.MOVE);
        });
        setOnMouseReleased(mouseEvent -> {
            getScene().setCursor(Cursor.HAND);
        });
        setOnMouseDragged(mouseEvent -> {
            double newX = mouseEvent.getX() + dragDelta.x;
            if (newX > 0 && newX < getScene().getWidth())
            {
                setCenterX(newX);
            }
            double newY = mouseEvent.getY() + dragDelta.y;
            if (newY > 0 && newY < getScene().getHeight())
            {
                setCenterY(newY);
            }

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
    private class Delta { double x, y; }
}