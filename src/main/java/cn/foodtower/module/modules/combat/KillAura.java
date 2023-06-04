package cn.foodtower.module.modules.combat;


import cn.foodtower.Client;
import cn.foodtower.api.EventBus;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Misc.EventLivingUpdate;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.move.Sprint;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.util.entity.entitycheck.EntityValidator;
import cn.foodtower.util.entity.entitycheck.checks.ConstantDistanceCheck;
import cn.foodtower.util.entity.entitycheck.checks.DistanceCheck;
import cn.foodtower.util.entity.entitycheck.checks.EntityCheck;
import cn.foodtower.util.entity.entitycheck.checks.TeamsCheck;
import cn.foodtower.util.math.MathUtil;
import cn.foodtower.util.math.RotationUtils;
import cn.foodtower.util.math.SmoothRotationObject;
import cn.foodtower.util.render.RenderUtil;
import cn.foodtower.util.time.MSTimer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class KillAura extends Module {
    public static boolean isBlocking;
    public static EntityLivingBase curTarget;
    public final Option autoBlock = new Option("Auto Block", true);
    public final Option coolDown = new Option("Auto CoolDown", false);
    public final Numbers<Double> aps = new Numbers("MaxCps", 13.0, 1.0, 20.0, 1.0);
    public final Numbers<Double> minAps = new Numbers<>("MinCps", 10.0, 1.0, 20.0, 1.0);
    public final cn.foodtower.api.value.Mode mode = new cn.foodtower.api.value.Mode("Mode", Mode.values(), Mode.SWITCH);
    public final cn.foodtower.api.value.Mode autoBlockMode = new cn.foodtower.api.value.Mode("Auto Block Mode", AutoBlockMode.values(), AutoBlockMode.KeyBind);
    public final Numbers<Double> switchDelay = new Numbers("Switch Delay", 3.0, 1.0, 10.0, 1.0);
    public final cn.foodtower.api.value.Mode sortingMode = new cn.foodtower.api.value.Mode("Sorting Mode", SortingMode.values(), SortingMode.DISTANCE);
    public final Numbers<Double> range = new Numbers("Range", 4.2, 3.0, 7.0, 0.1);
    public final Option teams = new Option("Teams", true);
    public final Option players = new Option("Players", true);
    public final Option animals = new Option("Animals", false);
    public final Option monsters = new Option("Monsters", false);
    public final Option prioritizePlayers = new Option("Prioritize Players", true);
    public final Option invisibles = new Option("Invisibles", false);
    public final Option forceUpdate = new Option("Force Update", false);
    public final Option disableOnDeath = new Option("Disable on death", true);
    public final java.util.List<EntityLivingBase> targets = new ArrayList<>();
    private final Numbers<Double> fov = new Numbers<>("Fov", 180d, 10d, 180d, 10d);
    private final Numbers<Double> rotSpeed = new Numbers<>("RotationSpeed", 180d, 1d, 180d, 1d);
    private final Option wall = new Option("Through Wall", true);
    private final Option swing = new Option("Swing", true);
    private final Option dbtap = new Option("Double Tap", false);
    private final cn.foodtower.api.value.Mode attackTiming = new cn.foodtower.api.value.Mode("AttackTiming", AttackTiming.values(), AttackTiming.Update);
    private final cn.foodtower.api.value.Mode abTiming = new cn.foodtower.api.value.Mode("BlockTiming", ABTiming.values(), ABTiming.Update);
    private final cn.foodtower.api.value.Mode rotMode = new cn.foodtower.api.value.Mode("RotationMode", Rotations.values(), Rotations.Basic);
    private final MSTimer attackStopwatch = new MSTimer();
    private final MSTimer updateStopwatch = new MSTimer();
    private final MSTimer critStopwatch = new MSTimer();
    private final EntityValidator entityValidator = new EntityValidator();
    private final EntityValidator blockValidator = new EntityValidator();
    private final SmoothRotationObject smoothRotationObject = new SmoothRotationObject();
    private float[] angles;
    private int hitTicks;
    private int targetIndex;
    private boolean changeTarget;

    public KillAura() {
        super("KillAura", new String[]{"ka"}, ModuleType.Combat);
        EntityCheck entityCheck = new EntityCheck(this.players, this.animals, this.monsters, this.invisibles, this.wall);
        TeamsCheck teamsCheck = new TeamsCheck(this.teams);
        this.entityValidator.add(new DistanceCheck(this.range));
        this.entityValidator.add(entityCheck);
        this.entityValidator.add(teamsCheck);
        this.blockValidator.add(new ConstantDistanceCheck(8.0f));
        this.blockValidator.add(entityCheck);
        this.blockValidator.add(teamsCheck);
        addValues(this.mode, this.sortingMode, this.autoBlockMode, this.aps, minAps, this.range, attackTiming, abTiming, rotMode, rotSpeed, this.switchDelay, wall, this.teams, this.players, this.prioritizePlayers, this.animals, this.monsters, this.invisibles, this.autoBlock, swing, fov, coolDown, dbtap, this.forceUpdate, this.disableOnDeath);
        setValueDisplayable(sortingMode, mode, Mode.SINGLE);
        setValueDisplayable(switchDelay, mode, Mode.SWITCH);
        setValueDisplayable(new Value[]{autoBlockMode, abTiming}, autoBlock, autoBlock.getValue());
        setValueDisplayable(prioritizePlayers, players, players.getValue());
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

    @Override
    public void onDisable() {
        if (autoBlock.getValue()) {
            if (autoBlockMode.getValue().equals(AutoBlockMode.Always)) {
                mc.gameSettings.keyBindUseItem.Doing = false;
                mc.playerController.onStoppedUsingItem(mc.thePlayer);
                isBlocking = false;
            } else {
                this.unblock();
            }
        }
        curTarget = null;
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
    private void onPre(EventPreUpdate e) {
        this.updateTargets();
        this.sortTargets();
        if (curTarget != null) {
            mc.thePlayer.setSprinting(ModuleManager.getModuleByClass(KeepSprint.class).isEnabled() && ModuleManager.getModuleByClass(Sprint.class).isEnabled());
        }
        ++hitTicks;
        if (!isHoldingSword()) {
            isBlocking = false;
        }
        if (curTarget == null && autoBlock.getValue() && isBlocking) {
            unblock();
        }
        if (this.canAttack() && curTarget != null) {
            if (this.updateStopwatch.hasTimePassed(56L) && this.forceUpdate.getValue().booleanValue() && !mc.thePlayer.isMoving()) {
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                this.updateStopwatch.reset();
            }
            if (!rotMode.getValue().equals(Rotations.None)) {
                switch ((Rotations) (rotMode.getValue())) {
                    case Basic:
                        angles = RotationUtils.getRotationsEntity(curTarget);
                        break;
                    case Hypixel:
                        angles = getNeededRotations(curTarget);
                        break;
                    case HVH:
                        angles = RotationUtils.getCustomRotation(RotationUtils.getLocation(curTarget.getEntityBoundingBox()));
                        break;
                }
                if (rotSpeed.getValue() == 180d) {
                    e.setYaw(angles[0]);
                    e.setPitch(angles[1]);
                    Client.RenderRotate(angles[0], angles[1]);
                } else {
                    smoothRotationObject.setWillYawPitch(angles[0], angles[1]);
                    smoothRotationObject.handleRotation(rotSpeed.getValue());
                    smoothRotationObject.setPlayerRotation(e);
                }
            }
        }
    }

    @EventHandler
    private void runAttack(EventMotionUpdate e) {
        if ((attackTiming.getValue().equals(AttackTiming.Pre) && e.isPre()) || (attackTiming.getValue().equals(AttackTiming.Post) && !e.isPre()) || attackTiming.getValue().equals(AttackTiming.Motion) || attackTiming.getValue().equals(AttackTiming.All)) {
            attack();
        }
        if ((abTiming.getValue().equals(ABTiming.Pre) && e.isPre()) || (abTiming.getValue().equals(ABTiming.Post) && !e.isPre()) || abTiming.getValue().equals(ABTiming.Motion) || abTiming.getValue().equals(ABTiming.All)) {
            block();
        }
    }

    @EventHandler
    private void runLivingUpdateAttack(EventLivingUpdate e) {
        if (attackTiming.getValue().equals(AttackTiming.All) || attackTiming.getValue().equals(AttackTiming.Update)) {
            attack();
        }
        if (abTiming.getValue().equals(ABTiming.All) || abTiming.getValue().equals(ABTiming.Update)) {
            block();
        }
    }

    @EventHandler
    private void onRender(EventRender3D e) {
        if (attackTiming.getValue().equals(AttackTiming.Render3D)) {
            attack();
        }
        if (abTiming.getValue().equals(ABTiming.Render3D)) {
            block();
        }
        if (curTarget != null) {
            Color color = curTarget.hurtTime > 0 ? new Color(-1618884) : new Color(-13330213);
            double x;
            double y;
            double z;
            x = curTarget.lastTickPosX + (curTarget.posX - curTarget.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
            mc.getRenderManager();
            y = curTarget.lastTickPosY + (curTarget.posY - curTarget.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
            mc.getRenderManager();
            z = curTarget.lastTickPosZ + (curTarget.posZ - curTarget.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
            x -= 0.5;
            z -= 0.5;
            y += curTarget.getEyeHeight() + 0.35 - (curTarget.isSneaking() ? 0.25 : 0.0);
            final double mid = 0.5;
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glTranslated(x + mid, y + mid, z + mid);
            GL11.glRotated(-curTarget.rotationYaw % 360.0f, 0.0, 1.0, 0.0);
            GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
            GL11.glDisable(3553);
            GL11.glEnable(2848);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
            GL11.glLineWidth(2.0f);
            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
            RenderUtil.drawBoundingBox(new AxisAlignedBB(x + 0.2, y - 0.04, z + 0.2, x + 0.8, y + 0.01, z + 0.8));
            GL11.glDisable(2848);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
            GL11.glPopMatrix();
        }
    }

//    @EventHandler
//    public final void onUpdate(EventUpdate eventUpdate) {
//        if (attackTiming.getValue().equals(AttackTiming.Update) || attackTiming.getValue().equals(AttackTiming.All)) {
//            attack();
//        }
//    }

//    @EventHandler
//    private void onRender3D(EventRender3D e) {
//        if (attackTiming.getValue().equals(AttackTiming.All)) {
//            attack();
//        }
//    }

    private void attack() {
        double delayValue = -1;
        if (this.coolDown.getValue()) {
            delayValue = 4;
            if (mc.thePlayer.getHeldItem() != null) {
                final Item item = mc.thePlayer.getHeldItem().getItem();
                if (item instanceof ItemSpade || item == Items.golden_axe || item == Items.diamond_axe || item == Items.wooden_hoe || item == Items.golden_hoe)
                    delayValue = 20;
                if (item == Items.wooden_axe || item == Items.stone_axe) delayValue = 25;
                if (item instanceof ItemSword) delayValue = 12;
                if (item instanceof ItemPickaxe) delayValue = 17;
                if (item == Items.iron_axe) delayValue = 22;
                if (item == Items.stone_hoe) delayValue = 10;
                if (item == Items.iron_hoe) delayValue = 7;
            }
            delayValue *= Math.max(1, mc.timer.timerSpeed);
        }
        curTarget = this.getTarget();
        if (curTarget != null && this.canAttack()) {
            if (((!coolDown.getValue() && this.attackStopwatch.hasTimePassed(getAps())) || (coolDown.getValue() && hitTicks > delayValue))) {
                EventBus.getInstance().register(new EventAttack(curTarget));
                this.attack(curTarget);
                this.attackStopwatch.reset();
                hitTicks = 0;
            }
            if ((double) curTarget.hurtTime >= this.switchDelay.getValue()) {
                this.changeTarget = true;
            }
        }
    }

    private Integer getAps() {
        if (minAps.getValue().equals(20.0)) {
            return 1000 / 20;
        }
        if (minAps.getValue().equals(aps.getValue())) {
            return 1000 / aps.getValue().intValue();
        }
        return 1000 / MathUtil.randomNumber(aps.getValue().intValue(), minAps.getValue().intValue());
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
        List<Entity> loadedEntityList = mc.theWorld.loadedEntityList;
        for (Object o : loadedEntityList) {
            Entity entity = (Entity) o;
            if (((Entity) o).isDead || !this.blockValidator.validate(entity)) continue;
            return fov.getValue().equals(180d) || RotationUtils.isVisibleFOV(entity, fov.getValue().intValue());
        }
        return false;
    }

    private void attack(EntityLivingBase entity) {
        EntityPlayerSP player = mc.thePlayer;
        if (autoBlockMode.getValue().equals(AutoBlockMode.Always) && isBlocking && curTarget != null) {
            isBlocking = false;
        }
        unblock();
        if (swing.getValue()) {
            player.swingItem();
        } else {
            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }
        if (dbtap.getValue()) {
            mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
        mc.getNetHandler().addToSendQueue(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
    }

    //    取消格挡
    private void unblock() {
        if ((this.autoBlock.getValue().booleanValue() || mc.thePlayer.isBlocking()) && isHoldingSword() && isBlocking) {
//            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 0);
            switch ((AutoBlockMode) this.autoBlockMode.getValue()) {
                case KeyBind:
                case Always:
                    mc.gameSettings.keyBindUseItem.Doing = false;
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    break;
                case HVH:
                    mc.gameSettings.keyBindUseItem.Doing = false;
                    mc.getNetHandler().addToSendQueueSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    break;
                case Packet:
                    mc.getNetHandler().addToSendQueueSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    break;
            }
            isBlocking = false;
        }
    }

    private void block() {
        if (this.autoBlock.getValue() && isHoldingSword() && this.isEntityNearby() && !isBlocking) {
//            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), mc.thePlayer.getHeldItem().getMaxItemUseDuration());
            switch ((AutoBlockMode) this.autoBlockMode.getValue()) {
                case Packet:
                    mc.getNetHandler().addToSendQueueSilent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
                    break;
                case HVH:
                    mc.gameSettings.keyBindUseItem.Doing = true;
                    mc.getNetHandler().addToSendQueueSilent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
                    break;
                case KeyBind:
                case Always:
//                    Doing比pressed有用
                    mc.gameSettings.keyBindUseItem.Doing = true;
                    break;
            }
            isBlocking = true;
        }
    }

    private boolean canAttack() {
        return !ModuleManager.getModuleByClass(Scaffold.class).isEnabled();
    }

    private void updateTargets() {
        this.targets.clear();
        List<Entity> entities = mc.theWorld.loadedEntityList;
        for (Entity o : entities) {
            EntityLivingBase entityLivingBase;
            if (!(o instanceof EntityLivingBase) || o.isDead || ((EntityLivingBase) o).getHealth() <= 0 || !this.entityValidator.validate(entityLivingBase = (EntityLivingBase) o))
                continue;
            if (fov.getValue().equals(180d) || RotationUtils.isVisibleFOV(o, fov.getValue().intValue()))
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
        Packet, KeyBind, HVH, Always
    }

    private enum Mode {
        SINGLE, SWITCH
    }

    private enum AttackTiming {
        Pre, Post, Motion, Update, All, Render3D
    }

    private enum ABTiming {
        Pre, Post, Motion, Update, All, Render3D
    }

    private enum Rotations {
        Basic, Hypixel, HVH, None
    }
}
