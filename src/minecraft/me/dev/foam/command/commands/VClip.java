/*
Author:SuMuGod
Date:2022/7/10 3:52
Project:foam Reborn
*/
package me.dev.foam.command.commands;

import me.dev.foam.command.Command;
import me.dev.foam.utils.math.MathUtil;
import me.dev.foam.utils.normal.Helper;
import net.minecraft.util.EnumChatFormatting;

public class VClip
        extends Command {

    public VClip() {
        super("Vc", new String[]{"Vclip", "clip", "verticalclip", "clip"}, "", "Teleport down a specific ammount");
    }

    @Override
    public String execute(String[] args) {
        if (!Helper.onServer("foam")) {
            if (args.length > 0) {
                if (MathUtil.parsable(args[0], (byte) 4)) {
                    float distance = Float.parseFloat(args[0]);
                    Helper.mc.thePlayer.setPosition(Helper.mc.thePlayer.posX, Helper.mc.thePlayer.posY + (double) distance, Helper.mc.thePlayer.posZ);
                    Helper.sendMessage("> Vclipped " + distance + " blocks");
                } else {
                    this.syntaxError((Object) ((Object) EnumChatFormatting.GRAY) + args[0] + " is not a valid number");
                }
            } else {
                this.syntaxError((Object) ((Object) EnumChatFormatting.GRAY) + "Valid .vclip <number>");
            }
        } else {
            Helper.sendMessage("> You cannot use vclip on the foam Server.");
        }
        return null;
    }
}
