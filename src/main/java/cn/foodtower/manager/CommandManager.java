package cn.foodtower.manager;

import cn.foodtower.api.EventBus;
import cn.foodtower.command.Command;
import cn.foodtower.command.commands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CommandManager implements Manager {
    public static List<Command> commands;

    public static Optional<Command> getCommandByName(String name) {
        return CommandManager.commands.stream().filter(c2 -> {
            boolean isAlias = false;
            String[] arrstring = c2.getAlias();
            int n = arrstring.length;
            int n2 = 0;
            while (n2 < n) {
                String str = arrstring[n2];
                if (str.equalsIgnoreCase(name)) {
                    isAlias = true;
                    break;
                }
                ++n2;
            }
            return c2.getName().equalsIgnoreCase(name) || isAlias;
        }).findFirst();
    }

    @Override
    public void init() {
        commands = new ArrayList<>();
        commands.add(new Help());
        commands.add(new Toggle());
        commands.add(new Bind());
        commands.add(new VClip());
        commands.add(new Config());
        commands.add(new ConfigManagerCommand());
        commands.add(new Hidden());
        commands.add(new Say());
        commands.add(new NotificationTest());
        commands.add(new Tp());
        commands.add(new Spammer());
        commands.add(new AutoOnlySword());

        EventBus.getInstance().register(this);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void add(Command command) {
        commands.add(command);
    }


}
