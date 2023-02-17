/*
Author:SuMuGod
Date:2022/7/10 4:42
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.api.events.EventTick;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class FastUse extends Module {
    private final Mode<Enum<mods>> mod = new Mode<>("Mode", "Mode", mods.values(), mods.Packet);
    private final Numbers<Number> speed = new Numbers<>("Speed", "Speed", 10.0, 1.0, 10.0, 1.0);

    public FastUse() {
        super("FastUse", "亟食", new String[]{"fu", "fastesu"}, ModuleType.Player);
        setColor(new Color(43255).getRGB());
    }

    @NMSL
    public void onUpdate(EventPreUpdate e) {
        if (mod.getValue() == mods.Packet) {
            if (mc.thePlayer.getItemInUseDuration() >= speed.getValue().floatValue() && (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood)) {
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getItemInUse()));
                for (int i = 0; i < 30; ++i) {//++i比i++性能好
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C03PacketPlayer(mc.thePlayer.onGround));
                }
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                mc.thePlayer.sendQueue.getNetworkManager().sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
            }
        } else if (mod.getValue() == mods.Bypass && (mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood)) {
            if (mc.thePlayer.getItemInUseDuration() > 14) {
                for (int i = 0; i < 20; ++i) {
                    mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
                }
            }
        } else if (mod.getValue() == mods.Vanilla && isFood()) {
            for (int i = 0; i < 30; ++i) {
                mc.getNetHandler().addToSendQueue(new C03PacketPlayer(mc.thePlayer.onGround));
            }
        }
    }

    private boolean isFood() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemPotion || mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemFood;
    }

    enum mods {
        Vanilla, Packet, Bypass
    }
}