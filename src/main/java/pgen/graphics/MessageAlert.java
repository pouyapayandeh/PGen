package pgen.graphics;

import javafx.beans.NamedArg;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

/**
 * Created by Pouya Payandeh on 9/18/2016.
 */
public class MessageAlert extends Alert
{
    public MessageAlert(@NamedArg("alertType") AlertType alertType)
    {
        super(alertType);
    }
    public MessageAlert(AlertType alertType, String text,String header)
    {
        super(alertType);
        setTitle("Messages Dialog");
        setHeaderText(header);
        setContentText("");
        Label label = new Label("");

        TextArea textArea = new TextArea(text);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        this.getDialogPane().setExpandableContent(expContent);
    }

}
