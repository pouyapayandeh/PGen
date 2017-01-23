package pgen.service;

/**
 * Created by Pouya Payandeh on 10/13/2016.
 */
public class LLCell
{
    public final static int ERROR = 0;
    public final static int SHIFT = 1;
    public final static int GOTO = 2;
    public final static int PUSH_GOTO = 3;
    public final static int RETURN = 4;
    public final static int ACCEPT = 5;

    int action;
    int target;
    String func;

    public LLCell(int action, int target, String func)
    {
        this.action = action;
        this.target = target;
        this.func = func;
        if(this.func.equals("") || this.func == null)
            this.func =  "NoSem";
        else
            this.func = "@"+this.func;
    }

    @Override
    public String toString()
    {
        return String.format("%d %d %s", action,target,func);
    }
    public String getActionString()
    {
        switch (action)
        {
            case 0:
                return "Error";
            case 1:
                return "SHIFT";
            case 2:
                return "GOTO";
            case 3:
                return "PUSH_GOTO";
            case 4:
                return "RETURN";
            case 5:
                return "ACCEPT";
        }
        return "";
    }

}
