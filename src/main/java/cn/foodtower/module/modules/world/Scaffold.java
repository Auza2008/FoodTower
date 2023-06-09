package cn.foodtower.module.modules.world;

import cn.foodtower.Client;
import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.events.World.*;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.ui.font.FontLoaders;
import cn.foodtower.util.entity.MoveUtils;
import cn.foodtower.util.entity.PlayerUtil;
import cn.foodtower.util.math.PositionUtils;
import cn.foodtower.util.math.SmoothRotationObject;
import cn.foodtower.util.render.RenderUtil;
import cn.foodtower.util.time.TimerUtil;
import net.minecraft.block.*;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class Scaffold extends Module {
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.command_block, Blocks.crafting_table, Blocks.chest, Blocks.trapped_chest, Blocks.ender_chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.wooden_button, Blocks.stone_button, Blocks.lever, Blocks.noteblock, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.red_flower, Blocks.yellow_flower, Blocks.ladder, Blocks.web, Blocks.beacon);
    public static final Option sprint = new Option("Sprint", false);
    public static float saveYaw, savePitch;
    private final Option strafeValue = new Option("Strafe", false);
    private final Numbers<Double> customPitch = new Numbers<>("CustomPitch", 82.4, -90d, 90d, 0.1);
    private final Mode rotationMode = new Mode("RotationMode", RotMode.values(), RotMode.Normal);
    private final Mode towerMode = new Mode("TowerMode", TowMode.values(), TowMode.Vanilla);
    private final Mode itemMode = new Mode("ItemMode", ItemMode.values(), ItemMode.Spoof);
    private final Mode placeTiming = new Mode("PlaceTiming", "placetiming", PlaceMode.values(), PlaceMode.Post);
    private final Mode eagleMode = new Mode("EagleMode", "eaglemode", EagleMode.values(), EagleMode.Key);
    private final Numbers<Double> delayValue = new Numbers<Double>("Delay", "delay", 0.0, 0.0, 2000.0, 1.0);
