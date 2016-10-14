package pgen.service;

import pgen.model.EdgeModel;
import pgen.model.GraphModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

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
                    if(!edgeModel.getGlobal())
                        funcs+= String.format(FUNCTION_TEMPLATE,edgeModel.getFunc());
                }
            }
            String output = String.format(CLASS_TEMPLATE,graphModel.getName(),funcs);
            makeFile(output,graphModel.getName());

        }
        List<String > globalfunc = graphs.stream().flatMap(graphModel -> graphModel.getEdges().stream()).filter(EdgeModel::getGlobal).map(EdgeModel::getFunc).collect(Collectors.toList());
        String gfuncs = "";
        for(String func : globalfunc)
        {
            if(!func.equals(""))
            {
                    gfuncs+= String.format(FUNCTION_TEMPLATE,func);
            }
        }
        String output = String.format(CLASS_TEMPLATE,"Global",gfuncs);
        makeFile(output,"Global");
    }

    private void makeFile(String data,String fileName)
    {
        try
        {
            PrintWriter writer = new PrintWriter(dir.getPath()+fileName+".java");
            System.out.println("File created");
            writer.print(data);
            writer.close();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }
}
