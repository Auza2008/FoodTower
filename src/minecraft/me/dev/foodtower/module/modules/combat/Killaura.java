/*
Author:SuMuGod
Date:2022/7/10 4:12
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.api.events.EventPreUpdate;
import me.dev.foodtower.api.events.EventRender2D;
import me.dev.foodtower.api.events.EventRender3D;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.movement.Scaffold;
import me.dev.foodtower.module.modules.world.Teams;
import me.dev.foodtower.module.modules.world.BedNuker;
import me.dev.foodtower.other.FriendManager;
import me.dev.foodtower.utils.math.MathUtils;
import me.dev.foodtower.utils.math.RotationUtil;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Mode;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Killaura extends Module {
    public Mode<Enum<AuraMode>> mode = new Mode<>("Mode", "Mode", AuraMode.values(), AuraMode.Switch);
    public static Mode<Enum<RotMode>> rotation = new Mode<>("Rotation", "Rotation", RotMode.values(), RotMode.Basic);
    public static float[] rotations;
    public static EntityLivingBase target;
    public static boolean blocking;
    public static boolean attacking;
    double yPos;
    boolean direction = true;
    TimerUtil espTimer = new TimerUtil();
    private final Numbers<Double> crack = new Numbers<>("CrackSize", "CrackSize", 1.0, 0.0, 5.0, 0.1);
    private final Numbers<Double> switchDelay = new Numbers<>("Switchdelay", "switchdelay", 11.0, 0.0, 50.0, 1.0);
    private final Numbers<Double> aps = new Numbers<>("CPS", "CPS", 10.0, 1.0, 20.0, 0.5);
    private final Numbers<Double> reach = new Numbers<>("Reach", "Reach", 4.5, 1.0, 6.0, 0.1);
    private static final Option<Boolean> autoBlock = new Option<>("Autoblock", "Autoblock", true);
    private final Option<Boolean> esp = new Option<>("DrawESP", "DrawESP", true);
    private final Option<Boolean> players = new Option<>("Players", "Players", true);
    private final Option<Boolean> animals = new Option<>("Animals", "Animals", true);
    private final Option<Boolean> mobs = new Option<>("Mobs", "Mobs", true);
    private final Option<Boolean> invis = new Option<>("Invisibles", "Invisibles", false);
    public Option<Boolean> matrix = new Option<>("Matrix", "Matrix", false);
    public List<EntityLivingBase> targets = new ArrayList<>();
    public TimerUtil timer = new TimerUtil(), swtichTimer = new TimerUtil();

    public Killaura() {
        super("KillAura", "戮死光环", new String[]{"ka"}, ModuleType.Combat);
        setColor(new Color(255, 255, 255).getRGB());
        setKey(Keyboard.KEY_R);
    }

    @NMSL
    private void onPre(EventPreUpdate e) {
        if (Client.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled() || Client.instance.getModuleManager().getModuleByClass(BedNuker.class).isEnabled() || mc.thePlayer.isDead || mc.thePlayer.isSpectator())
            return;
        if (targets.isEmpty())
            return;
        int crackSize = this.crack.getValue().intValue();
        target = targets.get(0);
        rotations = getRotationsToEnt(target);
        if (rotation.getValue() == RotMode.Basic) {
            rotations[0] += Math.abs(target.posX - target.lastTickPosX) - Math.abs(target.posZ - target.lastTickPosZ );
            rotations[1] += Math.abs(target.posY - target.lastTickPosY);
        }
        if (rotation.getValue() == RotMode.Dynamic) {
            rotations[0] += MathUtils.getRandomInRange(1, 5);
            rotations[1] += MathUtils.getRandomInRange(1, 5);
        }
        if (rotation.getValue() == RotMode.Prediction) {
            rotations[0] = (float) (rotations[0] + ((Math.abs(target.posX - target.lastTickPosX) - Math.abs(target.posZ - target.lastTickPosZ)) * (2 / 3)) * 2);
            rotations[1] = (float) (rotations[1] + ((Math.abs(target.posY - target.lastTickPosY) - Math.abs(target.getEntityBoundingBox().minY - target.lastTickPosY)) * (2 / 3)) * 2);
        }

        if (rotation.getValue() == RotMode.Resolver) {
            if (target.posY < 0) {
                rotations[1] = 1;
            } else if (target.posY > 255) {
                rotations[1] = 90;
            }

            if (Math.abs(target.posX - target.lastTickPosX) > 0.50 || Math.abs(target.posZ - target.lastTickPosZ) > 0.50) {
                target.setEntityBoundingBox(new AxisAlignedBB(target.posX, target.posY, target.posZ, target.lastTickPosX, target.lastTickPosY, target.lastTickPosZ));
//                mc.thePlayer.addChatComponentMessage(new ChatComponentText("Tenacity: resloved target hitbox at " + target.posX + "," + target.posY + "," + target.posZ));

            }
        }

        if (rotation.getValue() == RotMode.Smooth) {
            float sens = RotationUtil.getSensitivityMultiplier();

            rotations[0] = RotationUtil.smoothRotation(mc.thePlayer.rotationYaw, rotations[0], 360);
            rotations[1] = RotationUtil.smoothRotation(mc.thePlayer.rotationPitch, rotations[1], 90);

            rotations[0] = Math.round(rotations[0] / sens) * sens;
            rotations[1] = Math.round(rotations[1] / sens) * sens;

        }

        if (matrix.getValue()) {
            rotations[0] = rotations[0] + MathUtils.getRandomFloat(1.98f, -1.98f);
        }
        e.setYaw(rotations[0]);
        e.setPitch(rotations[1]);
        mc.thePlayer.rotationYawHead = mc.thePlayer.renderYawOffset = rotations[0];

        attacking = true;
        if (timer.hasReached(1000 / (aps.getValue()))) {
            if (target != null && target.getHealth() > 0) {
                mc.thePlayer.swingItem();
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
                int i2 = 0;
                while (i2 < crackSize && target != null) {
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT);
                    mc.effectRenderer.emitParticleAtEntity(target, EnumParticleTypes.CRIT_MAGIC);
                    i2++;
                }
                if (target != null && target.getHealth() <= 0) {
                    sortTargets();
                    target = targets.get(0);
                }
            }
            timer.reset();
        }
    }

    @NMSL
    public void onRenderWorld(EventRender3D e) {
        if (target != null && esp.getValue()) {
            RenderUtil.drawShadow(target, e.getPartialTicks(), (float) yPos, direction);
            RenderUtil.drawCircle(target, e.getPartialTicks(), (float) yPos);
        }
    }

    @NMSL
    private void onRender2D(EventRender2D e) {
        if (espTimer.delay(10)) {
            if (direction) {
                yPos += 0.03;
                if (2 - yPos < 0.02) {
                    direction = false;
                }
            } else {
                yPos -= 0.03;
                if (yPos < 0.02) {
                    direction = true;
                }
            }
            espTimer.reset();
        }
    }


    @NMSL
    private void onPost(EventPostUpdate e) {
        sortTargets();
        this.setSuffix(mode.getValue());
        if (Client.instance.getModuleManager().getModuleByClass(Scaffold.class).isEnabled() || Client.instance.getModuleManager().getModuleByClass(BedNuker.class).isEnabled() || mc.thePlayer.isDead || mc.thePlayer.isSpectator())
            return;
        if (!targets.isEmpty()) {
            if (mode.getValue() == AuraMode.Switch && swtichTimer.hasReached(switchDelay.getValue().longValue())) {
                swtichTimer.reset();
            }
            if (autoBlock.getValue() && mc.thePlayer.getItemInUse() == null) {
                if (mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    if (target != null) {
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        if (Minecraft.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getCurrentItem())) {
                            mc.getItemRenderer().resetEquippedProgress2();
                        }
                        blocking = true;
                    } else {
                        blocking = false;
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                        Minecraft.playerController.onStoppedUsingItem(mc.thePlayer);
                    }
                }
            }
        }
        if (targets.isEmpty()) {
            if (blocking) {
                mc.gameSettings.keyBindUseItem.pressed = false;
            }
            attacking = false;
            blocking = false;
            target = null;
        }
    }

    @Override
    public void onDisable() {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
        Minecraft.playerController.onStoppedUsingItem(mc.thePlayer);
        targets.clear();
        blocking = false;
        attacking = false;
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    public void sortTargets() {
        targets.clear();
        for (Entity entity : mc.theWorld.getLoadedEntityList()) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entLiving = (EntityLivingBase) entity;
                if (mc.thePlayer.getDistanceToEntity(entLiving) < reach.getValue() && entLiving != mc.thePlayer && !entLiving.isDead && isValid(entLiving)) {
                    targets.add(entLiving);
                }
            }
        }
        targets.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
    }

    public boolean isValid(EntityLivingBase ent) {
        if (ent instanceof EntityPlayer && !players.getValue())
            return false;
        if (ent instanceof EntityMob && !mobs.getValue())
            return false;
        if (ent instanceof EntityAnimal && !animals.getValue())
            return false;
        if (ent.isInvisible() && !invis.getValue())
            return false;
        if (FriendManager.isFriend(ent.getName()))
            return false;
        if (AntiBot.isServerBot(ent))
            return false;
        if (Teams.isOnSameTeam(ent))
            return false;
        if (ent.getHealth() <= 0)
            return false;
        if (ent.isDead) {
            target = null;
            return false;
        }
        return true;
    }

    enum AuraMode {
        Switch
    }

    enum RotMode {
        Basic,
        Dynamic,
        Prediction,
        Resolver,
        Smooth
    }

    private float[] getRotationsToEnt(Entity ent) {
        final double differenceX = ent.posX - mc.thePlayer.posX;
        final double differenceY = (ent.posY + ent.height) - (mc.thePlayer.posY + mc.thePlayer.height) - 0.5;
        final double differenceZ = ent.posZ - mc.thePlayer.posZ;
        final float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0D / Math.PI) - 90.0f;
        final float rotationPitch = (float) (Math.atan2(differenceY, mc.thePlayer.getDistanceToEntity(ent)) * 180.0D
                / Math.PI);
        final float finishedYaw = mc.thePlayer.rotationYaw
                + MathHelper.wrapAngleTo180_float(rotationYaw - mc.thePlayer.rotationYaw);
        final float finishedPitch = mc.thePlayer.rotationPitch
                + MathHelper.wrapAngleTo180_float(rotationPitch - mc.thePlayer.rotationPitch);
        return new float[]{finishedYaw, -MathHelper.clamp_float(finishedPitch, -90, 90)};
    }
}

