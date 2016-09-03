package pgen.cmd;

/**
 * Created by Pouya Payandeh on 7/23/2016.
 */
public interface Command
{
    void apply();
    void rollBack();
}
