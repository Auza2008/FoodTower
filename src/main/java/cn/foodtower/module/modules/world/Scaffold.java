package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.api.value.Value;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.font.CFontRenderer;
import cn.foodtower.ui.font.FontLoaders;
import cn.foodtower.util.entity.InventoryUtils;
import cn.foodtower.util.entity.MovementUtils;
import cn.foodtower.util.entity.SafeWalkUtil;
import cn.foodtower.util.math.Rotation;
import cn.foodtower.util.misc.liquidbounce.LiquidRender;
import cn.foodtower.util.misc.liquidbounce.RotationUtils;
import cn.foodtower.util.misc.scaffold.PlaceRotation;
import cn.foodtower.util.misc.scaffold.blocks.PlaceInfo;
import cn.foodtower.util.render.Colors;
import cn.foodtower.util.render.DrawUtil;
import cn.foodtower.util.time.MSTimer;
import cn.foodtower.util.time.TimeUtils;
import cn.foodtower.util.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class Scaffold extends Module {

    public final static List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web);
    public static boolean renderBlockCount = false;
    /**
     * OPTIONS
     */

    // Mode
    public final Mode modeValue = new Mode("Mode", ScaffoldMode.values(), ScaffoldMode.Normal);
    public final Mode rotMode = new Mode("RotationMode", RotMode.values(), RotMode.Normal);
    // Basic stuff
    public final Option sprintValue = new Option("Sprint", true);
    // Delay
    private final Numbers<Double> maxDelayValue = new Numbers<>("MaxDelay", 0d, 0d, 1000d, 1d);
    private final Numbers<Double> minDelayValue = new Numbers<>("MinDelay", 0d, 0d, 1000d, 1d);
    private final Option placeableDelay = new Option("PlaceableDelay", false);
    // AutoBlock
    private final Option autoBlockValue = new Option("AutoBlock", true);
    private final Option stayAutoBlock = new Option("StayAutoBlock", false);
    //SetMotion
    private final Option SetMotion = new Option("SetMotion", false);
    private final Option rotationStrafeValue = new Option("RotationStrafe", false);
    private final Numbers<Double> MotionSpeed = new Numbers<>("MotionSpeed", 0.22d, 0d, 1d, 0.01d);
    private final Option swingValue = new Option("Swing", true);
    private final Option searchValue = new Option("Search", true);
    private final Option GroundOnly = new Option("OnlyGround", true);
    private final Option downValue = new Option("Down", true);
    private final Mode placeModeValue = new Mode("PlaceTiming", PlaceTiming.values(), PlaceTiming.Post);
    // Eagle
    private final Option eagleValue = new Option("Eagle", false);
    private final Option eagleSilentValue = new Option("EagleSilent", false);
    private final Numbers<Double> blocksToEagleValue = new Numbers<>("BlocksToEagle", 0d, 0d, 10d, 1d);
    // Expand
    private final Numbers<Double> expandLengthValue = new Numbers<>("ExpandLength", 5d, 1d, 6d, 1d);
    // Rotations
    private final Option rotationsValue = new Option("Rotations", true);
    private final Numbers<Double> keepLengthValue = new Numbers<>("KeepRotationLength", 0d, 0d, 20d, 1d);
    private final Option keepRotationValue = new Option("KeepRotation", false);
    private final Numbers<Double> staticPitchValue = new Numbers<>("StaticPitch", 86d, 80d, 90d, 1d);
    // Zitter
    private final Option zitterValue = new Option("Zitter", false);
    private final Mode zitterModeValue = new Mode("ZitterMode", ZitterMode.values(), ZitterMode.Teleport);
    private final Numbers<Double> zitterSpeed = new Numbers<>("ZitterSpeed", 0.13d, 0.1d, 0.3d, 0.01d);
    private final Numbers<Double> zitterStrength = new Numbers<>("ZitterStrength", 0.072d, 0.05d, 0.2d, 0.01d);
    // Game
    private final Numbers<Double> timerValue = new Numbers<>("Timer", 1d, 0.1d, 10d, 0.01d);
    private final Numbers<Double> speedModifierValue = new Numbers<>("SpeedModifier", 1d, 0d, 2d, 1d);
    // Safety
    private final Option sameYValue = new Option("SameY", false);
    private final Option safeWalkValue = new Option("SafeWalk", true);
    private final Option airSafeValue = new Option("AirSafe", false);
    // Visuals
    private final Option counterDisplayValue = new Option("Counter", true);
    private final Option markValue = new Option("Mark", true);
    // Delay
    private final MSTimer delayTimer = new MSTimer();
    private final MSTimer zitterTimer = new MSTimer();
    /**
     * MODULE
     */

    // Target block
    private PlaceInfo targetPlace;
    // Launch position
    private int launchY;
    // Rotation lock
    private Rotation lockRotation;
    // Auto block slot
    private int slot;
    // Zitter Smooth
    private boolean zitterDirection;
    private long delay;
    // Eagle
    private int placedBlocksWithoutEagle = 0;
    private boolean eagleSneaking;
    // Down
    private boolean shouldGoDown = false;

    public Scaffold() {
        super("Scaffold", new String[]{"BlockFly"}, ModuleType.World);
        addValues(modeValue, maxDelayValue, minDelayValue, placeableDelay, autoBlockValue, stayAutoBlock, SetMotion, MotionSpeed, GroundOnly, sprintValue, swingValue, searchValue, downValue, placeModeValue, eagleValue, eagleSilentValue, blocksToEagleValue, expandLengthValue, rotationsValue, rotMode, staticPitchValue, rotationStrafeValue, keepLengthValue, keepRotationValue, zitterValue, zitterModeValue, zitterSpeed, zitterStrength, timerValue, speedModifierValue, sameYValue, safeWalkValue, airSafeValue, counterDisplayValue, markValue);
        setValueDisplayable(new Value[]{MotionSpeed, GroundOnly}, SetMotion, !SetMotion.get());
        setValueDisplayable(staticPitchValue, rotMode, RotMode.Static);
    }

    public static void renderBlock() {
        String var10001 = getBlocksAmount() == 0 ? "No blocks left." : Integer.toString(getBlocksAmount()) + EnumChatFormatting.GRAY + " blocks left.";
        RenderItem ir = new RenderItem(mc.getTextureManager(), mc.modelManager);
        CFontRenderer font = FontLoaders.GoogleSans18;
        ScaledResolution sr = new ScaledResolution(mc);
        int var10002 = ScaledResolution.getScaledWidth() / 2 + 1;
        font.drawStringWithShadow(var10001, (var10002 - mc.fontRendererObj.getStringWidth(Integer.toString(getBlocksAmount())) / 2f), ScaledResolution.getScaledHeight() / 2 + 12, -1);
        RenderHelper.enableGUIStandardItemLighting();
        if (InventoryUtils.findAutoBlockBlock() != -1)
            ir.renderItemIntoGUI(mc.thePlayer.inventory.mainInventory[InventoryUtils.findAutoBlockBlock() - 36], var10002 - font.getStringHeight(Integer.toString(getBlocksAmount())) - 15, ScaledResolution.getScaledHeight() / 2 + 7);
        RenderHelper.disableStandardItemLighting();
    }

    /**
     * @return hotbar blocks amount
     */
    private static int getBlocksAmount() {
        int amount = 0;

        for (int i = 36; i < 45; i++) {
            final ItemStack itemStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();

            if (itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                final Block block = ((ItemBlock) itemStack.getItem()).getBlock();
                if (mc.thePlayer.getHeldItem() == itemStack || !InventoryUtils.BLOCK_BLACKLIST.contains(block))
                    amount += itemStack.stackSize;
            }
        }

        return amount;
    }

    public static float getScaffoldRotation(float yaw, float strafe) {
        float rotationYaw = yaw;

        rotationYaw += 180F;

        float forward = -0.5F;

        if (strafe < 0F) rotationYaw -= 90F * forward;

        if (strafe > 0F) rotationYaw += 90F * forward;

        return rotationYaw;
    }

    /**
     * Enable module
     */
    @Override
    public void onEnable() {
        if (mc.thePlayer == null) return;

        launchY = (int) mc.thePlayer.posY;
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventHandler
    public void onUpdate(final EventMotionUpdate event) {
        mc.timer.timerSpeed = timerValue.get().floatValue();

        shouldGoDown = downValue.get() && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && getBlocksAmount() > 1;
        if (shouldGoDown) {
            mc.gameSettings.keyBindSneak.pressed = false;
            SafeWalkUtil.setSafe(false);
        }

        if (sprintValue.get()) {
            if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                mc.thePlayer.setSprinting(false);
            }
        } else {
            mc.thePlayer.setSprinting(false);
        }

        if (mc.thePlayer.onGround) {
            final ScaffoldMode mode = (ScaffoldMode) modeValue.get();

            // Rewinside scaffold mode
            if (mode.equals(ScaffoldMode.Rewinside)) {
                MovementUtils.strafe(0.2F);
                mc.thePlayer.motionY = 0D;
            }

            // Smooth Zitter
            if (zitterValue.get() && zitterModeValue.get().equals(ZitterMode.Smooth)) {
                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed = false;

                if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false;

                if (zitterTimer.hasTimePassed(100)) {
                    zitterDirection = !zitterDirection;
                    zitterTimer.reset();
                }

                if (zitterDirection) {
                    mc.gameSettings.keyBindRight.pressed = true;
                    mc.gameSettings.keyBindLeft.pressed = false;
                } else {
                    mc.gameSettings.keyBindRight.pressed = false;
                    mc.gameSettings.keyBindLeft.pressed = true;
                }
            }

            // Eagle
            if (eagleValue.get() && !shouldGoDown) {
                if (placedBlocksWithoutEagle >= blocksToEagleValue.get()) {
                    final boolean shouldEagle = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1D, mc.thePlayer.posZ)).getBlock() == Blocks.air;

                    if (eagleSilentValue.get()) {
                        if (eagleSneaking != shouldEagle) {
                            mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, shouldEagle ? C0BPacketEntityAction.Action.START_SNEAKING : C0BPacketEntityAction.Action.STOP_SNEAKING));
                        }

                        eagleSneaking = shouldEagle;
                    } else mc.gameSettings.keyBindSneak.pressed = shouldEagle;

                    placedBlocksWithoutEagle = 0;
                } else placedBlocksWithoutEagle++;
            }

            // Zitter
            if (zitterValue.get() && zitterModeValue.get().equals(ZitterMode.Teleport)) {
                MovementUtils.strafe(zitterSpeed.get().floatValue());

                final double yaw = Math.toRadians(mc.thePlayer.rotationYaw + (zitterDirection ? 90D : -90D));
                mc.thePlayer.motionX -= Math.sin(yaw) * zitterStrength.get();
                mc.thePlayer.motionZ += Math.cos(yaw) * zitterStrength.get();
                zitterDirection = !zitterDirection;
            }
        }
    }

    @EventHandler
    public void onPacket(final EventPacket event) {
        if (mc.thePlayer == null) return;

        final Packet<?> packet = event.getPacket();

        // AutoBlock
        if (packet instanceof C09PacketHeldItemChange) {
            final C09PacketHeldItemChange packetHeldItemChange = (C09PacketHeldItemChange) packet;

            slot = packetHeldItemChange.getSlotId();
        }
    }

    @EventHandler
    public void onMotion(final EventMotion event) {
        final EventMotion.Type eventState = event.getTypes();

        // Lock Rotation
        if (rotationsValue.get() && keepRotationValue.get() && lockRotation != null)
            RotationUtils.setTargetRotation(lockRotation);

        // Place block
        if (placeModeValue.get().name().equalsIgnoreCase(eventState.name())) place();

        // Update and search for new block
        if (eventState == EventMotion.Type.PRE) update();

        // Reset placeable delay
        if (targetPlace == null && placeableDelay.get()) delayTimer.reset();
    }

    private void update() {
        final boolean isHeldItemBlock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock;
        if (autoBlockValue.get() ? InventoryUtils.findAutoBlockBlock() == -1 && !isHeldItemBlock : !isHeldItemBlock)
            return;

        findBlock(modeValue.get().equals(ScaffoldMode.Expand));
    }

    /**
     * Search for new target block
     */
    private void findBlock(final boolean expand) {
        final BlockPos blockPosition = shouldGoDown ? (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.6D, mc.thePlayer.posZ) : new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 0.6, mc.thePlayer.posZ).down()) : (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? new BlockPos(mc.thePlayer) : new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down());

        if (!expand && (!BlockUtils.isReplaceable(blockPosition) || search(blockPosition, !shouldGoDown))) return;

        if (expand) {
            for (int i = 0; i < expandLengthValue.get(); i++) {
                if (search(blockPosition.add(mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST ? i : 0, 0, mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH ? i : 0), false))

                    return;
            }
        } else if (searchValue.get()) {
            for (int x = -1; x <= 1; x++)
                for (int z = -1; z <= 1; z++)
                    if (search(blockPosition.add(x, 0, z), !shouldGoDown)) return;
        }
    }

    /**
     * Place target block
     */
    private void place() {
        if (targetPlace == null) {
            if (placeableDelay.get()) delayTimer.reset();
            return;
        }

        if (!delayTimer.hasTimePassed(delay) || (sameYValue.get() && launchY - 1 != (int) targetPlace.getVec3().yCoord))
            return;

        int blockSlot = -1;
        ItemStack itemStack = mc.thePlayer.getHeldItem();

        if (mc.thePlayer.getHeldItem() == null || !(mc.thePlayer.getHeldItem().getItem() instanceof ItemBlock) || mc.thePlayer.getHeldItem().stackSize <= 0) {
            if (!autoBlockValue.get()) return;

            blockSlot = InventoryUtils.findAutoBlockBlock();

            if (blockSlot == -1) return;

            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(blockSlot - 36));
            itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).getStack();
        }


        if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, itemStack, targetPlace.getBlockPos(), targetPlace.getEnumFacing(), targetPlace.getVec3())) {
            delayTimer.reset();
            delay = TimeUtils.randomDelay(minDelayValue.get().intValue(), maxDelayValue.get().intValue());

            if (mc.thePlayer.onGround) {
                final float modifier = speedModifierValue.get().floatValue();

                mc.thePlayer.motionX *= modifier;
                mc.thePlayer.motionZ *= modifier;
            }

            if (swingValue.get()) mc.thePlayer.swingItem();
            else mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
        }

        if (!stayAutoBlock.get() && blockSlot >= 0)
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));

        // Reset
        this.targetPlace = null;
    }

    @EventHandler
    //took it from applyrotationstrafe XD. staticyaw comes from bestnub.
    public void onStrafe(final EventStrafe event) {
        if (lockRotation != null && rotationStrafeValue.get()) {
            final int dif = (int) ((MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - lockRotation.getYaw() - 23.5F - 135) + 180) / 45);

            final float yaw = lockRotation.getYaw();
            final float strafe = event.getStrafe();
            final float forward = event.getForward();
            final float friction = event.getFriction();
            float calcForward = 0F;
            float calcStrafe = 0F;
            /*
            Rotation Dif

            7 \ 0 / 1     +  +  +      +  |  -
            6   +   2     -- F --      +  S  -
            5 / 4 \ 3     -  -  -      +  |  -
            */
            switch (dif) {
                case 0: {
                    calcForward = forward;
                    calcStrafe = strafe;
                    break;
                }
                case 1: {
                    calcForward += forward;
                    calcStrafe -= forward;
                    calcForward += strafe;
                    calcStrafe += strafe;
                    break;
                }
                case 2: {
                    calcForward = strafe;
                    calcStrafe = -forward;
                    break;
                }
                case 3: {
                    calcForward -= forward;
                    calcStrafe -= forward;
                    calcForward += strafe;
                    calcStrafe -= strafe;
                    break;
                }
                case 4: {
                    calcForward = -forward;
                    calcStrafe = -strafe;
                    break;
                }
                case 5: {
                    calcForward -= forward;
                    calcStrafe += forward;
                    calcForward -= strafe;
                    calcStrafe -= strafe;
                    break;
                }
                case 6: {
                    calcForward = -strafe;
                    calcStrafe = forward;
                    break;
                }
                case 7: {
                    calcForward += forward;
                    calcStrafe += forward;
                    calcForward -= strafe;
                    calcStrafe += strafe;
                    break;
                }
            }

            if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
                calcForward *= 0.5f;
            }

            if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
                calcStrafe *= 0.5f;
            }

            float f = calcStrafe * calcStrafe + calcForward * calcForward;

            if (f >= 1.0E-4F) {
                f = MathHelper.sqrt_float(f);

                if (f < 1.0F) f = 1.0F;

                f = friction / f;
                calcStrafe *= f;
                calcForward *= f;

                final float yawSin = MathHelper.sin((float) (yaw * Math.PI / 180F));
                final float yawCos = MathHelper.cos((float) (yaw * Math.PI / 180F));

                mc.thePlayer.motionX += calcStrafe * yawCos - calcForward * yawSin;
                mc.thePlayer.motionZ += calcForward * yawCos + calcStrafe * yawSin;
            }
            event.setCancelled(true);
        }
    }

    /**
     * Disable scaffold module
     */
    @Override
    public void onDisable() {
        SafeWalkUtil.setSafe(false);
        if (mc.thePlayer == null) return;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false;

            if (eagleSneaking)
                mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
        }

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed = false;

        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false;

        lockRotation = null;
        mc.timer.timerSpeed = 1F;
        shouldGoDown = false;

        if (slot != mc.thePlayer.inventory.currentItem)
            mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventHandler
    public void onMove(final EventMove event) {
        if (SetMotion.get()) {
            if (MovementUtils.isMoving()) {
                if ((GroundOnly.get() && mc.thePlayer.onGround) || !GroundOnly.get())
                    MovementUtils.setMotion(event, MotionSpeed.get());
            }
        }
        if (!safeWalkValue.get() || shouldGoDown) return;

        if (airSafeValue.get() || mc.thePlayer.onGround) SafeWalkUtil.setSafe(true);
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventHandler
    public void onRender2D(EventRender2D event) {
        setSuffix(modeValue.get());
        renderBlockCount = counterDisplayValue.get();
        if (renderBlockCount) {
            final ScaledResolution scaledResolution = new ScaledResolution(mc);
            final String info = getBlockCount() + " blocks";
            int infoWidth = FontLoaders.SF20.getStringWidth(info);
            DrawUtil.roundedRect(ScaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, ScaledResolution.getScaledHeight() - 66.55F, (infoWidth) + 8, 16, 4, new Color(0, 0, 0, 100));
            FontLoaders.SF20.drawString(info, (float) (ScaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 0.4), ScaledResolution.getScaledHeight() - 62, Colors.WHITE.c);
        }
    }

    public int getBlockCount() {
        int n = 0;
        int i = 36;
        while (i < 45) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) {
                final ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                final Item item = stack.getItem();
                if (stack.getItem() instanceof ItemBlock && this.isValid(item)) {
                    n += stack.stackSize;
                }
            }
            ++i;
        }
        return n;
    }

    private boolean isValid(final Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock) item).getBlock());
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventHandler
    public void onRender3D(final EventRender3D event) {
        if (!markValue.get()) return;

        for (int i = 0; i < (modeValue.get().equals(ScaffoldMode.Expand) ? expandLengthValue.get() + 1 : 2); i++) {
            final BlockPos blockPos = new BlockPos(mc.thePlayer.posX + (mc.thePlayer.getHorizontalFacing() == EnumFacing.WEST ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.EAST ? i : 0), mc.thePlayer.posY - (mc.thePlayer.posY == (int) mc.thePlayer.posY + 0.5D ? 0D : 1.0D) - (shouldGoDown ? 1D : 0), mc.thePlayer.posZ + (mc.thePlayer.getHorizontalFacing() == EnumFacing.NORTH ? -i : mc.thePlayer.getHorizontalFacing() == EnumFacing.SOUTH ? i : 0));
            final PlaceInfo placeInfo = PlaceInfo.get(blockPos);

            if (BlockUtils.isReplaceable(blockPos) && placeInfo != null) {
                LiquidRender.drawBlockBox(blockPos, Color.CYAN, false);
                break;
            }
        }
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private boolean search(final BlockPos blockPosition, final boolean checks) {
        if (!BlockUtils.isReplaceable(blockPosition)) return false;

        final boolean staticYawMode = rotMode.get().equals(RotMode.AAC) || rotMode.get().equals(RotMode.Static);

        final Vec3 eyesPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.getEntityBoundingBox().minY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        PlaceRotation placeRotation = null;

        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbor = blockPosition.offset(side);

            if (!BlockUtils.canBeClicked(neighbor)) continue;

            final Vec3 dirVec = new Vec3(side.getDirectionVec());

            for (double xSearch = 0.1D; xSearch < 0.9D; xSearch += 0.1D) {
                for (double ySearch = 0.1D; ySearch < 0.9D; ySearch += 0.1D) {
                    for (double zSearch = 0.1D; zSearch < 0.9D; zSearch += 0.1D) {
                        final Vec3 posVec = new Vec3(blockPosition).addVector(xSearch, ySearch, zSearch);
                        final double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        final Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));

                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18D || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null))
                            continue;

                        // face block
                        for (int i = 0; i < (staticYawMode ? 2 : 1); i++) {
                            final double diffX = staticYawMode && i == 0 ? 0 : hitVec.xCoord - eyesPos.xCoord;
                            final double diffY = hitVec.yCoord - eyesPos.yCoord;
                            final double diffZ = staticYawMode && i == 1 ? 0 : hitVec.zCoord - eyesPos.zCoord;

                            final double diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);

                            Rotation rotation = new Rotation(MathHelper.wrapAngleTo180_float((float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F), MathHelper.wrapAngleTo180_float((float) -Math.toDegrees(Math.atan2(diffY, diffXZ))));
                            if (rotMode.get().equals(RotMode.Static)) {
                                rotation = new Rotation(getScaffoldRotation(mc.thePlayer.rotationYaw, mc.thePlayer.moveStrafing), staticPitchValue.get().floatValue());
                            }
                            final Vec3 rotationVector = new Vec3(RotationUtils.getVectorForRotation(rotation).getX(), RotationUtils.getVectorForRotation(rotation).getY(), RotationUtils.getVectorForRotation(rotation).getZ());
                            final Vec3 vector = eyesPos.addVector(rotationVector.xCoord * 4, rotationVector.yCoord * 4, rotationVector.zCoord * 4);
                            final MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);

                            if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.getBlockPos().equals(neighbor)))
                                continue;

                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(placeRotation.getRotation()))
                                placeRotation = new PlaceRotation(new PlaceInfo(neighbor, side.getOpposite(), hitVec), rotation);
                        }
                    }
                }
            }
        }

        if (placeRotation == null) return false;

        if (rotationsValue.get()) {
            RotationUtils.setTargetRotation(placeRotation.getRotation(), keepLengthValue.get().intValue());
            lockRotation = placeRotation.getRotation();
        }
        targetPlace = placeRotation.getPlaceInfo();
        return true;
    }

    enum ScaffoldMode {
        Normal, Rewinside, Expand
    }

    enum PlaceTiming {
        Pre, Post
    }

    enum ZitterMode {
        Teleport, Smooth
    }

    enum RotMode {
        Normal, AAC, Static
    }
}