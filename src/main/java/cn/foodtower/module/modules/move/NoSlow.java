package cn.foodtower.module.modules.move;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.World.EventMotion;
import cn.foodtower.api.events.World.EventMotionUpdate;
import cn.foodtower.api.events.World.EventNoSlow;
import cn.foodtower.api.events.World.EventPacket;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.entity.MovementUtils;
import cn.foodtower.util.time.MSTimer;
import cn.foodtower.util.world.PacketUtil;
import net.minecraft.item.*;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import java.util.concurrent.CopyOnWriteArrayList;

public class NoSlow extends Module {
    private final MSTimer timer = new MSTimer();
    private final Option customOnGround = new Option("CustomOnGround", false);
    private final Numbers<Double> customDelayValue = new Numbers<>("CustomDelay", 60d, 10d, 200d, 1d);
    private final Mode modeValue = new Mode("PacketMode", NoSlowMode.values(), NoSlowMode.Vanilla);
    // Soulsand
    private final Option soulsandValue = new Option("Soulsand", true);
    private final Option consume = new Option("Consume", true);
    private final MSTimer msTimer = new MSTimer();
    private final CopyOnWriteArrayList<Packet<?>> packetBuf = new CopyOnWriteArrayList<>();
    private boolean nextTemp = false;
    private boolean lastBlockingStat = false;
    private boolean waitC03 = false;
    private boolean released = true;

    public NoSlow() {
        super("NoSlow", new String[]{"NoSlowDown"}, ModuleType.Movement);
        this.addValues(modeValue, consume, soulsandValue, customOnGround, customDelayValue);
    }

    private boolean isBlocking() {
        return (mc.thePlayer.isUsingItem() || mc.thePlayer.isBlocking()) && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword;
    }

    @Override
    public void onDisable() {
        timer.reset();
        msTimer.reset();
        packetBuf.clear();
    }

    @Override
    public void onEnable() {
        timer.reset();
        msTimer.reset();
        packetBuf.clear();
    }

    @EventHandler
    public void onRender2D(EventRender2D e) {
        setSuffix(modeValue.get());
    }

