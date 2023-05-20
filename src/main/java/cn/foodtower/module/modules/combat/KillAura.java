package cn.foodtower.module.modules.combat;


import cn.foodtower.Client;
import cn.foodtower.api.EventBus;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.EventAttack;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.events.World.EventTick;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.world.Scaffold;
import cn.foodtower.util.entity.entitycheck.EntityValidator;
import cn.foodtower.util.entity.entitycheck.checks.*;
import cn.foodtower.util.math.MathUtil;
import cn.foodtower.util.render.RenderUtil;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.entity.RenderManager;
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
    public final Numbers<Double> aps = new Numbers("APS", 13.0, 1.0, 20.0, 1.0);
    public final Numbers<Double> randomization = new Numbers<>("Randomization", 0.0, 0.0, 5.0, 1.0);
    public final cn.foodtower.api.value.Mode mode = new cn.foodtower.api.value.Mode("Mode", Mode.values(), Mode.SWITCH);
    public final cn.foodtower.api.value.Mode autoBlockMode = new cn.foodtower.api.value.Mode("Auto Block Mode", AutoBlockMode.values(), AutoBlockMode.OFFSET);
    public final Numbers<Double> switchDelay = new Numbers("Switch Delay", 3.0, 1.0, 10.0, 1.0);
    public final cn.foodtower.api.value.Mode sortingMode = new cn.foodtower.api.value.Mode("Sorting Mode", SortingMode.values(), SortingMode.DISTANCE);
    public final Numbers<Double> range = new Numbers("Range", 4.2, 3.0, 7.0, 0.1);
    public final Option teams = new Option("Teams", true);
    public final Option players = new Option("Players", true);
    public final Option animals = new Option("Animals", false);
    public final Option monsters = new Option("Monsters", false);
    private int hitTicks;
    public final Option prioritizePlayers = new Option("Prioritize Players", true);
    public final Option invisibles = new Option("Invisibles", false);
    public final Option forceUpdate = new Option("Force Update", false);
    public final Option disableOnDeath = new Option("Disable on death", true);
    public final java.util.List<EntityLivingBase> targets = new ArrayList<>();
    private final Option swing = new Option("Swing", true);
    private final Option dbtap = new Option("Double Tap", false);
    private final cn.foodtower.api.value.Mode attackTiming = new cn.foodtower.api.value.Mode("AttackTiming", AttackTiming.values(), AttackTiming.Post);
    private final cn.foodtower.api.value.Mode abTiming = new cn.foodtower.api.value.Mode("BlockTiming", ABTiming.values(), ABTiming.Post);
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
        addValues(this.mode, this.sortingMode, this.autoBlockMode, this.aps, randomization, this.range, attackTiming, abTiming, rotMode, this.switchDelay, this.teams, this.players, this.prioritizePlayers, this.animals, this.monsters, this.invisibles, this.autoBlock, swing, coolDown, dbtap, this.forceUpdate, this.disableOnDeath);
        setValueDisplayable(sortingMode, mode, Mode.SINGLE);
        setValueDisplayable(switchDelay, mode, Mode.SWITCH);
    }

    public static float[] getRotationsToEnt(EntityLivingBase ent) {
        double differenceX = ent.posX - mc.thePlayer.posX;
        double differenceY = ent.posY + (double) ent.height - (mc.thePlayer.posY + (double) mc.thePlayer.height) - 0.5;
        double differenceZ = ent.posZ - mc.thePlayer.posZ;
        float rotationYaw = (float) (Math.atan2(differenceZ, differenceX) * 180.0 / Math.PI) - 90.0f;
        float rotationPitch = (float) (Math.atan2(differenceY, mc.thePlayer.getDistanceToEntity(ent)) * 180.0 / Math.PI);
        float finishedYaw = mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(rotationYaw - mc.thePlayer.rotationYaw);
        float finishedPitch = mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(rotationPitch - mc.thePlayer.rotationPitch);
        return new float[]{finishedYaw, -MathHelper.clamp_float(finishedPitch, -90.0f, 90.0f)};
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
        if (autoBlockMode.getValue().equals(AutoBlockMode.Always) || autoBlockMode.getValue().equals(AutoBlockMode.HVH)) {
            mc.thePlayer.stopUsingItem();
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
            isBlocking = false;
        } else {
            this.unblock();
        }
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
        ++hitTicks;
        this.updateTargets();
        this.sortTargets();
        if (!isHoldingSword()) {
            isBlocking = false;
        }
        if (curTarget == null) {
            if ((autoBlockMode.getValue().equals(AutoBlockMode.Always) || autoBlockMode.getValue().equals(AutoBlockMode.HVH)) && isEnabled()) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), false);
                mc.thePlayer.stopUsingItem();
                isBlocking = false;
            } else {
                this.unblock();
            }
        }
        if (curTarget != null && isEnabled()) {
            if (autoBlockMode.getValue().equals(AutoBlockMode.HVH) || autoBlockMode.getValue().equals(AutoBlockMode.Always)) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                isBlocking = true;
            }
        }
        if (this.canAttack() && curTarget != null) {
            if (this.updateStopwatch.hasReached(56L) && this.forceUpdate.getValue().booleanValue() && !mc.thePlayer.isMoving()) {
                mc.getNetHandler().addToSendQueueSilent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround));
                this.updateStopwatch.reset();
            }
            float[] angles = new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
            switch ((Rotations) (rotMode.getValue())) {
                case Basic:
                    angles = getRotationsToEnt(curTarget);
                    break;
                case Hypixel:
                    angles = getNeededRotations(curTarget);
                    break;
                case HVH:
                    angles = cn.foodtower.util.math.RotationUtil.getCustomRotation(cn.foodtower.util.math.RotationUtil.getLocation(curTarget.getEntityBoundingBox()));
                    break;
                case None:
                    break;
            }
            e.setYaw(angles[0]);
            e.setPitch(angles[1]);
            Client.RenderRotate(angles[0], angles[1]);
        }
    }

    @EventHandler
    private void onRender(EventRender3D e) {
        if (attackTiming.getValue().equals(AttackTiming.All)) {
            attack();
        }
        if (abTiming.getValue().equals(ABTiming.All)) {
            this.block();
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

    @EventHandler
    public final void onMotionUpdate(EventMotionUpdate event) {
        if (event.isPre()) {
            if (attackTiming.getValue().equals(AttackTiming.Pre)) {
                attack();
            }
            if (abTiming.getValue().equals(ABTiming.Pre)) {
                this.block();
            }
        }
        if (!event.isPre()) {
            if (attackTiming.getValue().equals(AttackTiming.Post)) {
                attack();
            }
            if (abTiming.getValue().equals(ABTiming.Post)) {
                this.block();
            }
        }
    }

    @EventHandler
    private void onTick(EventTick tick) {
        if (attackTiming.getValue().equals(AttackTiming.Tick)) {
            attack();
        }
        if (abTiming.getValue().equals(ABTiming.Tick)) {
            block();
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

                if (item == Items.wooden_axe || item == Items.stone_axe)
                    delayValue = 25;

                if (item instanceof ItemSword)
                    delayValue = 12;

                if (item instanceof ItemPickaxe)
                    delayValue = 17;

                if (item == Items.iron_axe)
                    delayValue = 22;

                if (item == Items.stone_hoe)
                    delayValue = 10;

                if (item == Items.iron_hoe)
                    delayValue = 7;
            }
            delayValue *= Math.max(1, mc.timer.timerSpeed);
        }
        curTarget = this.getTarget();
        if (curTarget != null && this.canAttack()) {
            if (((!coolDown.getValue() && this.attackStopwatch.hasReached(getAps())) || (coolDown.getValue() && hitTicks > delayValue))) {
                EventBus.getInstance().register(new EventAttack(curTarget, true));
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
        if (randomization.getValue().intValue() == 0) {
            return 1000 / aps.getValue().intValue();
        }
        return 1000 / MathUtil.randomNumber((aps.getValue().intValue() + randomization.getValue().intValue()), MathHelper.abs_int(aps.getValue().intValue() - randomization.getValue().intValue()));
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
            if (!this.blockValidator.validate(entity)) continue;
            return true;
        }
        return false;
    }

    private void attack(EntityLivingBase entity) {
        EntityPlayerSP player = mc.thePlayer;
        this.unblock();
        if (swing.getValue()) {
            player.swingItem();
        } else {
            sendPacket(new C0APacketAnimation());
        }
        if (dbtap.getValue()) {
            sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
        }
        sendPacket(new C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK));
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
                case Always:
                case FAKE:
                    isBlocking = false;
                    break;
                case HVH:
                    mc.playerController.onStoppedUsingItem(mc.thePlayer);
                    mc.getNetHandler().addToSendQueueSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    isBlocking = false;
                    break;
                case SMART:
                    mc.getNetHandler().addToSendQueueSilent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
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
                case HVH:
                    mc.getNetHandler().addToSendQueueSilent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getHeldItem(), 0.0f, 0.0f, 0.0f));
                    isBlocking = true;
                    break;
                case OFFSET:
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindUseItem.getKeyCode(), true);
                    isBlocking = true;
                    break;
                case FAKE:
                case Always:
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
            if (!(o instanceof EntityLivingBase) || !this.entityValidator.validate(entityLivingBase = (EntityLivingBase) o))
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
        SMART, Always, OFFSET, HVH, FAKE
    }

    private enum Mode {
        SINGLE, SWITCH
    }

    private enum AttackTiming {
        Pre, Post, Tick, All
    }

    private enum ABTiming {
        Pre, Post, Tick, All
    }

    private enum Rotations {
        Basic, Hypixel, HVH, None
    }
}
