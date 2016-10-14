package pgen.graphics;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Pouya Payandeh on 7/10/2016.
 */
public class Anchor extends Circle {
    Anchor(Color color, DoubleProperty x, DoubleProperty y, double r) {
        super(x.get(), y.get(), r);
        setFill(color.deriveColor(1, 1, 1, 1));
        setStroke(color);
        setStrokeWidth(2);
        setStrokeType(StrokeType.OUTSIDE);

        setStyle("-fx-background-color: crimson");
        x.bind(centerXProperty());
        y.bind(centerYProperty());
        enableDrag();
        setMouseTransparent(false);
    }

    public void setExternalMouse(Runnable externalMouse)
    {
        this.externalMouse = externalMouse;
    }

    Runnable externalMouse=() -> {};
    // make a node movable by dragging it around with the mouse.
    private void enableDrag() {
        final Delta dragDelta = new Delta();
        setOnMouseClicked(event -> {event.consume();});
        setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            mouseEvent.consume();
            dragDelta.x = getCenterX() - mouseEvent.getX();
            dragDelta.y = getCenterY() - mouseEvent.getY();
            getScene().setCursor(Cursor.MOVE);

        });
        setOnMouseReleased(mouseEvent -> {
            mouseEvent.consume();
            getScene().setCursor(Cursor.HAND);

        });
        setOnMouseDragged(mouseEvent -> {
            mouseEvent.consume();
            double newX = mouseEvent.getX() + dragDelta.x;
            if (newX > 0 && newX < getScene().getWidth()) {
                setCenterX(newX);
            }
            double newY = mouseEvent.getY() + dragDelta.y;
            if (newY > 0 && newY < getScene().getHeight()) {
                setCenterY(newY);
            }
            externalMouse.run();

        });
        setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.HAND);
            }
        });
        setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                getScene().setCursor(Cursor.DEFAULT);
            }
        });
    }

    // records relative x and y co-ordinates.
    private class Delta { double x, y; }
}