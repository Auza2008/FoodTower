/*
Author:SuMuGod
Date:2022/7/10 5:07
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.render;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventRender2D;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import me.dev.foodtower.ui.hudeditor.HUDEditor;
import me.dev.foodtower.utils.normal.RenderUtil;
import me.dev.foodtower.value.Numbers;
import me.dev.foodtower.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Radar extends Module {
    private Numbers<Double> scale = new Numbers<Double>("Scale", "scale", 128.0, 30.0, 300.0, 1.0);

    public static final Option<Boolean> players = new Option<>("Players", "Players", true);
    public static final Option<Boolean> animals = new Option<>("Animals", "Animals", true);
    public static final Option<Boolean> mobs = new Option<>("Mobs",  "Mobs", true);
    public static final Option<Boolean> item = new Option<>("Items", "Items", false);
    public static final Option<Boolean> invis = new Option<>("Invisibles", "Invisibles", false);
    public static int SIZE = 128;
    private static final int BGCOLOR = 0xAA << 24;
    public Radar() {
        super("Radar", "寻人之物", new String[]{"rd"}, ModuleType.Render);
    }

    @NMSL
    public void onRender(EventRender2D eventRender2D) {
        if (!HUDEditor.radar.extended)
            return;
        SIZE = scale.getValue().intValue();

        float radarX = HUDEditor.radar.x;
        float radarY = HUDEditor.radar.y + 15;

        if (!Minecraft.getMinecraft().gameSettings.showDebugInfo) {

            RenderUtil.drawRect(radarX + ((SIZE / 2f) - 0.5f), radarY + 3.5f, radarX + (SIZE / 2f) + 0.5f, (radarY + SIZE) - 3.5f, new Color(255, 255, 255, 80).getRGB());
            RenderUtil.drawRect(radarX + 3.5f, radarY + ((SIZE / 2f) - 0.5f), (radarX + SIZE) - 3.5f, radarY + (SIZE / 2) + 0.5f, new Color(255, 255, 255, 80).getRGB());

            float partialTicks = eventRender2D.getPartialTicks();
            Minecraft mc = Minecraft.getMinecraft();
            ScaledResolution sr = new ScaledResolution(mc);
            Entity player = mc.thePlayer;
            GL11.glPushMatrix();
            GL11.glTranslated(radarX, radarY, 0);
            RenderUtil.drawBordered(0, 0, SIZE, SIZE, 1, BGCOLOR, new Color(0x80FFFFFF, true).getRGB());
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glScissor((int) (radarX * sr.getScaleFactor()), (int) (mc.displayHeight - (radarY+SIZE) * sr.getScaleFactor()), SIZE * sr.getScaleFactor(), SIZE * sr.getScaleFactor());
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glTranslated(SIZE / (double)2, SIZE / (double)2, 0);
            GL11.glRotated(player.prevRotationYaw + (player.prevRotationYaw - player.prevRotationYaw) * partialTicks, 0, 0, -1);
            GL11.glPointSize(4 * sr.getScaleFactor());
            GL11.glEnable(GL11.GL_POINT_SMOOTH);
            GL11.glBegin(GL11.GL_POINTS);
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glVertex2d(0, 0);
            GL11.glColor4f(1, 0, 0, 1);
            Entity[] entities = getAllMatchedEntity();
            for (Entity entity : entities) {
                double dx = (player.prevPosX + (player.posX - player.prevPosX) * partialTicks) - (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks),
                        dz = (player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks) - (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks);
                GL11.glVertex2d(dx, dz);
            }
            GL11.glEnd();
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_POINT_SMOOTH);
        }
    }

    private Entity[] getAllMatchedEntity() {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        Entity player = mc.thePlayer;
        if(world != null) {
            ArrayList<Entity> entities = new ArrayList<>(world.loadedEntityList.size());
            double max = (SIZE * SIZE) * 2;
            for (Entity entity : world.loadedEntityList) {
                if(entity == player)
                    continue;
                double dx = player.posX - entity.posX,
                        dz = player.posZ - entity.posZ;
                double distance = dx * dx + dz * dz;
                if(distance > max)
                    continue;
                if(entity instanceof EntityPlayer && !players.getValue())
                    continue;
                if(entity instanceof EntityMob && !mobs.getValue())
                    continue;
                if(entity instanceof EntityAnimal && !animals.getValue())
                    continue;
                if(entity instanceof EntityItem && !item.getValue())
                    continue;
                if(entity.isInvisible() && !invis.getValue())
                    continue;
                entities.add(entity);
            }
            return entities.toArray(new Entity[0]);
        }
        return new Entity[0];
    }
}

