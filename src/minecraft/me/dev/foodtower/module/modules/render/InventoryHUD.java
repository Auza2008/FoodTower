/*
Author:SuMuGod
Date:2022/7/10 5:06
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventRender2D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.hudeditor.HUDEditor;
import me.dev.foodtower.utils.normal.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.awt.*;

public class InventoryHUD extends Module {
    public InventoryHUD() {
        super("InventoryHUD", "物品栏HUD", new String[]{"InvHUD", "IHUD"}, ModuleType.Render);
    }

    @NMSL
    public void renderinventory(EventRender2D e) {
        if (!HUDEditor.inv.extended)
            return;
        float startX = HUDEditor.inv.x;
        float startY = HUDEditor.inv.y + 15;
        int curIndex = 0;
        RenderUtil.drawBordered(HUDEditor.inv.x, HUDEditor.inv.y + 15, (20 * 9) + 2, (20 * 3) + 2, 1, 0xAA << 24, new Color(0x80FFFFFF, true).getRGB());

        for (int i = 9; i < 36; ++i) {
            ItemStack slot = mc.thePlayer.inventory.mainInventory[i];
            if (slot == null) {
                startX += 20;
                curIndex += 1;

                if (curIndex > 8) {
                    curIndex = 0;
                    startY += 20;
                    startX = HUDEditor.inv.x;
                }

                continue;
            }

            this.drawItemStack(slot, startX, startY);
            startX += 20;
            curIndex += 1;
            if (curIndex > 8) {
                curIndex = 0;
                startY += 20;
                startX = HUDEditor.inv.x + 2;
            }
        }
    }

    private void drawItemStack(ItemStack stack, float x, float y) {
        GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableAlpha();
        GlStateManager.clear(256);
        mc.getRenderItem().zLevel = -150.0F;
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        mc.getRenderItem().renderItemIntoGUI(stack, x, y);
        mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRendererObj, stack, x, y, null);
        mc.getRenderItem().zLevel = 0.0F;
        GlStateManager.enableAlpha();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }
}

