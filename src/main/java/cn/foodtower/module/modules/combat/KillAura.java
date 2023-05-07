package cn.foodtower.module.modules.combat;


import cn.foodtower.Client;
import cn.foodtower.api.EventBus;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.util.entity.entitycheck.EntityValidator;
import cn.foodtower.util.entity.entitycheck.checks.*;
import cn.foodtower.util.math.MathUtil;
import cn.foodtower.util.rotations.myAngle;
import cn.foodtower.util.rotations.myAngleUtility;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

import javax.vecmath.Vector3d;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    public static boolean isBlocking;
    public static EntityLivingBase curTarget;
    public final Option autoBlock = new Option("Auto Block", true);
    public final Numbers<Double> aps = new Numbers("APS", 13.0, 1.0, 20.0, 1.0);
    public final Numbers<Double> randomization = new Numbers<>("Randomization", 0.0, 0.0, 5.0, 1.0);
    public final cn.foodtower.api.value.Mode mode = new cn.foodtower.api.value.Mode("Mode", Mode.values(), Mode.SWITCH);
    public final cn.foodtower.api.value.Mode autoBlockMode = new cn.foodtower.api.value.Mode("Auto Block Mode", AutoBlockMode.values(), AutoBlockMode.OFFSET);
    private final cn.foodtower.api.value.Mode attackTiming = new cn.foodtower.api.value.Mode("AttackTiming", AttackTiming.values(), AttackTiming.Update);
    public final Numbers<Double> switchDelay = new Numbers("Switch Delay", 3.0, 1.0, 10.0, 1.0);
    public final cn.foodtower.api.value.Mode sortingMode = new cn.foodtower.api.value.Mode("Sorting Mode", SortingMode.values(), SortingMode.DISTANCE);
    public final Numbers<Double> range = new Numbers("Range", 4.2, 3.0, 7.0, 0.1);
    public final Option teams = new Option("Teams", true);
    public final Option players = new Option("Players", true);
    public final Option animals = new Option("Animals", false);
    public final Option monsters = new Option("Monsters", false);
    public final Option prioritizePlayers = new Option("Prioritize Players", true);
    public final Option invisibles = new Option("Invisibles", false);
    public final Option forceUpdate = new Option("Force Update", true);
    public final Option disableOnDeath = new Option("Disable on death", true);
    public final java.util.List<EntityLivingBase> targets = new ArrayList<>();
    private final cn.foodtower.api.value.Mode rotMode = new cn.foodtower.api.value.Mode("RotationMode", Rotations.values(), Rotations.Basic);
    private final TimerUtil attackStopwatch = new TimerUtil();
    private final TimerUtil updateStopwatch = new TimerUtil();
    private final TimerUtil critStopwatch = new TimerUtil();
    private final EntityValidator entityValidator = new EntityValidator();
    private final EntityValidator blockValidator = new EntityValidator();
    private int targetIndex;
    private boolean changeTarget;

    public KillAura() {
        super("KillAura", new String[]{"ka"}, ModuleType.Combat);
        AliveCheck aliveCheck = new AliveCheck();
        EntityCheck entityCheck = new EntityCheck(this.players, this.animals, this.monsters, this.invisibles);
        TeamsCheck teamsCheck = new TeamsCheck(this.teams);
        this.entityValidator.add(aliveCheck);
        this.entityValidator.add(new DistanceCheck(this.range));
        this.entityValidator.add(entityCheck);
        this.entityValidator.add(teamsCheck);
        this.blockValidator.add(aliveCheck);
        this.blockValidator.add(new ConstantDistanceCheck(8.0f));
        this.blockValidator.add(entityCheck);
        this.blockValidator.add(teamsCheck);
        addValues(this.mode, this.sortingMode, this.autoBlockMode, this.aps, randomization, this.range, attackTiming, rotMode, this.switchDelay, this.teams, this.players, this.prioritizePlayers, this.animals, this.monsters, this.invisibles, this.autoBlock, this.forceUpdate, this.disableOnDeath);
        setValueDisplayable(sortingMode, mode, Mode.SINGLE);
        setValueDisplayable(switchDelay, mode, Mode.SWITCH);
    }

    private static float[] getRotationsEntity(EntityLivingBase entity) {
        return getRotations(entity.posX, entity.posY + (double) entity.getEyeHeight() - 0.4, entity.posZ);
    }

    private static float[] getNeededRotations(EntityLivingBase entityIn) {
        double d0 = entityIn.posX - mc.thePlayer.posX;
        double d1 = entityIn.posZ - mc.thePlayer.posZ;
        double d2 = entityIn.posY + entityIn.getEyeHeight() - (mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight());
        float d3 = MathHelper.sqrt_double(d0 * d0 + d1 * d1);
        double f = (MathHelper.atan2(d1, d0) * 180.0 / Math.PI) - 90.0f;
        double f1 = (-(MathHelper.atan2(d2, d3) * 180.0 / Math.PI));
        return new float[]{(float) f, (float) f1};
    }


    public static float[] getRotations(double posX, double posY, double posZ) {
        EntityPlayerSP player = mc.thePlayer;
        double x = posX - player.posX;
        double y = posY - (player.posY + (double) player.getEyeHeight());
        double z = posZ - player.posZ;
        double dist = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (-(Math.atan2(y, dist) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }

    public static float[] getEntityRotations(EntityLivingBase target, float[] lastrotation, boolean aac, int smooth) {
        myAngleUtility angleUtility = new myAngleUtility(aac, smooth);
        Vector3d enemyCoords = new Vector3d(target.posX, target.posY + target.getEyeHeight(), target.posZ);
        Vector3d myCoords = new Vector3d(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
        myAngle dstAngle = angleUtility.calculateAngle(enemyCoords, myCoords);
        myAngle srcAngle = new myAngle(lastrotation[0], lastrotation[1]);
        myAngle smoothedAngle = angleUtility.smoothAngle(dstAngle, srcAngle);
        float yaw = smoothedAngle.getYaw();
        float pitch = smoothedAngle.getPitch();
        float yaw2 = MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw);
        yaw = mc.thePlayer.rotationYaw + yaw2;
        return new float[]{yaw, pitch};
    }

    @Override
    public void onDisable() {
        this.unblock();
    }

    @Override
    public void onEnable() {
        this.updateStopwatch.reset();
        this.critStopwatch.reset();
        this.targetIndex = 0;
        this.targets.clear();
        this.changeTarget = false;
    }

    @EventHandler
    public final void onMotionUpdate(EventMotionUpdate event) {
        this.updateTargets();
        this.sortTargets();
        if (!isHoldingSword()) {
            isBlocking = false;
        }
        if ((curTarget = this.getTarget()) == null) {
            this.unblock();
        }
        if (event.isPre() && this.canAttack() && curTarget != null) {
            if (this.updateStopwatch.hasReached(56L) && this.forceUpdate.getValue().booleanValue() && !mc.thePlayer.isMoving()) {
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                this.updateStopwatch.reset();
            }
            float[] angles;
            switch ((Rotations) (rotMode.getValue())) {
                case Basic:
                    angles = getRotationsEntity(curTarget);
                    event.setYaw(angles[0]);
                    event.setPitch(angles[1]);
                    Client.RenderRotate(angles[0], angles[1]);
                    break;
                case Hypixel:
                    angles = getNeededRotations(curTarget);
                    event.setYaw(angles[0]);
                    event.setPitch(angles[1]);
                    Client.RenderRotate(angles[0], angles[1]);
                    break;
                case None:
                    break;
            }
        }
        if (attackTiming.getValue().equals(AttackTiming.Pre) || attackTiming.getValue().equals(AttackTiming.All)) {
            if (event.isPre()) {
                attack();
            }
        }
        if (attackTiming.getValue().equals(AttackTiming.Post) || attackTiming.getValue().equals(AttackTiming.All)) {
            if (!event.isPre()) {
                attack();
            }
        }
    }

    @EventHandler
    public final void onUpdate(EventUpdate eventUpdate) {
        if (attackTiming.getValue().equals(AttackTiming.Update) || attackTiming.getValue().equals(AttackTiming.All)) {
            attack();
        }
    }

    @EventHandler
    private void onRender3D(EventRender3D e) {
        if (attackTiming.getValue().equals(AttackTiming.All)) {
            attack();
        }
    }

    private void attack() {
        curTarget = this.getTarget();
        if (curTarget != null && this.canAttack()) {
            if (this.attackStopwatch.hasReached(getAps()) && this.canAttack()) {
                this.attack(curTarget);
                this.attackStopwatch.reset();
            }
            if ((double) curTarget.hurtTime >= this.switchDelay.getValue()) {
                this.changeTarget = true;
            }
        }
        this.block();
    }

    private Integer getAps() {
        if (randomization.getValue().intValue() == 0) {
            return 1000 / aps.getValue().intValue();
        }
        return 1000 / MathUtil.randomNumber(MathHelper.abs_int(aps.getValue().intValue() + randomization.getValue().intValue()), MathHelper.abs_int(aps.getValue().intValue() - randomization.getValue().intValue()));
    }

    public final EntityLivingBase getTarget() {
        if (this.targets.isEmpty()) {
            return null;
        }
        if (this.mode.getValue() == Mode.SINGLE) {
            return this.targets.get(0);
        }
        int size = this.targets.size();
        if (size >= this.targetIndex && this.changeTarget) {
            ++this.targetIndex;
            this.changeTarget = false;
        }
        if (size <= this.targetIndex) {
            this.targetIndex = 0;
        }
        return this.targets.get(this.targetIndex);
    }

    private boolean isEntityNearby() {
        java.util.List loadedEntityList = mc.theWorld.loadedEntityList;
        int loadedEntityListSize = loadedEntityList.size();
        for (int i = 0; i < loadedEntityListSize; ++i) {
            Entity entity = (Entity) loadedEntityList.get(i);
            if (!this.blockValidator.validate(entity)) continue;
            return true;
        }
        return false;
    }

    private void attack(EntityLivingBase entity) {
        EntityPlayerSP player = mc.thePlayer;
        NetHandlerPlayClient netHandler = mc.getNetHandler();
        this.unblock();
        player.swingItem();
        EventBus.getInstance().register(new EventAttack(entity, true));
        netHandler.addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
    }

    private void unblock() {
        if ((this.autoBlock.getValue().booleanValue() || mc.thePlayer.isBlocking()) && isHoldingSword() && isBlocking) {
            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 0);
            switch ((AutoBlockMode) this.autoBlockMode.getValue()) {
                case OFFSET:
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    isBlocking = false;
                    break;
                case HVH:
                    mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.thePlayer.stopUsingItem();
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    isBlocking = false;
                    break;
                case SMART:
                    mc.playerController.syncCurrentPlayItem();
                    mc.getNetHandler().addToSendQueueSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlocking = false;
                    break;
                case FAKE:
                    isBlocking = false;
                    break;
            }
        }
    }

    private void block() {
        if (this.autoBlock.getValue().booleanValue() && isHoldingSword() && this.isEntityNearby() && !isBlocking) {
            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getMaxItemUseDuration());
            switch ((AutoBlockMode) this.autoBlockMode.getValue()) {
                case SMART:
                    mc.playerController.syncCurrentPlayItem();
                    mc.getNetHandler().addToSendQueueSilent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
                    isBlocking = true;
                    break;
                case OFFSET:
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    isBlocking = true;
                    break;
                case HVH:
                    mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    isBlocking = true;
                    break;
                case FAKE:
                    isBlocking = true;
                    break;
            }
        }
    }

    private boolean canAttack() {
        return !ModuleManager.getModuleByClass(Scaffold.class).isEnabled();
    }

    private void updateTargets() {
        this.targets.clear();
        List entities = mc.theWorld.loadedEntityList;
        int entitiesSize = entities.size();
        for (int i = 0; i < entitiesSize; ++i) {
            EntityLivingBase entityLivingBase;
            Entity entity = (Entity) entities.get(i);
            if (!(entity instanceof EntityLivingBase) || !this.entityValidator.validate(entityLivingBase = (EntityLivingBase) entity))
                continue;
            this.targets.add(entityLivingBase);
        }
    }

    private void sortTargets() {
        switch ((SortingMode) this.sortingMode.getValue()) {
            case HEALTH: {
                this.targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            }
            case DISTANCE: {
                this.targets.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
            }
        }
        if (this.prioritizePlayers.getValue().booleanValue()) {
            this.targets.sort(Comparator.comparing(entity -> entity instanceof EntityPlayer));
        }
    }

    private boolean isHoldingSword() {
        return mc.thePlayer.getCurrentEquippedItem() != null && mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemSword;
    }

    private enum SortingMode {
        DISTANCE, HEALTH
    }

    private enum AutoBlockMode {
        SMART, OFFSET, HVH, FAKE
    }

    private enum Mode {
        SINGLE, SWITCH
    }

    private enum AttackTiming {
        Pre, Post, Update, All
    }

    private enum Rotations {
        Basic, Hypixel, None
    }
}
