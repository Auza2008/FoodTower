/*
Author:SuMuGod
Date:2022/7/10 3:52
Project:foodtower Reborn
*/
package me.dev.foodtower.command.commands;

import me.dev.foodtower.command.Command;
import me.dev.foodtower.utils.math.MathUtil;
import me.dev.foodtower.utils.normal.Helper;
import net.minecraft.util.EnumChatFormatting;

public class VClip
        extends Command {

    public VClip() {
        super("Vc", new String[]{"Vclip", "clip", "verticalclip", "clip"}, "", "Teleport down a specific ammount");
    }

    @Override
    public String execute(String[] args) {
        if (!Helper.onServer("foodtower")) {
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
            Helper.sendMessage("> You cannot use vclip on the foodtower Server.");
        }
        return null;
    }
}
