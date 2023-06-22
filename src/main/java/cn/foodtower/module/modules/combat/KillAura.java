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

public class KillAura extends Module {
    public static boolean isBlocking;
    public static EntityLivingBase curTarget;
    private final Option autoBlock = new Option("Auto Block", true);
    private final Option noHitCheck = new Option("NoHitCheck", false);
    private final Option coolDown = new Option("Auto CoolDown", false);
    private final Option esp = new Option("ESP", true);
    private final Option predictBlock = new Option("PredictBlock", false);
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
    private final MSTimer critStopwatch = new MSTimer();
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
        addValues(mode, sortingMode, switchDelay, aps, minAps, range, wall, autoBlock, autoBlockMode, attackTiming, abTiming, rotMode, rotSpeed, noHitCheck, silent, this.teams, this.players, this.prioritizePlayers, this.animals, this.monsters, this.invisibles, esp, swing, predictBlock, coolDown, dbtap, fov, this.forceUpdate, this.disableOnDeath);
        setValueDisplayable(sortingMode, mode, Mode.Single);
        setValueDisplayable(switchDelay, mode, Mode.Switch);
        setValueDisplayable(new Value[]{autoBlockMode, abTiming}, autoBlock, autoBlock.get());
        setValueDisplayable(prioritizePlayers, players, players.get());
    }

    @Override
    public void onDisable() {
        if (autoBlock.get()) {
            this.unblock();
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
                        angles = RotationUtils.getRotationsEntityEye(curTarget);
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
        if (((abTiming.get().equals(ABTiming.Pre) && e.isPre()) || (abTiming.get().equals(ABTiming.Post) && !e.isPre()) || abTiming.get().equals(ABTiming.All)) && predictBlock.get())
            block();
        if ((attackTiming.get().equals(AttackTiming.Pre) && e.isPre()) || (attackTiming.get().equals(AttackTiming.Post) && !e.isPre()) || attackTiming.get().equals(AttackTiming.All))
            attack();
        if ((abTiming.get().equals(ABTiming.Pre) && e.isPre()) || (abTiming.get().equals(ABTiming.Post) && !e.isPre()) || abTiming.get().equals(ABTiming.All))
            block();
    }

    @EventHandler
    private void onUpdate(EventUpdate e) {
        this.updateTargets();
        this.sortTargets();
        ++hitTicks;
        if (!isHoldingSword()) {
            isBlocking = false;
        }
        if (curTarget == null && autoBlock.get() && isBlocking) {
            unblock();
        }
        if ((abTiming.get().equals(ABTiming.Update) || abTiming.get().equals(ABTiming.All)) && predictBlock.get())
            block();
        if (attackTiming.get().equals(AttackTiming.Update) || attackTiming.get().equals(AttackTiming.All)) attack();
        if (abTiming.get().equals(ABTiming.Update) || abTiming.get().equals(ABTiming.All)) block();
    }

    @EventHandler
    private void onRender(EventRender3D e) {
        if (curTarget != null && esp.get()) {
            RenderUtil.drawShadow(curTarget, e.getPartialTicks(), (float) yPos, direction);
            RenderUtil.drawCircle(curTarget, e.getPartialTicks(), (float) yPos);
        }
//        水影esp(不好看所以删了)
//        if (curTarget != null) {
//            Color color = curTarget.hurtTime > 0 ? new Color(-1618884) : new Color(-13330213);
//            double x;
//            double y;
//            double z;
//            x = curTarget.lastTickPosX + (curTarget.posX - curTarget.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
//            mc.getRenderManager();
//            y = curTarget.lastTickPosY + (curTarget.posY - curTarget.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
//            mc.getRenderManager();
//            z = curTarget.lastTickPosZ + (curTarget.posZ - curTarget.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
//            x -= 0.5;
//            z -= 0.5;
//            y += curTarget.getEyeHeight() + 0.35 - (curTarget.isSneaking() ? 0.25 : 0.0);
//            final double mid = 0.5;
//            GL11.glPushMatrix();
//            GL11.glEnable(3042);
//            GL11.glBlendFunc(770, 771);
//            GL11.glTranslated(x + mid, y + mid, z + mid);
//            GL11.glRotated(-curTarget.rotationYaw % 360.0f, 0.0, 1.0, 0.0);
//            GL11.glTranslated(-(x + mid), -(y + mid), -(z + mid));
//            GL11.glDisable(3553);
//            GL11.glEnable(2848);
//            GL11.glDisable(2929);
//            GL11.glDepthMask(false);
//            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
//            GL11.glLineWidth(2.0f);
//            GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
//            RenderUtil.drawBoundingBox(new AxisAlignedBB(x + 0.2, y - 0.04, z + 0.2, x + 0.8, y + 0.01, z + 0.8));
//            GL11.glDisable(2848);
//            GL11.glEnable(3553);
//            GL11.glEnable(2929);
//            GL11.glDepthMask(true);
//            GL11.glDisable(3042);
//            GL11.glPopMatrix();
//        }
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
        if (swing.get()) {
            player.swingItem();
        } else {
            mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }
        EventAttack attack = new EventAttack(curTarget);
        EventBus.getInstance().register(attack);
        if (attack.isCancelled()) return;
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
//            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 7199);
            switch ((AutoBlockMode) this.autoBlockMode.get()) {
                case Vanilla:
                case AAC:
                case Packet:
                    sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlocking = false;
                    break;
                case DCJ:
                    sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), EnumFacing.DOWN));
                    isBlocking = false;
                    break;
            }
        }
    }

    private void block() {
        if (this.autoBlock.get() && isHoldingSword() && this.isEntityNearby() && !isBlocking) {
//            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 7199);
            switch ((AutoBlockMode) this.autoBlockMode.get()) {
                case Vanilla:
                    if (mc.gameSettings.keyBindUseItem.isKeyDown()) {
                        mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
                        isBlocking = true;
                    } else {
                        isBlocking = false;
                    }
                    break;
                case Packet:
                    sendPacket(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0.0f, 0.0f, 0.0f));
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
                    sendPacket(new C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
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
        Packet, Vanilla, AAC, DCJ
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
