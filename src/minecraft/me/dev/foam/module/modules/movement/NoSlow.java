/*
Author:SuMuGod
Date:2022/7/10 4:30
Project:foam Reborn
*/
package me.dev.foam.module.modules.movement;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPacketRecieve;
import me.dev.foam.api.events.EventPostUpdate;
import me.dev.foam.api.events.EventPreUpdate;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.module.modules.combat.Killaura;
import me.dev.foam.utils.normal.ChatUtils;
import me.dev.foam.utils.normal.Helper;
import me.dev.foam.value.Mode;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
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
        if (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking() || Killaura.blocking) {
            switch (mode.getValue().toString().toLowerCase()) {
                case "ncp":
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    break;
                case "aac":
                    if (mc.thePlayer.ticksExisted % 3 != 0) {
                        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()));
                    }
                    break;
            }
        }
    }

    enum NoSlowMode {
        Vanilla, Hypixel, NCP, AAC
    }
}