//    private final Numbers<Double> expand = new Numbers<Double>("Expand", "expand", 0.0, 0.0, 10.0, 0.1);
    private final Numbers<Double> rotationSpeed = new Numbers<Double>("RotationSpeed", "rotationspeed", 180.0, 0.0, 180.0, 1.0);
    private final Option slowMove = new Option("SlowMove", "slowmode", false);
    private final Numbers<Double> slowMoveValue = new Numbers<Double>("SlowMoveSoeed", "slowmovesoeed", 0.2, 0.0, 0.3, 0.01);
    private final Option tower = new Option("Tower", "tower", true);
    private final Option moveTower = new Option("MoveTower", "movetower", true);
    private final Option eagle = new Option("Eagle", "eagle", false);
    private final Option down = new Option("Down", "down", false);
    private final Option swing = new Option("Swing", "swing", true);
    private final Option keepY = new Option("KeepY", "keepy", false);
    private final Option swap = new Option("Swap", "swap", true);
    private final TimerUtil delayTimerUtils = new TimerUtil();
    private final SmoothRotationObject rotationObject = new SmoothRotationObject();
    private BlockData blockData;
    private int startSlot;
    private double startY;

    public Scaffold() {
        super("Scaffold", new String[]{"magiccarpet", "blockplacer", "airwalk"}, ModuleType.World);
        addValues(rotationMode, customPitch, towerMode, itemMode, placeTiming, sprint, delayValue, rotationSpeed, tower, moveTower, slowMove, slowMoveValue, eagle, eagleMode, strafeValue, down, swing, keepY, swap);
        setValueDisplayable(customPitch, rotationMode, RotMode.MoveDirection);
    }

    public static int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            spoofSlot = i - 36;
            break;
        }
        return spoofSlot;
    }

    @EventHandler
    public void onTick(EventTick e) {
        if (!sprint.get()) mc.thePlayer.setSprinting(false);

        if (placeTiming.get().toString().equals("Tick")) {
            place();
        }
    }

    @EventHandler
    private void onRender(EventRender3D e) {
        Color color = new Color(Color.BLACK.getRGB());
        Color color2 = new Color(Color.ORANGE.getRGB());
        double x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
        double y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
        double z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
        double x2 = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * mc.timer.renderPartialTicks - RenderManager.renderPosX;
        double y2 = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * mc.timer.renderPartialTicks - RenderManager.renderPosY;
        double z2 = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * mc.timer.renderPartialTicks - RenderManager.renderPosZ;
        x -= 0.65;
        z -= 0.65;
        x2 -= 0.5;
        z2 -= 0.5;
        y += mc.thePlayer.getEyeHeight() + 0.35 - (mc.thePlayer.isSneaking() ? 0.25 : 0.0);
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        final double rotAdd = -0.25 * (Math.abs(mc.thePlayer.rotationPitch) / 90.0f);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 1.0f);
        GL11.glLineWidth(2.0f);
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y - 2, z, x + 1.3, y - 2, z + 1.3));
        RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x2, y - 2, z2, x2 + 1, y - 2, z2 + 1));
        if (mc.gameSettings.keyBindJump.pressed || ModuleManager.getModuleByName("Speed").isEnabled() && mc.thePlayer.moveForward != 0) {
            GL11.glColor4f(color2.getRed() / 255.0f, color2.getGreen() / 255.0f, color2.getBlue() / 255.0f, 1.0f);
            RenderUtil.drawOutlinedBoundingBox(new AxisAlignedBB(x, y - 2, z, x + 1.3, y - 2, z + 1.3));
        }
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    @EventHandler
    public void onPreUpdate(EventPreUpdate e) {
        blockData = null;
        /**
         * 此处为开启模块转头
         */
        if (Objects.requireNonNull((RotMode) rotationMode.get()) == RotMode.None) {
            savePitch = mc.thePlayer.rotationPitch;
            saveYaw = mc.thePlayer.rotationYaw;
        } else if (rotationMode.get() == RotMode.MoveDirection) {
            saveYaw = this.movingYaw() - 180;
            savePitch = customPitch.get().floatValue();
        }
        if (rotationSpeed.get().equals(180.0)) {
            e.setYaw(saveYaw);
            e.setPitch(savePitch);
            Client.RenderRotate(saveYaw, savePitch);
        } else {
            rotationObject.setWillYawPitch(saveYaw, savePitch);
            rotationObject.handleRotation(rotationSpeed.get());
            rotationObject.setPlayerRotation(e);
        }

        /**
         * 此处为放置后恢复转头
         */
//        ****
//        ****

        double x = mc.thePlayer.posX;
        double z = mc.thePlayer.posZ;
        double y = mc.thePlayer.posY;

        double yOffset = 1;

        final boolean shouldDown = down.get() && Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);

        if (shouldDown) {
            yOffset = 2;
        }

        if (keepY.get()) {
            if (MoveUtils.isMoving()) {
                y = startY;
            } else {
                startY = mc.thePlayer.posY;
            }
        }

