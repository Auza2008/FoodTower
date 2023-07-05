package cn.foodtower.module.modules.combat;


import cn.foodtower.Client;
import cn.foodtower.api.EventBus;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.ui.notifications.user.Notifications;
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
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static cn.foodtower.util.math.RotationUtils.getRotationFromPosition;

public class KillAura extends Module {
    public static boolean isBlocking;
    public static EntityLivingBase curTarget;
    private final Option autoBlock = new Option("Auto Block", true);
    private final Option noHitCheck = new Option("NoHitCheck", false);
    private final Option coolDown = new Option("Auto CoolDown", false);
    private final Option esp = new Option("ESP", true);
    private final Option predictBlock = new Option("PredictBlock", false);
    private final Option dbBlock = new Option("DoubleBlock", false);
    private final Numbers<Double> aps = new Numbers("MaxCps", 13.0, 1.0, 20.0, 1.0);
    private final Numbers<Double> minAps = new Numbers<>("MinCps", 10.0, 1.0, 20.0, 1.0);
    private final cn.foodtower.api.value.Mode mode = new cn.foodtower.api.value.Mode("Mode", Mode.values(), Mode.Switch);
    private final cn.foodtower.api.value.Mode autoBlockMode = new cn.foodtower.api.value.Mode("Auto Block Mode", AutoBlockMode.values(), AutoBlockMode.Packet);
    private final Numbers<Double> switchDelay = new Numbers("Switch Delay", 3.0, 1.0, 10.0, 1.0);
    private final cn.foodtower.api.value.Mode sortingMode = new cn.foodtower.api.value.Mode("Sorting Mode", SortingMode.values(), SortingMode.DISTANCE);
    private final Numbers<Double> range = new Numbers("Range", 4.2, 3.0, 8.0, 0.1);
    private final Option teams = new Option("Teams", true);
    private final Option players = new Option("Players", true);
    private final Option animals = new Option("Animals", false);
    private final Option monsters = new Option("Monsters", false);
    private final Option prioritizePlayers = new Option("Prioritize Players", true);
    private final Option invisibles = new Option("Invisibles", false);
    private final Option forceUpdate = new Option("Force Update", false);
    private final Option disableOnDeath = new Option("Disable on death", true);
    private final java.util.List<EntityLivingBase> targets = new ArrayList<>();
    private final Option silent = new Option("SilentRotation", true);
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
    private final EntityValidator entityValidator = new EntityValidator();
    private final EntityValidator blockValidator = new EntityValidator();
    private final SmoothRotationObject smoothRotationObject = new SmoothRotationObject();
    private final TimerUtil espTimer = new TimerUtil();
    boolean direction = true;
    double yPos;
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
        addValues(mode, sortingMode, switchDelay, aps, minAps, range, wall, autoBlock, autoBlockMode, predictBlock, attackTiming, abTiming, rotMode, rotSpeed, silent, noHitCheck, teams, players, prioritizePlayers, animals, monsters, invisibles, esp, swing, coolDown, dbtap, dbBlock, fov, forceUpdate, disableOnDeath);
        setValueDisplayable(sortingMode, mode, Mode.Single);
        setValueDisplayable(switchDelay, mode, Mode.Switch);
        setValueDisplayable(new Value[]{autoBlockMode, abTiming}, autoBlock, autoBlock.get());
        setValueDisplayable(prioritizePlayers, players, players.get());
    }

    public static float[] getRotationsDCJPitch(final EntityLivingBase ent) {
        final double x = ent.posX;
        final double z = ent.posZ;
        final double y = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.5;
        return getRotationFromPosition(x, z, y);
    }

    @Override
    public void onDisable() {
        this.unblock();
        curTarget = null;
    }

    @Override
    public void onEnable() {
        this.updateStopwatch.reset();
        this.targetIndex = 0;
        this.targets.clear();
        this.changeTarget = false;
    }

    @EventHandler
    public void Event(EventWorldChanged e) {
        if (disableOnDeath.get()) {
            Notifications.getManager().post("KillAura", "检测到世界变更！已自动关闭KillAura");
            this.setEnabled(false);

        }
    }

    @EventHandler
    private void onPre(EventPreUpdate e) {
        if (curTarget != null && this.canAttack()) {
            if (this.updateStopwatch.hasTimePassed(56L) && this.forceUpdate.get() && !mc.thePlayer.isMoving()) {
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                this.updateStopwatch.reset();
            }
            if (!rotMode.get().equals(Rotations.None)) {
                switch ((Rotations) (rotMode.get())) {
                    case Basic:
                        angles = RotationUtils.getRotationsEntity(curTarget);
                        break;
                    case Predict:
                        angles = RotationUtils.getPredictedRotations(curTarget);
                        break;
                    case HVH:
                        angles = RotationUtils.getCustomRotation(RotationUtils.getLocation(curTarget.getEntityBoundingBox()));
                        break;
                    case DCJ:
                        if (mc.thePlayer != null && (curTarget.getEntityBoundingBox().minY > mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.5 || mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - 0.5 > curTarget.getEntityBoundingBox().maxY)) {
                            angles[0] = RotationUtils.getRotationsEntity(curTarget)[0];
                            angles[1] = RotationUtils.getRotationsEntity(curTarget)[1] + curTarget.getEyeHeight() / 2f - 0.5f;
                        } else {
                            angles = getRotationsDCJPitch(curTarget);
                        }
                        break;
                }
                if (!silent.get()) {
                    mc.thePlayer.rotationYaw = angles[0];
                    mc.thePlayer.rotationPitch = angles[1];
                } else {
                    if (rotSpeed.get() == 180d) {
                        e.setYaw(angles[0]);
                        e.setPitch(angles[1]);
                        Client.RenderRotate(angles[0], angles[1]);
                    } else {
                        smoothRotationObject.setWillYawPitch(angles[0], angles[1]);
                        smoothRotationObject.handleRotation(rotSpeed.get());
                        smoothRotationObject.setPlayerRotation(e);
                    }
                }
            }
        }
    }

    @EventHandler
    private void runAttack(EventMotionUpdate e) {
        setSuffix(mode.get());
        if (((abTiming.get().equals(ABTiming.Pre) && e.isPre()) || (abTiming.get().equals(ABTiming.Post) && !e.isPre())) && predictBlock.get())
            block();
        if ((attackTiming.get().equals(AttackTiming.Pre) && e.isPre()) || (attackTiming.get().equals(AttackTiming.Post) && !e.isPre()))
            attack();
        if ((abTiming.get().equals(ABTiming.Pre) && e.isPre()) || (abTiming.get().equals(ABTiming.Post) && !e.isPre()) && !predictBlock.get())
            block();
    }

    @EventHandler
    private void onUpdate(EventUpdate e) {
        if (curTarget == null || mc.thePlayer.getDistanceToEntity(curTarget) > range.get() || (curTarget.getHealth() <= 0 || curTarget.isDead) || targets.isEmpty()) {
            this.updateTargets();
            this.sortTargets();
        }
        ++hitTicks;
        if (!isHoldingSword()) {
            if (isBlocking || mc.thePlayer.isBlocking()) unblock();
        }
        if (curTarget == null) {
            unblock();
        }
        if (abTiming.get().equals(ABTiming.Update) && predictBlock.get()) block();
        if (attackTiming.get().equals(AttackTiming.Update)) attack();
        if (abTiming.get().equals(ABTiming.Update) && !predictBlock.get()) block();
    }

    @EventHandler
    private void onRender(EventRender3D e) {
        if (abTiming.get().equals(ABTiming.All) && predictBlock.get()) block();
        if (attackTiming.get().equals(AttackTiming.All)) attack();
        if (abTiming.get().equals(ABTiming.All) && !predictBlock.get()) block();
        if (curTarget != null && esp.get()) {
            RenderUtil.drawShadow(curTarget, e.getPartialTicks(), (float) yPos, direction);
            RenderUtil.drawCircle(curTarget, e.getPartialTicks(), (float) yPos);
        }
    }

    @EventHandler
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

    private void attack() {
        double delayValue = -1;
        if (this.coolDown.get()) {
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
            if (((!coolDown.get() && this.attackStopwatch.hasTimePassed(getAps())) || (coolDown.get() && hitTicks > delayValue))) {
                if (noHitCheck.get() || getHitable()) {
                    this.attack(curTarget);
                    this.attackStopwatch.reset();
                    hitTicks = 0;
                }
            }
            if ((double) curTarget.hurtTime >= this.switchDelay.get()) {
                this.changeTarget = true;
            }
        }
    }

    private int getAps() {
//        if (aps.get().equals(minAps.get())) {
//            return 1000 / aps.get().intValue();
//        }
        return 1000 / MathUtil.randomNumber(aps.get().intValue(), minAps.get().intValue());
    }

    public final EntityLivingBase getTarget() {
        if (this.targets.isEmpty()) {
            return null;
        }
        if (this.mode.get() == Mode.Single) {
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

    private boolean getHitable() {
        return RotationUtils.isFaced(curTarget, range.get());
    }

    private boolean isEntityNearby() {
        List<Entity> loadedEntityList = mc.theWorld.loadedEntityList;
        for (Entity o : loadedEntityList) {
            Entity entity = o;
            if (o.isDead || !this.blockValidator.validate(entity)) continue;
            return fov.get().equals(180d) || RotationUtils.isVisibleFOV(entity, fov.get().intValue());
        }
        return false;
    }

    private void attack(EntityLivingBase entity) {
        EntityPlayerSP player = mc.thePlayer;
        unblock();
        if (autoBlockMode.get().equals(AutoBlockMode.Vanilla) && (mc.thePlayer.isBlocking() || isBlocking)) {
            sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
        }
        EventAttack attack = new EventAttack(curTarget);
        EventBus.getInstance().register(attack);
        if (attack.isCancelled()) return;
        if (swing.get()) {
            player.swingItem();
        } else {
            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }
        if (dbtap.get()) {
            sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
        if (ModuleManager.getModuleByClass(KeepSprint.class).isEnabled()) {
            sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        } else {
            mc.playerController.attackEntity(mc.thePlayer, curTarget);
        }
    }

    //    取消格挡
    private void unblock() {
        if ((this.autoBlock.get() || mc.thePlayer.isBlocking()) && isHoldingSword() && isBlocking) {
            switch ((AutoBlockMode) this.autoBlockMode.get()) {
                case Right:
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    isBlocking = false;
                    break;
                case NCP:
                case AAC:
                case Vanilla:
                case Packet:
                    sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlocking = false;
                    break;
                case DCJ:
                    sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                    isBlocking = false;
                    break;
            }
        }
    }

    private void block() {
        if (this.autoBlock.get() && isHoldingSword() && this.isEntityNearby() && !isBlocking) {
            for (int i = 0; dbBlock.get() ? i < 2 : i < 1; ++i) {
                switch ((AutoBlockMode) this.autoBlockMode.get()) {
                    case Right:
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        isBlocking = true;
                        break;
                    case Vanilla:
                    case Packet:
                        sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        isBlocking = true;
                        break;
                    case AAC:
                        if (mc.thePlayer.ticksExisted % 2 == 0) {
                            mc.playerController.interactWithEntitySendPacket(mc.thePlayer, curTarget);
                            sendPacket(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
                        }
                        isBlocking = true;
                        break;
                    case DCJ:
                        sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0f, 0f, 0f));
                        KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                        isBlocking = true;
                        break;
                    case NCP:
                        sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, null, 0f, 0f, 0f));
                        isBlocking = true;
                        break;
                }
            }
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
            if (fov.get().equals(180d) || RotationUtils.isVisibleFOV(o, fov.get().intValue()))
                this.targets.add(entityLivingBase);
        }
    }

    private void sortTargets() {
        switch ((SortingMode) this.sortingMode.get()) {
            case HEALTH: {
                this.targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
                break;
            }
            case DISTANCE: {
                this.targets.sort(Comparator.comparingDouble(entity -> mc.thePlayer.getDistanceToEntity(entity)));
            }
        }
        if (this.prioritizePlayers.get()) {
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
        Packet, Right, Vanilla, NCP, AAC, DCJ
    }

    private enum Mode {
        Single, Switch
    }

    private enum AttackTiming {
        Pre, Post, Update, All
    }

    private enum ABTiming {
        Pre, Post, Update, All
    }

    private enum Rotations {
        Basic, Predict, HVH, DCJ, None
    }
}