    @EventHandler
    public void onPacket(EventPacket event) {
        if (mc.thePlayer == null || mc.theWorld == null) {
            return;
        }
        Packet<?> packet = event.getPacket();
        if ((modeValue.get() == NoSlowMode.Vulcan) && nextTemp) {
            if ((packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement) && isBlocking()) {
                event.setCancelled(true);
            } else if (packet instanceof C03PacketPlayer || packet instanceof C0APacketAnimation || packet instanceof C0BPacketEntityAction || packet instanceof C07PacketPlayerDigging || packet instanceof C08PacketPlayerBlockPlacement) {
                if (modeValue.get() == NoSlowMode.Vulcan && waitC03 && packet instanceof C03PacketPlayer) {
                    waitC03 = false;
                    return;
                }
                packetBuf.add(packet);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onUpdate(EventMotionUpdate e) {
        if ((modeValue.get().equals(NoSlowMode.Vulcan) && (lastBlockingStat || isBlocking()))) {//有一种脑干缺失的美
            if (msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false;
                PacketUtil.sendPacketNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN));
                if (!packetBuf.isEmpty()) {
                    boolean canAttack = false;
                    for (Packet<?> packet : packetBuf) {
                        if (packet instanceof C03PacketPlayer) {
                            canAttack = true;
                        }
                        if (!((packet instanceof C02PacketUseEntity || packet instanceof C0APacketAnimation) && !canAttack)) {
                            PacketUtil.sendPacketNoEvent(packet);
                        }
                    }
                    packetBuf.clear();

                }
            }
            if (!nextTemp) {
                lastBlockingStat = isBlocking();
                if (!isBlocking()) {
                    return;
                }
                PacketUtil.sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                nextTemp = true;
                waitC03 = modeValue.get() == NoSlowMode.Vulcan;
                msTimer.reset();
            }
        }
    }

    @EventHandler
    public void onEventMotion(EventMotion event) {
        if (!MovementUtils.isMoving()) {
            released = false;//hypixel
            return;
        }
        if (!isBlocking() && !isUsingFood()) {
            released = false;//hypixel
            return;
        }
        switch ((NoSlowMode) modeValue.get()) {
            case LiquidBounce:
                sendPacket(event, true, true, false, 0, false, false);
                break;
            case AAC: {
                if (mc.thePlayer.ticksExisted % 3 == 0) {
                    sendPacket(event, true, false, false, 0, false, false);
                } else if (mc.thePlayer.ticksExisted % 3 == 1) {
                    sendPacket(event, false, true, false, 0, false, false);
                }
                break;
            }
            case AAC5:
                if (!event.isPre()) {
                    mc.thePlayer.sendQueue.getNetworkManager().sendPacketNoEvent(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f));
                }
                break;
            case Custom: {
                sendPacket(event, true, true, true, customDelayValue.get().longValue(), customOnGround.get(), false);
                break;
            }
            case NCP: {
                sendPacket(event, true, true, false, 0, false, false);
                break;
            }
            case NewNCP: {
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    sendPacket(event, true, false, false, 50, false, true);
                } else {
                    sendPacket(event, false, true, false, 0, true, true);
                }
                break;
            }
            case Hypixel:
                int st = 8;
                for (int i = 0; i < 8; ++i) {
                    if (mc.thePlayer.inventory.getStackInSlot(i) == null) {
                        st = i;
                    }
                }
                if (mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
                    if (!released) {
                        sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                        released = true;
                    }
                    return;
                } else {
                    released = false;
                }
                sendPacket(new C09PacketHeldItemChange(st));
                sendPacket(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, BlockPos.ORIGIN, EnumFacing.UP));
                sendPacket(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
                break;
        }
    }

    public boolean isUsingFood() {
        if (!consume.get()) return false;
        if (mc.thePlayer.getItemInUse() == null) return false;
        Item usingItem = mc.thePlayer.getItemInUse().getItem();
        return mc.thePlayer.isUsingItem() && (usingItem instanceof ItemFood || usingItem instanceof ItemBucketMilk || usingItem instanceof ItemPotion);
    }

    @EventHandler
    public void onSlowDown(EventNoSlow event) {
        Item heldItem = mc.thePlayer.getHeldItem().getItem();
        event.setMoveForward(getMultiplier(heldItem));
        event.setMoveStrafe(getMultiplier(heldItem));
    }

    private void sendPacket(EventMotion event, boolean sendC07, boolean sendC08, boolean delay, long delayValue, boolean onGround, boolean watchDog) {
        C07PacketPlayerDigging digging = new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(-1, -1, -1), EnumFacing.DOWN);
        C08PacketPlayerBlockPlacement blockPlace = new C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem());
        C08PacketPlayerBlockPlacement blockMent = new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0f, 0f, 0f);
        if (onGround && !mc.thePlayer.onGround) {
            return;
        }
        if (sendC07 && event.getTypes().equals(EventMotion.Type.PRE)) {
            if (delay && timer.hasTimePassed(delayValue)) {
                mc.getNetHandler().addToSendQueue(digging);
            } else if (!delay) {
                mc.getNetHandler().addToSendQueue(digging);
            }
        }
        if (sendC08 && event.getTypes().equals(EventMotion.Type.POST)) {
            if (delay && timer.hasTimePassed(delayValue) && !watchDog) {
                mc.getNetHandler().addToSendQueue(blockPlace);
                timer.reset();
            } else if (!delay && !watchDog) {
                mc.getNetHandler().addToSendQueue(blockPlace);
            } else if (watchDog) {
                mc.getNetHandler().addToSendQueue(blockMent);
            }
        }
    }

//    private float getMultiplier(Item item, boolean isForward) {
//        if (consume.get() && item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemBucketMilk) {
//            return (isForward) ? this.consumeForwardMultiplier.get().floatValue() : this.consumeStrafeMultiplier.get().floatValue();
//        } else if (item instanceof ItemSword) {
//            return (isForward) ? this.blockForwardMultiplier.get().floatValue() : this.blockStrafeMultiplier.get().floatValue();
//
//        } else if (item instanceof ItemBow) {
//            return (isForward) ? this.bowForwardMultiplier.get().floatValue() : this.bowStrafeMultiplier.get().floatValue();
//        } else {
//            return 0.2F;
//        }
//    }

    //原先的设置太多太脑瘫，换成简洁的(反正1.0绕不过你其他的也差不多绕不过)
    private float getMultiplier(Item item) {
        if (consume.get() && item instanceof ItemFood || item instanceof ItemPotion || item instanceof ItemBucketMilk) {
            return 1f;
        } else if (item instanceof ItemSword) {
            return 1f;
        } else if (item instanceof ItemBow) {
            return 1f;
        } else {
            return 0.2f;
        }
    }


    enum NoSlowMode {
        Vanilla, NCP, NewNCP, Hypixel, AAC, AAC5, Vulcan, LiquidBounce, Custom
    }
}