//        if (!(expand.get() == 0) && !mc.thePlayer.isCollidedHorizontally && !shouldDown) {
//            MovementInput movementInput = new MovementInput();
//            double[] coords = getExpandCoords(x, z, movementInput.moveForward, movementInput.moveStrafe, mc.thePlayer.rotationYaw);
//            x = coords[0];
//            z = coords[1];
//        }

        if (isAirBlock(mc.theWorld.getBlockState(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - yOffset, mc.thePlayer.posZ)).getBlock())) {
            x = mc.thePlayer.posX;
            z = mc.thePlayer.posZ;
        }

        final boolean hasBlock = getInventoryBlockSize() > 0;

        if (hasBlock) {
            BlockPos underPos = new BlockPos(x, y - yOffset, z);
            Block underBlock = PlayerUtil.getBlock(underPos);

            final BlockData data = getBlockData(underPos, shouldDown);

            if (isAirBlock(underBlock) && data != null) {
                if (eagle.get() && eagleMode.get().toString().equals("Key")) {
                    mc.thePlayer.movementInput.sneak = true;
                    mc.thePlayer.setSprinting(false);
                    MoveUtils.strafe(0);
                }

                blockData = data;
                final float[] rotations = getRotationsBlock(data.position, data.face);
                /**
                 * 此处为放置方块时转头(改变转头)
                 */
                if (Objects.requireNonNull((RotMode) rotationMode.get()) == RotMode.Normal) {
                    float yaw = rotations[0];
                    float pitch = rotations[1];
                    saveYaw = yaw;
                    savePitch = pitch;
                }
            }
        }

        label:
        {
            if (hasBlock && mc.gameSettings.keyBindJump.isKeyDown() && (tower.get() || moveTower.get())) {
                if (tower.get()) {
                    if (!moveTower.get()) if (MoveUtils.isMoving()) break label;
                } else if (moveTower.get() && !MoveUtils.isMoving()) break label;

                if (!MoveUtils.isMoving()) {
                    MoveUtils.strafe(0);
                }

                if (towerMode.get().toString().equals("Hypixel") || (moveTower.get() && MoveUtils.isMoving())) {
                    if (mc.thePlayer.onGround) {
                        int posX0 = (int) mc.thePlayer.posX;
                        if (mc.thePlayer.posX < posX0) {
                            posX0 -= 1;
                        }

                        int posZ0 = (int) mc.thePlayer.posZ;
                        if (mc.thePlayer.posZ < posZ0) {
                            posZ0 -= 1;
                        }
                        mc.thePlayer.setPosition(posX0 + 0.5, mc.thePlayer.posY, posZ0 + 0.5);
                    }

                    if (MoveUtils.isOnGround(0.76D) && !MoveUtils.isOnGround(0.75D) && mc.thePlayer.motionY > 0.23D && mc.thePlayer.motionY < 0.25D) {
                        mc.thePlayer.motionY = ((double) Math.round(mc.thePlayer.posY) - mc.thePlayer.posY);
                    }

                    if (MoveUtils.isOnGround(1.0E-4D)) {
                        mc.thePlayer.motionY = 0.41999998688698;
                    } else if (mc.thePlayer.posY >= (double) Math.round(mc.thePlayer.posY) - 1.0E-4D && mc.thePlayer.posY <= (double) Math.round(mc.thePlayer.posY) + 1.0E-4D) {
                        mc.thePlayer.motionY = 0;
                    }

                    if (towerMode.get().toString().equals("Hypixel") && !MoveUtils.isMoving()) {
                        if (PlayerUtil.getBlock(new BlockPos(mc.thePlayer).add(0, 2, 0)) instanceof BlockAir) {
                            double var3 = e.getY() % 1.0D;
                            double var4 = MathHelper.floor_double(mc.thePlayer.posY);
                            double[] offsets = new double[]{0.41999998688698, 0.7531999805212};
                            if (var3 > 0.419D && var3 < 0.753D) {
                                e.setY(var4 + offsets[0]);
                            } else if (var3 > 0.753D) {
                                e.setY(var4 + offsets[1]);
                            } else {
                                e.setY(var4);
                            }

                            e.setX(e.getX() + (mc.thePlayer.ticksExisted % 2 == 0 ? ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D) : -ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D)));
                            e.setZ(e.getZ() + (mc.thePlayer.ticksExisted % 2 != 0 ? ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D) : -ThreadLocalRandom.current().nextDouble(0.06D, 0.0625D)));
                        }
                    }
                } else if (towerMode.get().toString().equals("Vanilla")) {
                    mc.thePlayer.motionY = 0.41982;
                    MoveUtils.strafe(0);
                } else if (towerMode.get().toString().equals("TP")) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1, mc.thePlayer.posZ);

                    if (!MoveUtils.isOnGround(1)) {
                        mc.thePlayer.motionY = 0;
                    }

                    MoveUtils.strafe(0);
                } else if (towerMode.get().toString().equals("Low")) {
                    if (MoveUtils.isOnGround(0.99)) {
                        mc.thePlayer.motionY = 0.36;
                        MoveUtils.strafe(0);
                    }
                } else if (towerMode.get().toString().equals("Slow")) {
                    if (MoveUtils.isOnGround(0.114514)) {
                        mc.thePlayer.jump();
                        MoveUtils.strafe(0);
                    }
                }
            }
        }

        if (placeTiming.get().toString().equals("Pre")) {
            place();
        }
    }

    @EventHandler
    public void onMove(EventMove e) {
        if (slowMove.get()) {
            MoveUtils.setSpeed(e, slowMoveValue.get());
        }
    }

    @EventHandler
    public void onPostUpdate(EventPostUpdate e) {
        if (placeTiming.get().toString().equals("Post")) {
            place();
        }
    }

    private void place() {
        if (getInventoryBlockSize() > 0 && blockData != null) {
            if (swap.get()) {
                getBlock(getBestSpoofSlot());
            }

            if (delayValue.get().intValue() == 0 || delayTimerUtils.hasReached(delayValue.get())) {
                final int slot = getBlockFromHotBar();
                if (slot != -1) {
                    final boolean sendSneakPacket = eagle.get() && eagleMode.get().toString().equals("Packet");

                    if (sendSneakPacket) {
                        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SNEAKING));
                    }

                    final int old = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = slot;

                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), blockData.position, blockData.face, PositionUtils.getVec3(blockData.position, blockData.face))) {
                        if (swing.get()) {
                            mc.thePlayer.swingItem();
                        } else mc.getNetHandler().addToSendQueue(new C0APacketAnimation());
                    }

                    if (itemMode.get().toString().equals("Spoof")) {
                        mc.thePlayer.inventory.currentItem = old;
                    }

                    if (sendSneakPacket) {
                        mc.getNetHandler().addToSendQueue(new C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING));
                    }
                }
            }
        }
    }

    @EventHandler
    public void on2D(EventRender2D e) {
        final int width = ScaledResolution.getScaledWidth();
        final int height = ScaledResolution.getScaledHeight();
        final int middleX = width / 2 + 15;
        final int middleY = height / 2 - 12;
        final int block = getInventoryBlockSize();
        if (block != 0) {
            final ItemStack stackInSlot = mc.thePlayer.inventory.getStackInSlot(getBlockFromHotBar());
            final String displayName;
            if (stackInSlot == null) {
                displayName = "NULL";
            } else displayName = stackInSlot.getDisplayName();
            FontLoaders.GoogleSans16.drawStringWithShadow((block >= 64 ? EnumChatFormatting.YELLOW : EnumChatFormatting.RED) + "Blocks:" + block + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GRAY + displayName + EnumChatFormatting.WHITE + ")", middleX - 50, middleY + 20, -1);
        } else {
            FontLoaders.GoogleSans16.drawStringWithShadow(EnumChatFormatting.RED + "Blocks:" + block + EnumChatFormatting.WHITE + " (" + EnumChatFormatting.GRAY + "NULL" + EnumChatFormatting.WHITE + ")", middleX - 50, middleY + 20, -1);
        }
    }

    @Override
    public void onEnable() {
        if (itemMode.get().toString().equals("Switch")) {
            startSlot = mc.thePlayer.inventory.currentItem;
        }
        startY = mc.thePlayer.posY;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (itemMode.get().toString().equals("Switch")) {
            mc.thePlayer.inventory.currentItem = startSlot;
        }
        blockData = null;
        super.onDisable();
    }

    private float[] getRotationsBlock(BlockPos block, EnumFacing face) {
        double x = (double) block.getX() + 0.5 - mc.thePlayer.posX + (double) face.getFrontOffsetX() / 2.0;
        double z = (double) block.getZ() + 0.5 - mc.thePlayer.posZ + (double) face.getFrontOffsetZ() / 2.0;
        double y = (double) block.getY() + 0.5;
        double d1 = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - y;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float) (Math.atan2(d1, d3) * 180.0 / Math.PI);
        if (yaw < 0.0f) {
            yaw += 360.0f;
        }
        return new float[]{yaw, pitch};
    }

    private int getBlockFromHotBar() {
        int slot = -1;

        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = mc.thePlayer.inventory.getStackInSlot(i);
            if (itemStack == null) continue;
            if (!isValidItem(itemStack.getItem())) continue;
            slot = i;
            break;
        }

        return slot;
    }

    private void getBlock(int hotBarSlot) {
        if (getBlockFromHotBar() == -1) {
            for (int i = 0; i < 36; ++i) {
                if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory))
                    continue;
                ItemStack is = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                if (!(is.getItem() instanceof ItemBlock) || !isValidItem(is.getItem())) continue;
                if (36 + hotBarSlot == i) break;

                swap(i, hotBarSlot);

                break;
            }
        }
    }

    private int getInventoryBlockSize() {
        int final_ = 0;

        for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
            if (stack == null) continue;
            if (!(stack.getItem() instanceof ItemBlock) || !isValidItem(stack.getItem())) continue;
            final_ += stack.stackSize;
        }

        return final_;
    }

