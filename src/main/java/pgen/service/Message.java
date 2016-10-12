package pgen.service;

/**
 * Created by Pouya Payandeh on 9/18/2016.
 */
public class Message
{
    public final static int ERROR = 0;

    public int getType()
    {
        return type;
    }

    public void setType(int type)
    {
        this.type = type;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public final static int INFO = 1;
    int type;
    String message;

    public Message(int type, String message)
    {
        this.type = type;
        this.message = message;
    }
}
