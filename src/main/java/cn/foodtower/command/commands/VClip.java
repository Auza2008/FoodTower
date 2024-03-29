package cn.foodtower.command.commands;

import cn.foodtower.command.Command;
import cn.foodtower.util.math.MathUtil;
import cn.foodtower.util.misc.Helper;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.EnumChatFormatting;

public class VClip extends Command {
    private final TimerUtil timer = new TimerUtil();

    public VClip() {
        super("Vc", new String[]{"Vclip", "clip", "verticalclip", "clip"}, "", "向下传送指定格数");
    }

    @Override
    public String execute(String[] args) {
        if (args.length > 0) {
            if (MathUtil.parsable(args[0], (byte) 4)) {
                float distance = Float.parseFloat(args[0]);
                Minecraft.getMinecraft().getNetHandler().addToSendQueue(
                        new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX,
                                Minecraft.getMinecraft().thePlayer.posY + (double) distance,
                                Minecraft.getMinecraft().thePlayer.posZ, false));
                Minecraft.getMinecraft().thePlayer.setPosition(Minecraft.getMinecraft().thePlayer.posX,
                        Minecraft.getMinecraft().thePlayer.posY + (double) distance,
                        Minecraft.getMinecraft().thePlayer.posZ);
                Helper.sendMessage("Vclipped " + distance + " blocks");
            } else {
                Helper.sendMessage(EnumChatFormatting.GRAY + args[0] + " is not a valid number");
            }
        } else {
            Helper.sendMessage(EnumChatFormatting.GRAY + "Correct usage .vclip <number>");
        }
        return null;
    }
}
