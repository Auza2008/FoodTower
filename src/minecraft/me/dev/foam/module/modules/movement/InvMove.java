package me.dev.foam.module.modules.movement;

import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventPacketRecieve;
import me.dev.foam.api.events.EventPreUpdate;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleType;
import me.dev.foam.utils.math.TimerUtil;
import me.dev.foam.value.Mode;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
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
            if (mc.currentScreen instanceof GuiContainer) {
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
    public void onPacket(final EventPacketRecieve event) {
        if (mode.getValue() == invMoveMode.Spoof) {
            if (event.getPacket() instanceof S2DPacketOpenWindow) {
                event.setCancelled(true);
            }
            if (event.getPacket() instanceof S2EPacketCloseWindow) {
                event.setCancelled(true);
            }
        }
    }
    enum invMoveMode {
        Vanilla,
        Spoof,
        Delay
    }
}

