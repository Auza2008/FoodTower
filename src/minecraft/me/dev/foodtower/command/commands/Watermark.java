/*
Author:SuMuGod
Date:2022/7/10 3:54
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.command.Command;
import me.dev.foodtower.module.modules.render.HUD;
import me.dev.foodtower.utils.normal.Helper;

public class Watermark
        extends Command {
    public Watermark() {
        super("Watermark", new String[]{"wm"}, "", "Set Watermark");
    }

    @Override
    public String execute(String[] args) {
        if (args.length > 1) {
            HUD.wm = args[0];
        } else if (args.length < 1) {
            Helper.sendMessageWithoutPrefix("\u00a7bCorrect usage:\u00a77 .watermark <name>");
        }
        return null;
    }
}