//    public double[] getExpandCoords(double x, double z, double forward, double strafe, float YAW) {
//        BlockPos underPos = new BlockPos(x, mc.thePlayer.posY - 1, z);
//        Block underBlock = mc.theWorld.getBlockState(underPos).getBlock();
//        double xCalc = -999, zCalc = -999;
//        double dist = 0;
//        double expandDist = expand.get() * 2;
//        while (!isAirBlock(underBlock)) {
//            xCalc = x;
//            zCalc = z;
//            dist++;
//            if (dist > expandDist) {
//                dist = expandDist;
//            }
//            final double cos = Math.cos(Math.toRadians(YAW + 90.0f));
//            final double sin = Math.sin(Math.toRadians(YAW + 90.0f));
//            xCalc += (forward * 0.45 * cos + strafe * 0.45 * sin) * dist;
//            zCalc += (forward * 0.45 * sin - strafe * 0.45 * cos) * dist;
//            if (dist == expandDist) {
//                break;
//            }
//            underPos = new BlockPos(xCalc, mc.thePlayer.posY - 1, zCalc);
//            underBlock = mc.theWorld.getBlockState(underPos).getBlock();
//        }
//        return new double[]{xCalc, zCalc};
//    }

    private boolean isAirBlock(Block block) {
        if (block.getMaterial().isReplaceable()) {
            return !(block instanceof BlockSnow);
        }

        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean isValidItem(Item item) {
        if (item instanceof ItemBlock) {
            ItemBlock iBlock = (ItemBlock) item;
            Block block = iBlock.getBlock();
            return !invalidBlocks.contains(block);
        }
        return false;
    }

    public void swap(int slot1, int hotbarSlot) {
        mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, slot1, hotbarSlot, 2, mc.thePlayer);
    }

    private BlockData getBlockData(BlockPos pos, boolean down) {
        if (down) {
            if (isPosSolid(pos.add(0, 1, 0))) {
                return new BlockData(pos.add(0, 1, 0), EnumFacing.DOWN);
            }
        }

        if (isPosSolid(pos.add(0, -1, 0))) {
            return new BlockData(pos.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos.add(-1, 0, 0))) {
            return new BlockData(pos.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos.add(1, 0, 0))) {
            return new BlockData(pos.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos.add(0, 0, 1))) {
            return new BlockData(pos.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos.add(0, 0, -1))) {
            return new BlockData(pos.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos1 = pos.add(-1, 0, 0);
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos2 = pos.add(1, 0, 0);
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos3 = pos.add(0, 0, 1);
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos4 = pos.add(0, 0, -1);
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos1.add(0, -1, 0))) {
            return new BlockData(pos1.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos1.add(-1, 0, 0))) {
            return new BlockData(pos1.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos1.add(1, 0, 0))) {
            return new BlockData(pos1.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos1.add(0, 0, 1))) {
            return new BlockData(pos1.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos1.add(0, 0, -1))) {
            return new BlockData(pos1.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos2.add(0, -1, 0))) {
            return new BlockData(pos2.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos2.add(-1, 0, 0))) {
            return new BlockData(pos2.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos2.add(1, 0, 0))) {
            return new BlockData(pos2.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos2.add(0, 0, 1))) {
            return new BlockData(pos2.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos2.add(0, 0, -1))) {
            return new BlockData(pos2.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos3.add(0, -1, 0))) {
            return new BlockData(pos3.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos3.add(-1, 0, 0))) {
            return new BlockData(pos3.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos3.add(1, 0, 0))) {
            return new BlockData(pos3.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos3.add(0, 0, 1))) {
            return new BlockData(pos3.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos3.add(0, 0, -1))) {
            return new BlockData(pos3.add(0, 0, -1), EnumFacing.SOUTH);
        }
        if (isPosSolid(pos4.add(0, -1, 0))) {
            return new BlockData(pos4.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos4.add(-1, 0, 0))) {
            return new BlockData(pos4.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos4.add(1, 0, 0))) {
            return new BlockData(pos4.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos4.add(0, 0, 1))) {
            return new BlockData(pos4.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos4.add(0, 0, -1))) {
            return new BlockData(pos4.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos5 = pos.add(0, -1, 0);
        if (isPosSolid(pos5.add(0, -1, 0))) {
            return new BlockData(pos5.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos5.add(-1, 0, 0))) {
            return new BlockData(pos5.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos5.add(1, 0, 0))) {
            return new BlockData(pos5.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos5.add(0, 0, 1))) {
            return new BlockData(pos5.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos5.add(0, 0, -1))) {
            return new BlockData(pos5.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos6 = pos5.add(1, 0, 0);
        if (isPosSolid(pos6.add(0, -1, 0))) {
            return new BlockData(pos6.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos6.add(-1, 0, 0))) {
            return new BlockData(pos6.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos6.add(1, 0, 0))) {
            return new BlockData(pos6.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos6.add(0, 0, 1))) {
            return new BlockData(pos6.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos6.add(0, 0, -1))) {
            return new BlockData(pos6.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos7 = pos5.add(-1, 0, 0);
        if (isPosSolid(pos7.add(0, -1, 0))) {
            return new BlockData(pos7.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos7.add(-1, 0, 0))) {
            return new BlockData(pos7.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos7.add(1, 0, 0))) {
            return new BlockData(pos7.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos7.add(0, 0, 1))) {
            return new BlockData(pos7.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos7.add(0, 0, -1))) {
            return new BlockData(pos7.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos8 = pos5.add(0, 0, 1);
        if (isPosSolid(pos8.add(0, -1, 0))) {
            return new BlockData(pos8.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos8.add(-1, 0, 0))) {
            return new BlockData(pos8.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos8.add(1, 0, 0))) {
            return new BlockData(pos8.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos8.add(0, 0, 1))) {
            return new BlockData(pos8.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos8.add(0, 0, -1))) {
            return new BlockData(pos8.add(0, 0, -1), EnumFacing.SOUTH);
        }
        BlockPos pos9 = pos5.add(0, 0, -1);
        if (isPosSolid(pos9.add(0, -1, 0))) {
            return new BlockData(pos9.add(0, -1, 0), EnumFacing.UP);
        }
        if (isPosSolid(pos9.add(-1, 0, 0))) {
            return new BlockData(pos9.add(-1, 0, 0), EnumFacing.EAST);
        }
        if (isPosSolid(pos9.add(1, 0, 0))) {
            return new BlockData(pos9.add(1, 0, 0), EnumFacing.WEST);
        }
        if (isPosSolid(pos9.add(0, 0, 1))) {
            return new BlockData(pos9.add(0, 0, 1), EnumFacing.NORTH);
        }
        if (isPosSolid(pos9.add(0, 0, -1))) {
            return new BlockData(pos9.add(0, 0, -1), EnumFacing.SOUTH);
        }
        return null;
    }

    @EventHandler
    private void onStrafe(EventStrafe event) {
        if (strafeValue.get()) {
            final int dif = (int) ((MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - saveYaw - 23.5F - 135) + 180) / 45);

            final float yaw = saveYaw;
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

    private boolean isPosSolid(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        return (block.getMaterial().isSolid() || !block.isTranslucent() || (block.getMaterial().blocksMovement() && block.isFullCube()) || block instanceof BlockLadder || block instanceof BlockCarpet || block instanceof BlockSnow || block instanceof BlockSkull) && !block.getMaterial().isLiquid() && !(block instanceof BlockContainer);
    }

    private double direction() {
        EntityPlayerSP thePlayer = mc.thePlayer;
        float rotationYaw = thePlayer.rotationYaw;
        if (thePlayer.moveForward < 0f) rotationYaw += 180f;
        float forward = 1f;
        if (thePlayer.moveForward < 0f) forward = -0.5f;
        else if (thePlayer.moveForward > 0f) forward = 0.5f;
        if (thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward;
        if (thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward;
        return Math.toRadians(rotationYaw);
    }

    private float movingYaw() {
        return (float) (this.direction() * 180f / Math.PI);
    }

    enum RotMode {
        Normal, MoveDirection, None
    }

    enum TowMode {
        Vanilla, Hypixel, TP, Low, Slow
    }

    enum ItemMode {
        Spoof, Switch
    }

    enum PlaceMode {
        Pre, Post, Tick
    }

    enum EagleMode {
        Key, Packet
    }

    private static class BlockData {
        private final BlockPos position;
        private final EnumFacing face;

        private BlockData(BlockPos position, EnumFacing face) {
            this.position = position;
            this.face = face;
        }
    }
}

