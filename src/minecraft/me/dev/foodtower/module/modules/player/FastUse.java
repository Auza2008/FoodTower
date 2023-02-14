/*
Author:SuMuGod
Date:2022/7/10 4:42
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;

import java.awt.*;

public class FastUse extends Module {

    public FastUse() {
        super("FastUse", "亟食", new String[]{"fu", "fastesu"}, ModuleType.Player);
        setColor(new Color(0xFFFFFF).getRGB());
    }

    @NMSL
    public void onUpdate(EventPreUpdate e) {
        Item usingItem = mc.thePlayer.getItemInUse().getItem();

        if (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion) {
            for (int i = 0; i <= 32; i++) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
            }
            mc.playerController.onStoppedUsingItem(mc.thePlayer);
        }
    }
}