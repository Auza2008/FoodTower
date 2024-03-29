package cn.foodtower.module.modules.world;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.World.EventPreUpdate;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.util.time.Timer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class ChestStealer extends Module {
    private final cn.foodtower.util.time.Timer timer = new cn.foodtower.util.time.Timer();
    private final cn.foodtower.util.time.Timer stealTimer = new Timer();
    private final Option instantexploit = new Option("InstantExploit", false);
    public Numbers<Double> delay = new Numbers<>("Delay", 150.0, 0.0, 300.0, 10.0);
    public Option close = new Option("Close", true);
    public Option ignore = new Option("Ignore", true);
    public Option hypixel = new Option("Hypixel", true);
    private boolean isStealing;

    public ChestStealer() {
        super("ChestStealer", new String[]{"cheststealer"}, ModuleType.World);
        addValues(delay, close, ignore, hypixel, instantexploit);
    }

    @EventHandler
    public void onUpdate(EventPreUpdate event) {
        if (instantexploit.get()) {
            if (mc.currentScreen instanceof GuiChest) {
                GuiChest chest = (GuiChest) mc.currentScreen;
                int rows = chest.inventoryRows * 9;
                for (int i = 0; i < rows; ++i) {
                    Slot slot = chest.inventorySlots.getSlot(i);
                    if (slot.getHasStack()) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0EPacketClickWindow(chest.inventorySlots.windowId, i, 0, 1, slot.getStack(), (short) 1));
                    }
                }
                mc.thePlayer.closeScreen();
            }
        } else if (mc.currentScreen instanceof GuiChest) {
            String[] list;
            GuiChest guiChest = (GuiChest) mc.currentScreen;
            String name = guiChest.lowerChestInventory.getDisplayName().getUnformattedText().toLowerCase();
            for (String str : list = new String[]{"menu", "selector", "game", "gui", "bypass", "inventory", "play", "teleporter", "shop", "melee", "armor", "block", "castle", "mini", "warp", "teleport", "user", "team", "tool", "sure", "trade", "cancel", "accept", "soul", "book", "recipe", "profile", "tele", "port", "map", "kit", "select", "lobby", "vault", "lock"}) {
                if (!name.contains(str)) continue;
                return;
            }
            this.isStealing = true;
            boolean full = true;
            for (ItemStack item : mc.thePlayer.inventory.mainInventory) {
                if (item != null) continue;
                full = false;
                break;
            }
            boolean containsItems = false;
            if (!full) {
                ItemStack stack;
                int index;
                for (index = 0; index < guiChest.lowerChestInventory.getSizeInventory(); ++index) {
                    stack = guiChest.lowerChestInventory.getStackInSlot(index);
                    if (stack == null || this.isBad(stack)) continue;
                    containsItems = true;
                    break;
                }
                if (containsItems) {
                    for (index = 0; index < guiChest.lowerChestInventory.getSizeInventory(); ++index) {
                        stack = guiChest.lowerChestInventory.getStackInSlot(index);
                        if (stack == null || !this.timer.delay(this.delay.get().intValue()) || this.isBad(stack))
                            continue;
                        mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 0, 1, mc.thePlayer);
                        if (this.hypixel.get().booleanValue()) {
                            mc.playerController.windowClick(guiChest.inventorySlots.windowId, index, 1, 1, mc.thePlayer);
                        }
                        this.timer.reset();
                    }
                } else if (this.close.get().booleanValue()) {
                    mc.thePlayer.closeScreen();
                    this.isStealing = false;
                }
            } else if (this.close.get().booleanValue()) {
                mc.thePlayer.closeScreen();
                this.isStealing = false;
            }
        } else {
            this.isStealing = false;
        }
    }

    private EnumFacing getFacingDirection(BlockPos pos) {
        MovingObjectPosition rayResult;
        EnumFacing direction = null;
        if (!mc.theWorld.getBlockState(pos.add(0, 1, 0)).getBlock().isBlockNormalCube()) {
            direction = EnumFacing.UP;
        }
        if ((rayResult = mc.theWorld.rayTraceBlocks(new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ), new Vec3((double) pos.getX() + 0.5, pos.getY(), (double) pos.getZ() + 0.5))) != null) {
            return rayResult.sideHit;
        }
        return direction;
    }

    private boolean isBad(ItemStack item) {
        if (!this.ignore.get()) {
            return false;
        }
        ItemStack is = null;
        float lastDamage = -1.0f;
        for (int i = 9; i < 45; ++i) {
            ItemStack is1;
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || !((is1 = mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemSword) || !(item.getItem() instanceof ItemSword) || lastDamage >= this.getDamage(is1))
                continue;
            lastDamage = this.getDamage(is1);
            is = is1;
        }
        if (is != null && is.getItem() instanceof ItemSword && item.getItem() instanceof ItemSword) {
            float currentDamage = this.getDamage(is);
            float itemDamage = this.getDamage(item);
            if (itemDamage > currentDamage) {
                return false;
            }
        }
        return item != null && (item.getItem().getUnlocalizedName().contains("tnt") || item.getItem().getUnlocalizedName().contains("stick") || item.getItem().getUnlocalizedName().contains("egg") && !item.getItem().getUnlocalizedName().contains("leg") || item.getItem().getUnlocalizedName().contains("string") || item.getItem().getUnlocalizedName().contains("flint") || item.getItem().getUnlocalizedName().contains("compass") || item.getItem().getUnlocalizedName().contains("feather") || item.getItem().getUnlocalizedName().contains("bucket") || item.getItem().getUnlocalizedName().contains("snow") || item.getItem().getUnlocalizedName().contains("fish") || item.getItem().getUnlocalizedName().contains("enchant") || item.getItem().getUnlocalizedName().contains("exp") || item.getItem().getUnlocalizedName().contains("shears") || item.getItem().getUnlocalizedName().contains("anvil") || item.getItem().getUnlocalizedName().contains("torch") || item.getItem().getUnlocalizedName().contains("seeds") || item.getItem().getUnlocalizedName().contains("leather") || item.getItem() instanceof ItemPickaxe || item.getItem() instanceof ItemGlassBottle || item.getItem().getUnlocalizedName().contains("piston") || item.getItem().getUnlocalizedName().contains("potion") && this.isBadPotion(item));
    }

    private boolean isBadPotion(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (PotionEffect o : potion.getEffects(stack)) {
                    PotionEffect effect = o;
                    if (effect.getPotionID() != Potion.poison.getId() && effect.getPotionID() != Potion.harm.getId() && effect.getPotionID() != Potion.moveSlowdown.getId() && effect.getPotionID() != Potion.weakness.getId())
                        continue;
                    return true;
                }
            }
        }
        return false;
    }

    private float getDamage(ItemStack stack) {
        if (!(stack.getItem() instanceof ItemSword)) {
            return 0.0f;
        }
        return (float) EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack) * 1.25f + ((ItemSword) stack.getItem()).getDamageVsEntity();
    }
}

