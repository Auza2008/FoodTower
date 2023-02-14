/*
Author:SuMuGod
Date:2022/7/10 4:41
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.Event;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.Notification;
import me.dev.foodtower.utils.normal.MsgUtil;
import me.dev.foodtower.value.Numbers;
import net.minecraft.event.ClickEvent;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.IChatComponent;

public class AutoPlay extends Module {

    private final Numbers<Double> Delay = new Numbers<>("AutoPlay Delay", "Delay", 3.0D, 1.0D, 6.0D, 1.0D);

    public AutoPlay() {
        super("AutoPlay", "", new String[]{"ap"}, ModuleType.Player);
    }

    @NMSL
    public void onUpdate(Event event) {
        for (IChatComponent cc : event.getChatComponent().getSiblings()) {
            final ClickEvent ce = cc.getChatStyle().getChatClickEvent();
            if (ce != null)
                if ((ce.getAction() == ClickEvent.Action.RUN_COMMAND) && ce.getValue().contains("/play")) {
                    MsgUtil.sendNotification("You will play again in " + Delay.getMaximum() + "s", Notification.Type.INFO);
                    new Thread(() -> {
                        try {
                            Thread.sleep(Delay.getMinimum().shortValue() * 1000L);
                        } catch (final InterruptedException a) {
                            a.printStackTrace();
                        }
                        mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(ce.getValue()));
                    }).start();

                    event.setCancelled(true);
                }
        }
    }
}
