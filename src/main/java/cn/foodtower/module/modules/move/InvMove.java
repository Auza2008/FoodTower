package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventMotion;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventPacket;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C16PacketClientStatus;

import java.awt.*;
import java.util.concurrent.LinkedBlockingQueue;

public class InvMove extends Module {
    private final Mode modeValue = new Mode("Mode", InvMoveM.values(), InvMoveM.Vanilla);
    private final Mode sprintModeValue = new Mode("InvSprint", sprint.values(), sprint.Keep);
    private final Option noDetectableValue = new Option("NoDetectable", false);
    private final LinkedBlockingQueue playerPackets = new LinkedBlockingQueue<C03PacketPlayer>();

    public InvMove() {
        super("InvMove", new String[]{"InvMove", "crit"}, ModuleType.Movement);
        this.setColor(new Color(235, 194, 138).getRGB());
        this.addValues(modeValue, sprintModeValue, noDetectableValue);
    }

    @EventHandler
    private void onUpdate(EventMotionUpdate event) {
        Speed speedModule = (Speed) ModuleManager.getModuleByClass(Speed.class);
        if (mc.currentScreen == null) return;
        if (!(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiIngameMenu) && (!noDetectableValue.get() || !(mc.currentScreen instanceof GuiContainer))) {
            mc.gameSettings.keyBindForward.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindForward);
            mc.gameSettings.keyBindBack.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindBack);
            mc.gameSettings.keyBindRight.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindRight);
            mc.gameSettings.keyBindLeft.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindLeft);
            if (!speedModule.isEnabled())
                mc.gameSettings.keyBindJump.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindJump);
            mc.gameSettings.keyBindSprint.pressed = GameSettings.isKeyDown(mc.gameSettings.keyBindSprint);
            if (sprintModeValue.get().equals(sprint.Stop)) {
                mc.thePlayer.setSprinting(false);
            }
        }
    }

    @EventHandler
    private void onMotion(EventMotion event) {
        if (event.isPre() && playerPackets.size() > 0 && (mc.currentScreen == null || mc.currentScreen instanceof GuiChat || mc.currentScreen instanceof GuiIngameMenu || (noDetectableValue.get() && mc.currentScreen instanceof GuiContainer))) {
            for (Object i : playerPackets) {
                mc.getNetHandler().addToSendQueue((Packet) i);
            }
            playerPackets.clear();
        }
    }

//    @EventHandler
//    private void onClick(EventClickWindow event) {
//        if (noMoveClicksValue.get() && MoveUtils.isMoving()) event.setCancelled(true);
//    }

    @EventHandler
    private void onPacket(EventPacket event) {
        Packet packet = event.packet;
        switch (modeValue.get().toString().toLowerCase()) {
            case "silent":
                if (packet instanceof C16PacketClientStatus && ((C16PacketClientStatus) packet).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT)
                    event.setCancelled(true);
                break;
            case "blink":
                if (mc.currentScreen != null && !(mc.currentScreen instanceof GuiChat) && !(mc.currentScreen instanceof GuiIngameMenu) && (!noDetectableValue.get() || !(mc.currentScreen instanceof GuiContainer)) && packet instanceof C03PacketPlayer) {
                    event.setCancelled(true);
                    playerPackets.add(packet);
                }
        }
    }

    @Override
    public void onDisable() {
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindForward) || mc.currentScreen != null)
            mc.gameSettings.keyBindForward.pressed = false;
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindBack) || mc.currentScreen != null)
            mc.gameSettings.keyBindBack.pressed = false;
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight) || mc.currentScreen != null)
            mc.gameSettings.keyBindRight.pressed = false;
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft) || mc.currentScreen != null)
            mc.gameSettings.keyBindLeft.pressed = false;
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindJump) || mc.currentScreen != null)
            mc.gameSettings.keyBindJump.pressed = false;
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSprint) || mc.currentScreen != null)
            mc.gameSettings.keyBindSprint.pressed = false;
    }

    private enum InvMoveM {
        Vanilla, Silent, Blink
    }

    private enum sprint {
        Keep, Stop
    }

//    private void isAACAP(): Boolean = sprintModeValue.get().equals("aacap", true) && mc.currentScreen != null && mc.currentScreen !is GuiChat && mc.currentScreen !is GuiIngameMenu && (!noDetectableValue.get() || mc.currentScreen !is GuiContainer)
}
