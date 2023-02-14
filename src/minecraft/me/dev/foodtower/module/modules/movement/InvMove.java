package me.dev.foodtower.module.modules.movement;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventPacketRecieve;
import me.dev.foodtower.api.events.EventPacketSend;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

import java.util.Arrays;
import java.util.List;

public class InvMove extends Module {
    private final TimerUtil delayTimer = new TimerUtil();
    private final Mode mode = new Mode("Mode", "mode", invMoveMode.values(), invMoveMode.Spoof);
    public InvMove() {
        super("InvMove", "背包而行", new String[]{}, ModuleType.Movement);
    }

    private static final List<KeyBinding> keys = Arrays.asList(
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump
    );

    public static void updateStates() {
        if (mc.currentScreen != null) {
            keys.forEach(k -> k.pressed = GameSettings.isKeyDown(k));
        }
    }

    @NMSL
    public void onMotion(final EventPreUpdate event) {
        if (mode.getValue() == invMoveMode.Spoof) {
            if (mc.currentScreen instanceof GuiContainer) {
                updateStates();
            }
        }
        if (mode.getValue() == invMoveMode.Vanilla) {
            if (Minecraft.currentScreen instanceof GuiContainer) {
                updateStates();
            }
        }
        if (mode.getValue() == invMoveMode.NoOpenPacket) {
            if (Minecraft.currentScreen instanceof GuiContainer) {
                updateStates();
            }
        }
        if (mode.getValue() == invMoveMode.Delay) {
            if (mc.currentScreen instanceof GuiContainer) {
                if (delayTimer.hasReached(100)) {
                    updateStates();
                    delayTimer.reset();
                }
            }
        }
    }

    @NMSL
    public void onPacket(final EventPacketSend event) {
        if (mode.getValue() == invMoveMode.Spoof) {
            if (event.getPacket() instanceof C16PacketClientStatus && ((C16PacketClientStatus) event.getPacket()).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof C0DPacketCloseWindow) {
                event.setCancelled(true);
            }
        } else if (mode.getValue() == invMoveMode.NoOpenPacket) {
            if (event.getPacket() instanceof C16PacketClientStatus && ((C16PacketClientStatus) event.getPacket()).getStatus() == C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT) {
                event.setCancelled(true);
            }
        }
    }
    enum invMoveMode {
        Vanilla,
        NoOpenPacket,
        Spoof,
        Delay
    }
}

