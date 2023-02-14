/*
Author:SuMuGod
Date:2022/7/10 4:41
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventChat;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.other.FriendManager;

import java.awt.*;

public class AutoAccept
        extends Module {
    public AutoAccept() {
        super("AutoAccept", "自可许者", new String[]{"TPAccept, autotp"}, ModuleType.Player);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
    }

    @NMSL
    private void onPacket(EventChat e) {
        if (e.getType() == 0) {
            String message = e.getMessage();
            if (this.gotTpaRequest(message)) {
                this.handleRequest(message, "/tpaccept", "Accepted teleport");
            }
            if (this.gotPartyRequest(message)) {
                this.handleRequest(message, "/party accept", "Accepted party invite");
            }
            if (this.gotFactionRequest(message)) {
                this.handleRequest(message, "/f join", "Accepted faction invitation");
            }
        }
    }

    private void handleRequest(String message, String messageToSend, String notificationMessage) {
        FriendManager.getFriends().forEach((friends, alias) -> {
            if (FriendManager.isFriend(FriendManager.getAlias(friends)) && message.contains(FriendManager.getAlias(friends))) {
                this.mc.thePlayer.sendChatMessage(String.valueOf(messageToSend) + " " + FriendManager.getAlias(friends));
            }
        });
    }

    private boolean gotTpaRequest(String message) {
        if (!((message = message.toLowerCase()).contains("has requested to teleport") || message.contains("to teleport to you") || message.contains("has requested that you teleport to them"))) {
            return false;
        }
        return true;
    }

    private boolean gotFactionRequest(String message) {
        if ((message = message.toLowerCase()).contains("has invited you to join") && !message.contains("party")) {
            return true;
        }
        return false;
    }

    private boolean gotPartyRequest(String message) {
        message = message.toLowerCase();
        return message.contains("has invited you to join their party");
    }
}
