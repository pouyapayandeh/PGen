package pgen.graphics;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pgen.cmd.CommandManager;
import pgen.cmd.DeleteEdgeCmd;
import pgen.model.EdgeModel;
import pgen.controller.EdgePropertiesController;

import java.io.IOException;

/**
 * Created by Pouya Payandeh on 7/10/2016.
 */
public class BoundLine extends CubicCurve
{
    Anchor anchor;
    Text text;
    EdgeModel edge;

    Path arrowEnd = new Path();

    BoundLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY)
    {
        startXProperty().bind(startX);
        startYProperty().bind(startY);
        endXProperty().bind(endX);
        endYProperty().bind(endY);
        setStroke(Color.ORANGE);
        setStrokeWidth(4);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0));


    }


    public BoundLine(EdgeModel edge)
    {
        this(edge.getStart().xProperty(), edge.getStart().yProperty(), edge.getEnd().xProperty(), edge.getEnd().yProperty());


        startXProperty().addListener(this::calArrow);
        startYProperty().addListener(this::calArrow);
        endXProperty().addListener(this::calArrow);
        endYProperty().addListener(this::calArrow);

        startXProperty().addListener(this::calCurve);
        startYProperty().addListener(this::calCurve);
        endXProperty().addListener(this::calCurve);
        endYProperty().addListener(this::calCurve);


        anchor = new Anchor(Color.BLUE, edge.anchorXProperty(), edge.anchorYProperty(), 5);

        anchor.setExternalMouse(() ->{ calCurve(null, 0, 0);calArrow(null, 0, 0);});
        this.edge = edge;
        text = new Text(edge.getToken() + "/@" + edge.getFunc());
        text.xProperty().bind(edge.anchorXProperty().add(10));
        text.yProperty().bind(edge.anchorYProperty());

        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteBtn = new MenuItem("Delete");
        MenuItem propertiesBtn = new MenuItem("Properties");

        calCurve(null, 0, 0);
        calArrow(null, 0, 0);


        contextMenu.getItems().addAll(deleteBtn,propertiesBtn);
        propertiesBtn.setOnAction(event -> showPropertiesDialog());
        deleteBtn.setOnAction(event -> {
            CommandManager.getInstance().applyCommand(new DeleteEdgeCmd(edge));
        });
        text.setOnMousePressed(event ->
        {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
                event.consume();
        });
        text.setOnMouseReleased(Event::consume);
    }

    public Path getArrowEnd()
    {
        return arrowEnd;
    }


    public void calArrow(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
    {

        Point2D ori = eval(this, 0.9f);
        Point2D tan = evalDt(this, 0.9f).normalize().multiply(50);

        arrowEnd.getElements().clear();

        arrowEnd.getElements().add(new MoveTo(ori.getX() - 0.2 * tan.getX() - 0.2 * tan.getY(),
                ori.getY() - 0.2 * tan.getY() + 0.2 * tan.getX()));
        arrowEnd.getElements().add(new LineTo(ori.getX(), ori.getY()));
        arrowEnd.getElements().add(new LineTo(ori.getX() - 0.2 * tan.getX() + 0.2 * tan.getY(),
                ori.getY() - 0.2 * tan.getY() - 0.2 * tan.getX()));
    }

    public void calCurve(ObservableValue<? extends Number> observable, Number oldValue, Number newValue)
    {


//        double x1 = 2*edge.getAnchorX() - getStartX()/2 - getEndX()/2;
//        double y1 = 2*edge.getAnchorY() - getStartY()/2 - getEndY()/2;

        double x1 = 4.0/3.0*(edge.getAnchorX() - 1.0/8.0*(getStartX() + getEndX()));
        double y1 = 4.0/3.0*(edge.getAnchorY() - 1.0/8.0*(getStartY() + getEndY()));


        controlX1Property().setValue(x1);
        controlY1Property().setValue(y1);

        controlX2Property().setValue(x1);
        controlY2Property().setValue(y1);

        if(edge.getStart() == edge.getEnd())
        {
            double Ax = x1*2;
            double Ay = y1*2;
            x1 = (Ax+Ay)/2.0-(getStartY());
            y1 = (Ay-Ax)/2.0+(getStartX());

            controlX1Property().setValue(x1);
            controlY1Property().setValue(y1);

            double xc = getStartX() + getStartY() - y1;
            double yc = getStartY() -(getStartX() - x1);
            controlX2Property().setValue(xc);
            controlY2Property().setValue(yc);
        }
//        System.out.println("Anchor " + edge.getAnchorX() + " " +edge.getAnchorY());
//        System.out.println("Control " + x1 + " " +y1);

        Point2D ori = eval(this, 0.5);

//        System.out.println("Ori " + ori.getX()+ " " +ori.getY());
        anchor.setCenterX(ori.getX());
        anchor.setCenterY(ori.getY());

    }
    private void showPropertiesDialog()
    {

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EdgePropertiesDialog.fxml"));
        EdgePropertiesController controller = new EdgePropertiesController();
        loader.setController(controller);
        Parent root = null;
        try
        {
            root = loader.load();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        controller.init(edge);
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
        System.out.println("Finisheed");
    }

    public Anchor getAnchor()
    {
        return anchor;
    }

    /**
     * Evaluate the quad curve at a parameter 0<=t<=1, returns a Point2D
     *
     * @param c the QuadCurve
     * @param t param between 0 and 1
     * @return a Point2D
     */
    private Point2D eval(QuadCurve c, double t)
    {
        Point2D p = new Point2D(Math.pow(1 - t, 2) * c.getStartX() +
                2 * t * (1 - t) * c.getControlX() +
                Math.pow(t, 2) * c.getEndX(),
                Math.pow(1 - t, 2) * c.getStartY() +
                        2 * t * (1 - t) * c.getControlY() +
                        Math.pow(t, 2) * c.getEndY());
        return p;
    }

    /**
     * Evaluate the tangent of the quad curve at a parameter 0<=t<=1, returns a Point2D
     *
     * @param c the QuadCurve
     * @param t param between 0 and 1
     * @return a Point2D
     */
    private Point2D evalDt(QuadCurve c, double t)
    {
        Point2D p = new Point2D(2*(1-t)*(c.getControlX()-c.getStartX())+ 2*(t)*(c.getEndX()-c.getControlX()),
                2*(1-t)*(c.getControlY()-c.getStartY())+ 2*(t)*(c.getEndY()-c.getControlY()));
        return p;
    }

    private Point2D eval(CubicCurve c, double t)
    {
        Point2D p = new Point2D(Math.pow(1 - t, 3) * c.getStartX() +
                3 * t * Math.pow(1 - t, 2) * c.getControlX1() +
                3 * (1 - t) * t * t * c.getControlX2() +
                Math.pow(t, 3) * c.getEndX(),
                Math.pow(1 - t, 3) * c.getStartY() +
                        3 * t * Math.pow(1 - t, 2) * c.getControlY1() +
                        3 * (1 - t) * t * t * c.getControlY2() +
                        Math.pow(t, 3) * c.getEndY());
        return p;
    }
    private Point2D evalDt(CubicCurve c, double t)
    {
        Point2D p = new Point2D(-3 * Math.pow(1 - t, 2) * c.getStartX() +
                3 * (Math.pow(1 - t, 2) - 2 * t * (1 - t)) * c.getControlX1() +
                3 * ((1 - t) * 2 * t - t * t) * c.getControlX2() +
                3 * Math.pow(t, 2) * c.getEndX(),
                -3 * Math.pow(1 - t, 2) * c.getStartY() +
                        3 * (Math.pow(1 - t, 2) - 2 * t * (1 - t)) * c.getControlY1() +
                        3 * ((1 - t) * 2 * t - t * t) * c.getControlY2() +
                        3 * Math.pow(t, 2) * c.getEndY());
        return p;
    }
    public Text getText()
    {

        return text;
    }
}