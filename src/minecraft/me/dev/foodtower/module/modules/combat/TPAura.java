/*
Author:SuMuGod
Date:2022/7/10 4:18
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventMove;
import me.dev.foodtower.api.events.EventPostUpdate;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.world.Teams;
import me.dev.foodtower.other.FriendManager;
import me.dev.foodtower.utils.math.AStarCustomPathFinder;
import me.dev.foodtower.utils.math.TimerUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TPAura extends Module {
    private double dashDistance = 5.0;
    private ArrayList<Vec3> path = new ArrayList();
    private java.util.List<Vec3>[] test = new ArrayList[50];
    private java.util.List<EntityLivingBase> targets = new CopyOnWriteArrayList<EntityLivingBase>();
    public static Numbers<Double> CPS = new Numbers<Double>("CPS", "CPS", 13.0, 1.0, 20.0, 1.0);
    public static Numbers<Double> RANGE = new Numbers<Double>("Range", "Range", 30.0, 1.0, 100.0, 1.0);
    public static Numbers<Double> MAXT = new Numbers<Double>("MaxTargets", "MaxTargets", 1.0, 1.0, 25.0, 1.0);
    private Option<Boolean> PLAYERS = new Option<Boolean>("Player", "Player", true);
    private Option<Boolean> ANIMALS = new Option<Boolean>("Animals", "Animals", false);
    private Option<Boolean> MOBS = new Option<Boolean>("Mobs", "Mobs", false);
    private Option<Boolean> INVISIBLES = new Option<Boolean>("Invisible", "Invisible", false);
    private Option<Boolean> TEAMS = new Option<Boolean>("Teams", "Teams", false);
    private Timer cps = new Timer();
    public Timer timer = new Timer();
    public static boolean canReach;

    public TPAura() {
        super("TPAura", "道在我心穷山海封日矣", new String[]{"tpaura"}, ModuleType.Combat);
    }

    @Override
    public void onEnable() {
        this.targets.clear();
    }

    @NMSL
    public void onUpdate(EventMove event) {
        this.setSuffix(CPS.getValue().floatValue());
        int delayValue = 20 / ((Number)CPS.getValue()).intValue() * 50;
        int maxtTargets = ((Number)MAXT.getValue()).intValue();
        this.targets = this.getTargets();
        if (this.cps.check(delayValue) && this.targets.size() > 0) {
            this.test = new ArrayList[50];
            for (int i = 0; i < (this.targets.size() > maxtTargets ? maxtTargets : this.targets.size()); ++i) {
                EntityLivingBase T = this.targets.get(i);
                Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                this.path = this.computePath(topFrom, to);
                this.test[i] = this.path;
                for (Vec3 pathElm : this.path) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                }
                mc.thePlayer.swingItem();
                mc.playerController.attackEntity(mc.thePlayer, T);
                Collections.reverse(this.path);
                for (Vec3 pathElm : this.path) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));
                }
            }
            this.cps.reset();
        }
    }

    public void attack(EntityLivingBase entity) {
        this.attack(entity, false);
    }

    public void attack(EntityLivingBase entity, boolean crit) {
        this.mc.thePlayer.swingItem();
        float sharpLevel = EnchantmentHelper.getModifierForCreature(this.mc.thePlayer.getHeldItem(), entity.getCreatureAttribute());
        boolean vanillaCrit = this.mc.thePlayer.fallDistance > 0.0f && !this.mc.thePlayer.onGround && !this.mc.thePlayer.isOnLadder() && !this.mc.thePlayer.isInWater() && !this.mc.thePlayer.isPotionActive(Potion.blindness) && this.mc.thePlayer.ridingEntity == null;
        this.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity((Entity)entity, C02PacketUseEntity.Action.ATTACK));
        if (crit || vanillaCrit) {
            this.mc.thePlayer.onCriticalHit(entity);
        }
        if (sharpLevel > 0.0f) {
            this.mc.thePlayer.onEnchantmentCritical(entity);
        }
    }

    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!this.canPassThrow(new BlockPos(topFrom.mc()))) {
            topFrom = topFrom.addVector(0.0, 1.0, 0.0);
        }
        AStarCustomPathFinder pathfinder = new AStarCustomPathFinder(topFrom, to);
        pathfinder.compute();
        int i = 0;
        Vec3 lastLoc = null;
        Vec3 lastDashLoc = null;
        ArrayList<Vec3> path = new ArrayList<Vec3>();
        ArrayList<Vec3> pathFinderPath = pathfinder.getPath();
        for (Vec3 pathElm : pathFinderPath) {
            if (i == 0 || i == pathFinderPath.size() - 1) {
                if (lastLoc != null) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0.0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > this.dashDistance * this.dashDistance) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    int x = (int)smallX;
                    block1 : while ((double)x <= bigX) {
                        int y = (int)smallY;
                        while ((double)y <= bigY) {
                            int z = (int)smallZ;
                            while ((double)z <= bigZ) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break block1;
                                }
                                ++z;
                            }
                            ++y;
                        }
                        ++x;
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0.0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            ++i;
        }
        return path;
    }

    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    boolean validEntity(EntityLivingBase entity) {
        float range = ((Number)RANGE.getValue()).floatValue();
        boolean players = (Boolean)PLAYERS.getValue();
        boolean animals = (Boolean)ANIMALS.getValue();
        boolean mobs = (Boolean)MOBS.getValue();
        if (mc.thePlayer.isEntityAlive() && !(entity instanceof EntityPlayerSP) && mc.thePlayer.getDistanceToEntity(entity) <= range) {
            if (entity.isPlayerSleeping()) {
                return false;
            }
            if (FriendManager.isFriend(entity.getName())) {
                return false;
            }
            if((entity instanceof EntityMob || entity instanceof EntitySlime
                    || entity instanceof EntityBat) && mobs) {
                return true;
            }
            if (entity instanceof EntityPlayer) {
                if (players) {
                    EntityPlayer player = (EntityPlayer)entity;
                    if (!player.isEntityAlive() && (double)player.getHealth() == 0.0) {
                        return false;
                    }
                    if (Teams.isOnSameTeam(player) && TEAMS.getValue().booleanValue()) {
                        return false;
                    }
                    if (player.isInvisible() && !((Boolean)INVISIBLES.getValue()).booleanValue()) {
                        return false;
                    }
                    return !FriendManager.isFriend(player.getName());
                }
            } else if (!entity.isEntityAlive()) {
                return false;
            }
            if (entity instanceof EntityMob && animals) {
                return true;
            }
            if ((entity instanceof EntityAnimal || entity instanceof EntityVillager) && animals) {
                return !entity.getName().equals("Villager");
            }
        }
        return false;
    }

    private List<EntityLivingBase> getTargets() {
        ArrayList<EntityLivingBase> targets = new ArrayList<EntityLivingBase>();
        for (Entity o : mc.theWorld.getLoadedEntityList()) {
            EntityLivingBase entity;
            if (!(o instanceof EntityLivingBase) || !this.validEntity(entity = (EntityLivingBase)o)) continue;
            targets.add(entity);
        }
        targets.sort((o1, o2) -> (int)(o1.getDistanceToEntity(mc.thePlayer) * 1000.0f - o2.getDistanceToEntity(mc.thePlayer) * 1000.0f));
        return targets;
    }

    private int speed() {
        return 8;
    }

    class Timer {
        private long previousTime = -1L;

        public boolean check(float milliseconds) {
            return (float)this.getTime() >= milliseconds;
        }

        public long getTime() {
            return this.getCurrentTime() - this.previousTime;
        }

        public void reset() {
            this.previousTime = this.getCurrentTime();
        }

        public long getCurrentTime() {
            return System.currentTimeMillis();
        }
    }
}
