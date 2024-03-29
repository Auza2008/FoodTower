package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventJump;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.MovementUtils;

public class NoWeb extends Module {
    private final Mode mode = new Mode("Mode", Modes.values(), Modes.None);
    private final Numbers<Double> horizonSpeed = new Numbers<>("HorizonSpeed", 0.1d, 0.01d, 0.8d, 0.01d);
    boolean usedTimer = false;

    public NoWeb() {
        super("NoWeb", new String[]{"noweb"}, ModuleType.Movement);
    }

    @EventHandler
    public void onJump(EventJump e) {
        if (mode.get().equals(Modes.AAC4)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onUpdate(EventMotionUpdate e) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F;
            usedTimer = false;
        }
        if (!mc.thePlayer.isInWeb()) {
            return;
        }


        switch ((Modes) mode.get()) {
            case None:
                mc.thePlayer.isInWeb = false;
                break;
            case OldAAC: {
                mc.thePlayer.jumpMovementFactor = 0.59f;

                if (!mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = 0.0;
                }
                break;
            }
            case LAAC: {
                mc.thePlayer.jumpMovementFactor = (mc.thePlayer.movementInput.moveStrafe != 0f) ? 1.0f
                        : 1.21f;

                if (!mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = 0.0;
                }

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                break;
            }
            case AAC4: {
                mc.timer.timerSpeed = 0.99F;
                mc.thePlayer.jumpMovementFactor = 0.02958f;
                mc.thePlayer.motionY -= 0.00775;
                if (mc.thePlayer.onGround) {
                    // mc.thePlayer.jump()
                    mc.thePlayer.motionY = 0.4050;
                    mc.timer.timerSpeed = 1.35F;
                }
                break;
            }
            case Horizon: {
                if (mc.thePlayer.onGround) {
                    MovementUtils.strafe(horizonSpeed.get().floatValue());
                }
                break;
            }
            case Spartan: {
                MovementUtils.strafe(0.27F);
                mc.timer.timerSpeed = 3.7F;
                if (!mc.gameSettings.keyBindSneak.isKeyDown()) {
                    mc.thePlayer.motionY = 0.0;
                }
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    mc.timer.timerSpeed = 1.7F;
                }
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3F;
                }
                usedTimer = true;
                break;
            }
            case Matrix: {
                mc.thePlayer.jumpMovementFactor = 0.12425f;
                mc.thePlayer.motionY = -0.0125;
                if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.thePlayer.motionY = -0.1625;
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                    mc.thePlayer.motionY = 0.2425;
                }
                if (mc.thePlayer.ticksExisted % 40 == 0) {
                    mc.timer.timerSpeed = 3.0F;
                    usedTimer = true;
                }
                break;
            }
            case AAC5: {
                mc.thePlayer.jumpMovementFactor = 0.42f;

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                break;
            }
            case Test: {
                if (mc.thePlayer.ticksExisted % 6 == 0) {
                    mc.thePlayer.jumpMovementFactor = 0.42f;
                }
                if (mc.thePlayer.ticksExisted % 6 == 1) {
                    mc.thePlayer.jumpMovementFactor = 0.40f;
                }
                if (mc.thePlayer.ticksExisted % 6 == 2) {
                    mc.thePlayer.jumpMovementFactor = 0.25f;
                }
                break;
            }
            case Rewinside:
                mc.thePlayer.jumpMovementFactor = 0.42f;

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump();
                }
                break;
        }
    }

    public enum Modes {
        None, OldAAC, LAAC, Rewinside, Horizon, Spartan, AAC4, AAC5, Matrix, Test
    }
}
