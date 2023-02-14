/*
Author:SuMuGod
Date:2022/7/10 3:54
Project:foodtower Reborn
*/
package me.dev.foodtower.command;

import me.dev.foodtower.api.EventBus;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventChat;
import me.dev.foodtower.command.commands.*;
import me.dev.foodtower.utils.client.Manager;
import me.dev.foodtower.utils.normal.Helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CommandManager
        implements Manager {
    private List<Command> commands;

    @Override
    public void init() {
        this.commands = new ArrayList<>();
        this.commands.add(new Command("test", new String[]{"test"}, "", "testing") {
            @Override
            public String execute(String[] args) {
                return null;
            }
        });
        this.commands.add(new Help());
        this.commands.add(new Toggle());
        this.commands.add(new Bind());
        this.commands.add(new VClip());
        this.commands.add(new Watermark());
        this.commands.add(new Cheats());
        this.commands.add(new ConfigCmd());
        this.commands.add(new Hide());
        this.commands.add(new Enchant());
        this.commands.add(new New());
        this.commands.add(new ValueCmd());
        EventBus.getInstance().register(this);
    }

    public List<Command> getCommands() {
        return this.commands;
    }

    public Optional<Command> getCommandByName(String name) {
        return this.commands.stream().filter(c2 -> {
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

    public void add(Command command) {
        this.commands.add(command);
    }

    @NMSL
    private void onChat(EventChat e) {
        if (e.getMessage().length() > 1 && e.getMessage().startsWith(".")) {
            e.setCancelled(true);
            String[] args = e.getMessage().trim().substring(1).split(" ");
            Optional<Command> possibleCmd = this.getCommandByName(args[0]);
            if (possibleCmd.isPresent()) {
                String result = possibleCmd.get().execute(Arrays.copyOfRange(args, 1, args.length));
                if (result != null && !result.isEmpty()) {
                    Helper.sendMessage(result);
                }
            } else {
                Helper.sendMessage(String.format("> Command not found Try '%shelp'", "."));
            }
        }
    }

}

