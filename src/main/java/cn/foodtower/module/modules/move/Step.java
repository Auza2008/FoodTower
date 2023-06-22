package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Misc.EventStepConfirm;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.events.World.EventStep;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;

public class Step extends Module {

    private final Mode mode = new Mode("Mode", ModeE.values(), ModeE.Vanilla);
    private final Numbers<Double> heightValue = new Numbers<>("Height", 1d, 0.6, 10d, 0.1);
    private final Numbers<Double> timerValue = new Numbers<>("Timer", 1d, 0.3, 10d, 0.1);
    private final Numbers<Double> delay = new Numbers<>("Delay", 0d, 0d, 500d, 1d);
    private final TimerUtil timer = new TimerUtil();
    private boolean isStep = false;
    private boolean usedTimer = false;
    private double stepX = 0.0;
    private double stepY = 0.0;
    private double stepZ = 0.0;

    public Step() {
        super("Step", new String[]{""}, ModuleType.Movement);
        addValues(mode, heightValue, timerValue, delay);
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) return;
        mc.thePlayer.stepHeight = 0.5F;
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.speedInAir = 0.02F;
    }

    @EventHandler
    private void onUpdate(EventPreUpdate e) {
        if (usedTimer) {
            mc.timer.timerSpeed = 1F;
            usedTimer = false;
        }
        isStep = false;
    }

    @EventHandler
    private void onStep(EventStep e) {
        if (mc.thePlayer == null) return;
        if (!mc.thePlayer.onGround || !timer.hasReached(delay.get().longValue())) {
            mc.thePlayer.stepHeight = 0.5F;
            e.setStepHeight(0.5F);
            return;
        }
        double height = heightValue.get();
        mc.thePlayer.stepHeight = (float) height;
        mc.timer.timerSpeed = timerValue.get().floatValue();
        usedTimer = true;
        e.setStepHeight(height);
        if (e.getStepHeight() > 0.5F) {
            isStep = true;
            stepX = mc.thePlayer.posX;
            stepY = mc.thePlayer.posY;
            stepZ = mc.thePlayer.posZ;
        }
    }

    @EventHandler
    private void onStepC(EventStepConfirm e) {
        if (mc.thePlayer == null || !isStep) return;

        if (mc.thePlayer.getEntityBoundingBox().minY - stepY > 0.5) {
            switch ((ModeE) mode.get()) {
                case NCP:
                    fakeJump();
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 0.41999998688698, stepZ, false));
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 0.7531999805212, stepZ, false));
                    break;
                case NewNCP:
                    fakeJump();
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 0.41999998688698, stepZ, false));
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 0.7531999805212, stepZ, false));
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 1, stepZ, true));
                    break;
                case Vulcan:
                    double rstepHeight = mc.thePlayer.getEntityBoundingBox().minY - stepY;
                    fakeJump();
                    if (rstepHeight > 2.0) {
                        double[] stpPacket = new double[]{0.5, 1.0, 1.5, 2.0};
                        for (double i : stpPacket) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + i, stepZ, true));
                        }
                    } else if (rstepHeight <= 2.0 && rstepHeight > 1.5) {
                        double[] stpPacket = new double[]{0.5, 1.0, 1.5};
                        for (double i : stpPacket) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + i, stepZ, true));
                        }
                    } else if (rstepHeight <= 1.5 && rstepHeight > 1.0) {
                        double[] stpPacket = new double[]{0.5, 1.0};
                        for (double i : stpPacket) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + i, stepZ, true));
                        }
                    } else if (rstepHeight <= 1.0 && rstepHeight > 0.6) {
                        double[] stpPacket = new double[]{0.5};
                        for (double i : stpPacket) {
                            sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + i, stepZ, true));
                        }
                    }
                    break;
                case BlocksMC:
                    fakeJump();
                    BlockPos pos = mc.thePlayer.getPosition().add(0.0, -1.5, 0.0);
                    sendPacket(new C08PacketPlayerBlockPlacement(pos, 1, new ItemStack(Blocks.stone.getItem(mc.theWorld, pos)), 0.0F, (float) (0.5F + Math.random() * 0.44), 0.0F));
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 0.41999998688698, stepZ, false));
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 0.7531999805212, stepZ, false));
                    sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(stepX, stepY + 1, stepZ, true));
                    break;
            }
            timer.reset();
        }
        isStep = false;
        stepX = 0.0;
        stepY = 0.0;
        stepZ = 0.0;
    }

    private void fakeJump() {
        mc.thePlayer.isAirBorne = true;
        mc.thePlayer.triggerAchievement(StatList.jumpStat);
    }

    private enum ModeE {
        Vanilla, NCP, NewNCP, Vulcan, BlocksMC
    }
}
