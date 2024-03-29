package cn.foodtower.module.modules.combat;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventPacketReceive;
import cn.foodtower.api.events.World.EventWorldChanged;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.EntityUtils;
import cn.foodtower.util.render.ColorUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S14PacketEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AntiBot extends Module {

    private final Option tabValue = new Option("Tab", true);
    private final Mode tabModeValue = new Mode("TabMode", AABmode.values(), AABmode.Contains);
    private final Option entityIDValue = new Option("EntityID", true);
    private final Option colorValue = new Option("Color", false);
    private final Option livingTimeValue = new Option("LivingTime", false);
    private final Numbers<Double> livingTimeTicksValue = new Numbers<>("LivingTimeTicks", "LivingTimeTicks", 40D, 1D, 200D, 0.1D);
    private final Option groundValue = new Option("Ground", true);
    private final Option airValue = new Option("Air", false);
    private final Option invalidGroundValue = new Option("InvalidGround", true);
    private final Option swingValue = new Option("Swing", false);
    private final Option healthValue = new Option("Health", false);
    private final Option derpValue = new Option("Derp", true);
    private final Option wasInvisibleValue = new Option("WasInvisible", false);
    private final Option armorValue = new Option("Armor", false);
    private final Option pingValue = new Option("Ping", false);
    private final Option needHitValue = new Option("NeedHit", false);
    private final Option duplicateInWorldValue = new Option("DuplicateInWorld", false);
    private final Option duplicateInTabValue = new Option("DuplicateInTab", false);
    private final Option unLegitSpeed = new Option("UnLegitSpeed", false);
    private final Numbers<Double> speedValue = new Numbers<>("MaxUnLegitSpeed", 0.2d, 0.2d, 10d, 0.01d);

    private final List<Integer> ground = new ArrayList<>();
    private final List<Integer> air = new ArrayList<>();
    private final Map<Integer, Integer> invalidGround = new HashMap<>();
    private final List<Integer> swing = new ArrayList<>();
    private final List<Integer> invisible = new ArrayList<>();
    private final List<Integer> hitted = new ArrayList<>();

    public AntiBot() {
        super("AdvancedAntiBot", new String[]{"AAB"}, ModuleType.Combat);
        this.addValues(tabValue, tabModeValue, entityIDValue, colorValue, livingTimeValue, livingTimeTicksValue, groundValue, airValue, invalidGroundValue, swingValue, healthValue, derpValue, wasInvisibleValue, armorValue, pingValue, needHitValue, duplicateInWorldValue, duplicateInTabValue, unLegitSpeed, speedValue);
    }

    public static boolean isServerBot(final Entity entity) {
        if (!(entity instanceof EntityPlayer)) return false;

        final AntiBot antiBot = (AntiBot) ModuleManager.getModuleByClass(AntiBot.class);

        if (antiBot == null || !antiBot.isEnabled()) return false;

        if (antiBot.colorValue.get() && !entity.getDisplayName().getFormattedText().replace("§r", "").contains("§"))
            return true;

        if (antiBot.livingTimeValue.get() && entity.ticksExisted < antiBot.livingTimeTicksValue.get())
            return true;

        if (antiBot.groundValue.get() && !antiBot.ground.contains(entity.getEntityId())) return true;

        if (antiBot.airValue.get() && !antiBot.air.contains(entity.getEntityId())) return true;

        if (antiBot.swingValue.get() && !antiBot.swing.contains(entity.getEntityId())) return true;

        if (antiBot.healthValue.get() && ((EntityLivingBase) entity).getHealth() > 20F) return true;

        if (antiBot.entityIDValue.get() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1))
            return true;

        if (antiBot.derpValue.get() && (entity.rotationPitch > 90F || entity.rotationPitch < -90F)) return true;

        if (antiBot.wasInvisibleValue.get() && antiBot.invisible.contains(entity.getEntityId())) return true;

        if (antiBot.armorValue.get()) {
            final EntityPlayer player = (EntityPlayer) entity;

            if (player.inventory.armorInventory[0] == null && player.inventory.armorInventory[1] == null && player.inventory.armorInventory[2] == null && player.inventory.armorInventory[3] == null)
                return true;
        }

        if (antiBot.pingValue.get()) {
            EntityPlayer player = (EntityPlayer) entity;

            if (mc.getNetHandler().getPlayerInfo(player.getUniqueID()).getResponseTime() == 0) return true;
        }

        if (antiBot.needHitValue.get() && !antiBot.hitted.contains(entity.getEntityId())) return true;

        if (antiBot.invalidGroundValue.get() && antiBot.invalidGround.getOrDefault(entity.getEntityId(), 0) >= 10)
            return true;

        if (antiBot.tabValue.get()) {
            final boolean equals = antiBot.tabModeValue.get() == AABmode.Equals;
            final String targetName = ColorUtils.stripColor(entity.getDisplayName().getFormattedText());

            if (targetName != null) {
                for (final NetworkPlayerInfo networkPlayerInfo : mc.getNetHandler().getPlayerInfoMap()) {
                    final String networkName = ColorUtils.stripColor(EntityUtils.getName(networkPlayerInfo));

                    if (networkName == null) continue;

                    if (equals ? targetName.equals(networkName) : targetName.contains(networkName)) return false;
                }

                return true;
            }
        }

        if (antiBot.duplicateInWorldValue.get()) {
            if (mc.theWorld.loadedEntityList.stream().filter(currEntity -> currEntity instanceof EntityPlayer && currEntity.getDisplayName().equals(currEntity.getDisplayName())).count() > 1)
                return true;
        }

        if (antiBot.duplicateInTabValue.get()) {
            if (mc.getNetHandler().getPlayerInfoMap().stream().filter(networkPlayer -> entity.getName().equals(ColorUtils.stripColor(EntityUtils.getName(networkPlayer)))).count() > 1)
                return true;
        }

        if (antiBot.unLegitSpeed.get()) {
            if (getSpeed(entity) > antiBot.speedValue.get())
                return true;
        }

        return entity.getName().isEmpty() || entity.getName().equals(mc.thePlayer.getName());
    }

    private static double getSpeed(Entity e) {
        final double motionX = e.motionX;
        final double n = motionX * e.motionX;
        final double motionZ = e.motionZ;
        return Math.sqrt(n + motionZ * e.motionZ);
    }

    public void onDisable() {
        clearAll();
        super.onDisable();
    }

    @EventHandler
    public void onPacket(final EventPacketReceive event) {
        if (mc.thePlayer == null || mc.theWorld == null) return;

        final Packet<?> packet = event.getPacket();

        if (packet instanceof S14PacketEntity) {
            final S14PacketEntity packetEntity = (S14PacketEntity) event.getPacket();
            final Entity entity = packetEntity.getEntity(mc.theWorld);

            if (entity instanceof EntityPlayer) {
                if (packetEntity.getOnGround() && !ground.contains(entity.getEntityId()))
                    ground.add(entity.getEntityId());

                if (!packetEntity.getOnGround() && !air.contains(entity.getEntityId())) air.add(entity.getEntityId());

                if (packetEntity.getOnGround()) {
                    if (entity.prevPosY != entity.posY)
                        invalidGround.put(entity.getEntityId(), invalidGround.getOrDefault(entity.getEntityId(), 0) + 1);
                } else {
                    final int currentVL = invalidGround.getOrDefault(entity.getEntityId(), 0) / 2;

                    if (currentVL <= 0) invalidGround.remove(entity.getEntityId());
                    else invalidGround.put(entity.getEntityId(), currentVL);
                }

                if (entity.isInvisible() && !invisible.contains(entity.getEntityId()))
                    invisible.add(entity.getEntityId());
            }
        }

        if (packet instanceof S0BPacketAnimation) {
            final S0BPacketAnimation packetAnimation = (S0BPacketAnimation) event.getPacket();
            final Entity entity = mc.theWorld.getEntityByID(packetAnimation.getEntityID());

            if (entity instanceof EntityLivingBase && packetAnimation.getAnimationType() == 0 && !swing.contains(entity.getEntityId()))
                swing.add(entity.getEntityId());
        }
    }

    @EventHandler
    public void onAttack(EventAttack e) {
        final Entity entity = e.getEntity();

        if (entity instanceof EntityLivingBase && !hitted.contains(entity.getEntityId()))
            hitted.add(entity.getEntityId());
    }

    @EventHandler
    public void onWorld(EventWorldChanged event) {
        clearAll();
    }

    private void clearAll() {
        hitted.clear();
        swing.clear();
        ground.clear();
        invalidGround.clear();
        invisible.clear();
    }

    enum AABmode {
        Equals, Contains
    }
}
