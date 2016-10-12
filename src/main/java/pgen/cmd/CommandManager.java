package pgen.cmd;

import pgen.controller.RefreshableController;

import java.util.Stack;

/**
 * Created by Pouya Payandeh on 9/3/2016.
 */
public class CommandManager
{
    private static CommandManager instance;
    Stack<Command> cmds;
    RefreshableController controller;

    private CommandManager(RefreshableController controller)
    {
        this.controller = controller;
        cmds = new Stack<>();
    }

    public static CommandManager getInstance()
    {
        return instance;
    }

    public static void init(RefreshableController controller)
    {
        instance = new CommandManager(controller);
    }

    public void applyCommand(Command command)
    {
        command.apply();
        cmds.push(command);
        controller.refresh();
    }

    public void rollBack()
    {
        if (cmds.size() > 0)
        {
            cmds.pop().rollBack();
            controller.refresh();

        }
    }
}
