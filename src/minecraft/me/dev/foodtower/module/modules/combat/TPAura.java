/*
Author:SuMuGod
Date:2022/7/10 4:18
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.combat;

import me.dev.foodtower.api.NMSL;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class TPAura extends Module {
    Numbers<Number> range = new Numbers<>("Range", "Range", 50, 1, 100, 1);
    Numbers<Number> delay = new Numbers<>("Delay", "Delay", 100, 10, 1000, 10);
    ArrayList<Vec3> vec3s = new ArrayList<>();
    TimerUtil timer = new TimerUtil();
    final Option<Boolean> Swing = new Option<Boolean>("Swing", "Swing", true);
    final Option<Boolean> players = new Option<Boolean>("Players", "players", true);
    final Option<Boolean> animals = new Option<Boolean>("Animals", "animals", true);
    private final Option<Boolean> mobs = new Option<Boolean>("Mobs", "mobs", false);
    private final Option<Boolean> invis = new Option<Boolean>("Invisibles", "invisibles", false);

    public TPAura() {
        super("TPAura", "道在我心穷山海封日矣", new String[]{"ta"}, ModuleType.Combat);
        this.setColor(new Color(255, 50, 70).getRGB());
    }

    @NMSL
    public void onUpdate(EventPostUpdate e) {
        if (timer.delay(delay.getValue().floatValue())) {
            if (mc.theWorld.loadedEntityList.size() == 0) {
                vec3s = new ArrayList<>();
            }
            for (Entity entity : mc.theWorld.loadedEntityList) {
                if (entity instanceof EntityLivingBase && !entity.isDead && (mc.thePlayer.getDistanceToEntity(entity) < range.getValue().floatValue()) && (entity != mc.thePlayer)) {
                    EntityLivingBase T = (EntityLivingBase) entity;
                    if (AntiBot.isServerBot(T))
                        return;
                    if (Teams.isOnSameTeam(T))
                        return;
                    if (FriendManager.isFriend(T.getName()))
                        return;
                    if (T instanceof EntityPlayer && !players.getValue())
                        return;
                    if (T instanceof EntityAnimal && !animals.getValue())
                        return;
                    if (T instanceof EntityMob && !mobs.getValue())
                        return;
                    if (T.isInvisible() && !invis.getValue())
                        return;
                    Vec3 topFrom = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ);
                    Vec3 to = new Vec3(T.posX, T.posY, T.posZ);
                    vec3s = computePath(topFrom, to);
                    float n = 1;
                    for (Vec3 pathElm : vec3s) {
                        mc.thePlayer.setPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ());
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ(), true));//+ 4 - ((n + 1) >= vec3s.size() ? 1 : 0)
                        n++;
                    }
                    mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(T, C02PacketUseEntity.Action.ATTACK));
                    if (Swing.getValue()) {
                        mc.thePlayer.swingItem();
                    }
                    Collections.reverse(vec3s);
                    for (Vec3 pathElm : vec3s) {
                        mc.thePlayer.setPosition(pathElm.getX(), pathElm.getY(), pathElm.getZ());

                    }
                }

            }

            timer.reset();
        }
    }


    private boolean canPassThrow(BlockPos pos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ())).getBlock();
        return block.getMaterial() == Material.air || block.getMaterial() == Material.plants || block.getMaterial() == Material.vine || block == Blocks.ladder || block == Blocks.water || block == Blocks.flowing_water || block == Blocks.wall_sign || block == Blocks.standing_sign;
    }

    private ArrayList<Vec3> computePath(Vec3 topFrom, Vec3 to) {
        if (!canPassThrow(new BlockPos(topFrom))) {
            topFrom = topFrom.addVector(0, 1, 0);
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
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                }
                path.add(pathElm.addVector(0.5, 0, 0.5));
                lastDashLoc = pathElm;
            } else {
                boolean canContinue = true;
                if (pathElm.squareDistanceTo(lastDashLoc) > 5 * 5) {
                    canContinue = false;
                } else {
                    double smallX = Math.min(lastDashLoc.getX(), pathElm.getX());
                    double smallY = Math.min(lastDashLoc.getY(), pathElm.getY());
                    double smallZ = Math.min(lastDashLoc.getZ(), pathElm.getZ());
                    double bigX = Math.max(lastDashLoc.getX(), pathElm.getX());
                    double bigY = Math.max(lastDashLoc.getY(), pathElm.getY());
                    double bigZ = Math.max(lastDashLoc.getZ(), pathElm.getZ());
                    cordsLoop:
                    for (int x = (int) smallX; x <= bigX; x++) {
                        for (int y = (int) smallY; y <= bigY; y++) {
                            for (int z = (int) smallZ; z <= bigZ; z++) {
                                if (!AStarCustomPathFinder.checkPositionValidity(x, y, z, false)) {
                                    canContinue = false;
                                    break cordsLoop;
                                }
                            }
                        }
                    }
                }
                if (!canContinue) {
                    path.add(lastLoc.addVector(0.5, 0, 0.5));
                    lastDashLoc = lastLoc;
                }
            }
            lastLoc = pathElm;
            i++;
        }
        return path;
    }
}


