package pgen.graphics;

import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pgen.model.EdgeModel;
import pgen.controller.EdgePropertiesController;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Pouya Payandeh on 7/10/2016.
 */
public class BoundLine extends QuadCurve
{
    Anchor anchor;
    Text text;
    EdgeModel edge;


    BoundLine(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY)
    {
        startXProperty().bind(startX);
        startYProperty().bind(startY);
        endXProperty().bind(endX);
        endYProperty().bind(endY);
        setStroke(Color.FORESTGREEN);
        setStrokeWidth(4);
        setStrokeLineCap(StrokeLineCap.ROUND);
        setFill(Color.CORNSILK.deriveColor(0, 1.2, 1, 0));


    }

    public BoundLine(EdgeModel edge)
    {
        this(edge.getStart().xProperty(), edge.getStart().yProperty(), edge.getEnd().xProperty(), edge.getEnd().yProperty());

        controlXProperty().bind(edge.anchorXProperty());
        controlYProperty().bind(edge.anchorYProperty());

//        controlX2Property().bind(edge.anchorXProperty());
//        controlY2Property().bind(edge.anchorYProperty());
        anchor = new Anchor(Color.BLUE, edge.anchorXProperty(), edge.anchorYProperty(), 3);
        this.edge = edge;
        text = new Text(edge.getToken() + "/@" + edge.getFunc());
        text.xProperty().bind(edge.anchorXProperty().add(10));
        text.yProperty().bind(edge.anchorYProperty());
        text.setOnMousePressed(event ->
        {
            try
            {
                showPropertiesDialog();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            event.consume();
        });
    }

    private void showPropertiesDialog() throws java.io.IOException
    {

        final FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/EdgePropertiesDialog.fxml"));
        EdgePropertiesController controller = new EdgePropertiesController();
        loader.setController(controller);
        Parent root = loader.load();
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

    public Text getText()
    {

        return text;
    }
}