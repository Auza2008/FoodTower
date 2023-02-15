/*
Author:SuMuGod
Date:2022/7/10 4:30
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.Killaura;
import me.dev.foodtower.utils.normal.PacketUtils;
import me.dev.foodtower.value.Mode;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.awt.*;

public class NoSlow extends Module {
    private final Mode mode = new Mode("Mode", "mode", NoSlowMode.values(), NoSlowMode.Vanilla);

    public NoSlow() {
        super("NoSlow", "无迟", new String[]{"NoSlow"}, ModuleType.Movement);
        this.setColor(new Color(255, 255, 255).getRGB());
    }

    @NMSL
    private void onUpdate(EventPreUpdate eventPreUpdate) {
        setSuffix(mode.getValue());
    }

    @NMSL
    private void onPacket(EventPacketRecieve e) {
        if (mode.getValue() == NoSlowMode.Hypixel && e.getPacket() instanceof S30PacketWindowItems && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking())) {
            e.setCancelled(true);
        }
    }

    @NMSL
    private void onMove(EventPreUpdate eventPreUpdate) {
        if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || Killaura.blocking) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "ncp":
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                    break;
                case "aac":
                    if (mc.thePlayer.ticksExisted % 3 == 0) {
                        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                }
                    break;
            }
        }
    }

    @NMSL
    private void onMove(EventPostUpdate eventPostUpdate) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "ncp":
                    if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || Killaura.blocking) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                    break;
                case "aac":
                    if (mc.thePlayer.ticksExisted % 3 != 0 && (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || Killaura.blocking)) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                    break;
                case "hypixel":
                    if ((!Client.instance.getModuleManager().getModuleByClass(Killaura.class).isEnabled() || !Killaura.blocking) && mc.thePlayer.getItemInUse() != null && mc.thePlayer.getItemInUse().getItem() != null) {
                        if (mc.thePlayer.isUsingItem() && (mc.thePlayer.getItemInUse().getItem() instanceof ItemFood || mc.thePlayer.getItemInUse().getItem() instanceof ItemBucketMilk || mc.thePlayer.getItemInUse().getItem() instanceof ItemPotion) && mc.thePlayer.getItemInUseCount() >= 1) {
                            PacketUtils.sendPacketNoEvent(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                        }
                    }
            }
        }

    enum NoSlowMode {
        Vanilla, Hypixel, NCP, AAC
    }
}
