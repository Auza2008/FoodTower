/*
Author:SuMuGod
Date:2022/7/10 5:08
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.Client;
import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventRender2D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.module.modules.combat.Killaura;
import me.dev.foodtower.utils.math.MathUtils;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TargetHUD extends Module {
    static float hurtPercent = 0;
    public TargetHUD() {
        super("TargetHUD", "视界面而有之", new String[]{"TargetHud"}, ModuleType.Render);
    }

    static int colors = new Color(-1).getRGB();

    private float lastHealth = 0.0F;



    private EntityLivingBase lasttarget;
    public Mode mode = new Mode("Mode", "mode", TargetMode.values(), TargetMode.ETB);
    float anim = 140;

    public static ResourceLocation getskin(EntityLivingBase entity) {
        ResourceLocation var2;
        try {
            if (entity instanceof EntityPlayer) {
                NetworkPlayerInfo playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(entity.getUniqueID());
                var2 = playerInfo.getLocationSkin();
            } else {
                var2 = DefaultPlayerSkin.getDefaultSkinLegacy();
            }
        } catch (NullPointerException e) {
            var2 = DefaultPlayerSkin.getDefaultSkinLegacy();
            e.printStackTrace();
        }
        return var2;
    }

    public static void Target1(final double x, final double y, final float u, final float v, final int uWidth, final int vHeight, final int width, final int height, final float tileWidth, final float tileHeight, final AbstractClientPlayer target) {
        final ResourceLocation skin = target.getLocationSkin();
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GL11.glEnable(GL11.GL_BLEND);
        RenderUtil.drawScaledCustomSizeModalRect((int) x, (int) y, u, v, uWidth, vHeight, width, height, tileWidth, tileHeight);
        GL11.glDisable(GL11.GL_BLEND);
    }

    @NMSL
    public void onRender2D(EventRender2D event) {
        EntityLivingBase target = Killaura.target;
        if (this.mode.getValue() == TargetMode.ETB) {
            if (target == null || !(target instanceof EntityPlayer) || mc.theWorld.getEntityByID(target.getEntityId()) == null || mc.theWorld.getEntityByID(target.getEntityId()).getDistanceSqToEntity(mc.thePlayer) > 100) {
                return;
            }
            hurtPercent = target.hurtTime;
            GlStateManager.pushMatrix();
            // Width and height
            final float width = mc.displayWidth / (float) (mc.gameSettings.guiScale * 2) + 680;
            final float height = mc.displayHeight / (float) (mc.gameSettings.guiScale * 2) + 280;
            GlStateManager.translate(width - 660, height - 160.0f - 90.0f, 0.0f);
            // Border rect.
            RenderUtil.rectangle(2, -6, 156.0, 47.0, new Color(23, 23, 23).getRGB());
            // Main rect.
            // Draws name.
            mc.fontRendererObj.drawString(target.getName(), 46, (int) 0.3, -1);
            mc.fontRendererObj.drawString("Distance: " + mc.thePlayer.getDistanceToEntity(target) + "F", 46, 10, -1);
            mc.fontRendererObj.drawString("Health: " + (int) target.getHealth() + "/" + (int) target.getMaxHealth(), 46, 20, -1);
            // Gets health.
            final float health = target.getHealth();
            // Color stuff for the healthBar.
            final float[] fractions = new float[]{0.0F, 0.5F, 1.0F};
            final Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
            // Max health.
            final float progress = health / target.getMaxHealth();
            // Color.
            final Color healthColor = health >= 0.0f ? RenderUtil.blendColors(fractions, colors, progress).brighter() : Color.RED;
            NetworkPlayerInfo networkPlayerInfo = mc.getNetHandler().getPlayerInfo(target.getUniqueID());
            GlStateManager.pushMatrix();
            GlStateManager.popMatrix();
            // Draws the ping thingy from tab. :sunglasses:
            // Round.
            double cockWidth = 0.0;
            cockWidth = MathUtils.round(cockWidth, (int) 5.0);
            if (cockWidth < 50.0) {
                cockWidth = 50.0;
            }
            // Bar behind healthbar.
            RenderUtil.rectangle(6.5, 37.3, 151, 43, Color.RED.darker().darker().getRGB());
            final double healthBarPos = cockWidth * (double) progress;
            // health bar.
            if (anim < 150 * (target.getHealth() / target.getMaxHealth())) {
                anim = (int) (150 * (target.getHealth() / target.getMaxHealth()));
            } else if (anim > 150 * (target.getHealth() / target.getMaxHealth())) {
                anim -= 120f / mc.getDebugFPS();
            }
            RenderUtil.rect(6f, 37.3f, anim, 6f, new Color(target.hurtTime * 20, 0, 0));
            RenderUtil.rect(6f, 37.3f, (healthBarPos * 2.9), 6f, healthColor);
            // Gets the armor thingy for the bar.
            float armorValue = target.getTotalArmorValue();
            double armorWidth = armorValue / 20D;
            // Bar behind armor bar.
            RenderUtil.rect(45.5f, 32.3f, 105, 2.5f, new Color(0x00A6FF));
            // Armor bar.
            RenderUtil.rect(45.5f, 32.3f, (105 * armorWidth), 2.5f, new Color(0x00A6FF));
            // Draws head.
            Target1(7, -1, 3, 3, 3, 3, 35, 35, 24, 24, (AbstractClientPlayer) target);
            // Draws armor.
//            GlStateManager.scale(1.1, 1.1, 1.1);
//            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
//            GlStateManager.enableAlpha();
//            GlStateManager.enableBlend();
//            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
//            // Draw targets armor the worst way possible.
//            if (target != null) Target(24, 11); Target(44, 11); NoThing(64, 11); NMSL(84, 11);
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }
    private void Target(final int x, final int y) {
        Killaura ka = (Killaura) Client.instance.getModuleManager().getModuleByClass(Killaura.class);
        EntityLivingBase target = ka.target;
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack helmet = ((EntityPlayer) target).getCurrentArmor(3);
        if (helmet != null) {
            stuff.add(helmet);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    private void NoThing(final int x, final int y) {
        Killaura ka = (Killaura) Client.instance.getModuleManager().getModuleByClass(Killaura.class);
        EntityLivingBase target = ka.target;
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack legs = ((EntityPlayer) target).getCurrentArmor(1);
        if (legs != null) {
            stuff.add(legs);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    private void NMSL(final int x, final int y) {
        Killaura ka = (Killaura) Client.instance.getModuleManager().getModuleByClass(Killaura.class);
        EntityLivingBase target = ka.target;
        if (target == null || !(target instanceof EntityPlayer)) return;
        GL11.glPushMatrix();
        final List<ItemStack> stuff = new ArrayList<>();
        int cock = -2;
        final ItemStack boots = ((EntityPlayer) target).getCurrentArmor(0);
        if (boots != null) {
            stuff.add(boots);
        }

        for (final ItemStack yes : stuff) {
            if (Minecraft.getMinecraft().theWorld != null) {
                RenderHelper.enableGUIStandardItemLighting();
                cock += 20;
            }
            GlStateManager.pushMatrix();
            GlStateManager.disableAlpha();
            GlStateManager.clear(256);
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(yes, cock + x, y);
            GlStateManager.disableBlend();
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.disableDepth();
            GlStateManager.disableLighting();
            GlStateManager.enableDepth();
            GlStateManager.scale(2.0f, 2.0f, 2.0f);
            GlStateManager.enableAlpha();
            GlStateManager.popMatrix();
        }
        GL11.glPopMatrix();
    }

    enum TargetMode {
        ETB
    }
}

