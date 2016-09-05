package pgen.service;

import pgen.model.EdgeModel;
import pgen.model.GraphModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Created by Pouya Payandeh on 9/5/2016.
 */
public class ExportService
{
    File dir;
    String FUNCTION_TEMPLATE="public static void %s()\n{\n}";
    String CLASS_TEMPLATE="public class %s\n{%s}\n";

    public ExportService(File selectedDirectory)
    {
        dir = selectedDirectory;
    }
    public void exportGraphs(List<GraphModel> graphs)
    {
        dir.mkdirs();

        for(GraphModel graphModel : graphs)
        {
            String funcs = "";
            for(EdgeModel edgeModel : graphModel.getEdges())
            {
                if(!edgeModel.getFunc().equals(""))
                {
                    funcs+= String.format(FUNCTION_TEMPLATE,edgeModel.getFunc());
                }
            }
            String output = String.format(CLASS_TEMPLATE,graphModel.getName(),funcs);
            try
            {
                PrintWriter writer = new PrintWriter(dir.getPath()+graphModel.getName()+".java");
                System.out.println("File created");
                writer.print(output);
                writer.close();
            } catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

        }

    }
}
