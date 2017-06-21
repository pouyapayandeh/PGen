package pgen.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.scene.control.ListView;
import pgen.model.EdgeModel;
import pgen.model.GraphModel;
import pgen.model.NodeModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by Pouya Payandeh on 9/6/2016.
 */
public class SaveLoadService
{
    File file;

    public SaveLoadService(File selectedDirectory)
    {
        file = selectedDirectory;
    }
    public void save(List<GraphModel> graphs)
    {
        List<GraphJSON> graphJSONs = graphs.stream().map(GraphJSON::new).collect(Collectors.toList());
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
       String out = gson.toJson(graphJSONs);
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(file);
            System.out.println("File Saved");
            writer.print(out);
            writer.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }
    public void load(ListView<GraphModel> list)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<GraphJSON> graphJSONs  = null;
        try
        {
            list.getItems().clear();
            graphJSONs = Arrays.asList(gson.fromJson(new FileReader(file),GraphJSON[].class));
            graphJSONs.forEach(graph ->
            {
                GraphModel graphModel = new GraphModel(graph.name);
                Map<Integer , NodeModel> nodes = new HashMap<>();
                graph.nodes.forEach(nodeJSON -> {
                    NodeModel node = new NodeModel(nodeJSON.x,nodeJSON.y,graphModel);
                    node.setId(nodeJSON.id);
                    node.setFinal(nodeJSON.isFinal);
                    nodes.put(node.getId() , node);
                    graphModel.getNodes().add(node);
                });
                if(graph.start != -1)
                    if(graphModel.getNodes().stream().anyMatch(nodeJSON -> nodeJSON.getId()==graph.start))
                        graphModel.setStart( graphModel.getNodes().stream().filter(nodeJSON -> nodeJSON.getId()==graph.start).findFirst().get());
                graph.edges.forEach(edgeJSON ->
                {
                    NodeModel start = nodes.get(edgeJSON.start);
                    NodeModel end = nodes.get(edgeJSON.end);
                    EdgeModel edge = new EdgeModel(start, end);
                    edge.setFunc(edgeJSON.func);
                    edge.setToken(edgeJSON.token);
                    edge.anchorXProperty().setValue(edgeJSON.anchorX);
                    edge.anchorYProperty().setValue(edgeJSON.anchorY);
                    edge.setGraph(edgeJSON.isGraph);
                    edge.setGlobal(edgeJSON.isGlobal);
                    start.getAdjacent().add(edge);

                });
                list.getItems().add(graphModel);
            });
            NodeModel.setCounter(list.getItems().stream().
                    flatMap(graphModel -> graphModel.getNodes().stream()).map(NodeModel::getId).max(Integer::compareTo).get()+1);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
