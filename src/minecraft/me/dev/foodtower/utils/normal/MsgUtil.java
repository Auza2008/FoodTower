/*
Author:SuMuGod
Date:2022/7/10 5:35
Project:foodtower Reborn
*/
package me.dev.foodtower.utils.normal;

import me.dev.foodtower.ui.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public enum MsgUtil {

    INSTANCE;

    public static ArrayList<Notification> notifications = new ArrayList<>();

    public static void sendNotification(String message, Notification.Type type) {
        notifications.add(new Notification(message, type));
    }

    public void drawNotifications() {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        double startY = res.getScaledHeight() - 50;
        final double lastY = startY;
        for (int i = 0; i < notifications.size(); i++) {
            Notification not = notifications.get(i);
            if (not.shouldDelete()) {
                notifications.remove(i);
            } else if (notifications.size() > 10) {
                notifications.remove(i);
            }
            not.draw(startY, lastY);
            startY -= not.getHeight() + 3;
        }
    }

}
