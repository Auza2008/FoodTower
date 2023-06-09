package cn.foodtower.module.modules.render;

import cn.foodtower.api.EventHandler;
import cn.foodtower.api.events.Render.EventRender2D;
import cn.foodtower.api.events.Render.EventRender3D;
import cn.foodtower.api.value.Mode;
import cn.foodtower.api.value.Numbers;
import cn.foodtower.api.value.Option;
import cn.foodtower.manager.FriendManager;
import cn.foodtower.manager.ModuleManager;
import cn.foodtower.module.Module;
import cn.foodtower.module.ModuleType;
import cn.foodtower.module.modules.world.Teams;
import cn.foodtower.util.misc.liquidbounce.LiquidRender;
import cn.foodtower.util.render.Colors;
import cn.foodtower.util.render.ETBRenderUtil;
import cn.foodtower.util.render.RenderUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class ESP extends Module {
    private static final Map<EntityPlayer, float[][]> entities = new HashMap<>();
    public static Mode mode = new Mode("Mode", "mode", TwoD.values(), TwoD.Box2D);
    public static Numbers<Double> r = new Numbers<>("Red", "Red", 255d, 0d, 255d, 1d);
    public static Numbers<Double> g = new Numbers<>("Green", "Green", 255d, 0d, 255d, 1d);
    public static Numbers<Double> b = new Numbers<>("Blue", "Blue", 255d, 0d, 255d, 1d);
    public static Numbers<Double> a = new Numbers<>("Alpha", "Alpha", 40d, 0d, 255d, 1d);
    public static Option HEALTH = new Option("Health", "Health", false);
    public static Option player = new Option("Players", "Players", true);
    public static Option mobs = new Option("Mobs", "Mobs", false);
    public static Option animals = new Option("Animals", "Animals", false);
    public static Option armorstand = new Option("ArmorStand", "ArmorStand", false);
    public static Option antiinvis = new Option("AntiInvis", "AntiInvis", true);
    private final Numbers<Double> skeletonwidth = new Numbers<>("SkeletonWidth", 1.0, 0.5, 10.0, 0.1);
    private final Option skeleton = new Option("Skeleton", false);
    private final Map<EntityLivingBase, double[]> entityConvertedPointsMap;
    FontRenderer fr;

    public ESP() {
        super("ESP", new String[0], ModuleType.Render);
        this.addValues(mode, r, g, b, a, HEALTH, player, antiinvis, mobs, animals, armorstand, skeleton, skeletonwidth);
        this.entityConvertedPointsMap = new HashMap<>();
    }

    public static void addEntity(EntityPlayer e, ModelPlayer model) {
        entities.put(e, new float[][]{{model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, model.bipedHead.rotateAngleZ}, {model.bipedRightArm.rotateAngleX, model.bipedRightArm.rotateAngleY, model.bipedRightArm.rotateAngleZ}, {model.bipedLeftArm.rotateAngleX, model.bipedLeftArm.rotateAngleY, model.bipedLeftArm.rotateAngleZ}, {model.bipedRightLeg.rotateAngleX, model.bipedRightLeg.rotateAngleY, model.bipedRightLeg.rotateAngleZ}, {model.bipedLeftLeg.rotateAngleX, model.bipedLeftLeg.rotateAngleY, model.bipedLeftLeg.rotateAngleZ}});
    }

    public static Color blend(Color color1, Color color2, double ratio) {
        float r = (float) ratio;
        float ir = 1.0f - r;
        float[] rgb1 = new float[3];
        float[] rgb2 = new float[3];
        color1.getColorComponents(rgb1);
        color2.getColorComponents(rgb2);
        float red = rgb1[0] * r + rgb2[0] * ir;
        float green = rgb1[1] * r + rgb2[1] * ir;
        float blue = rgb1[2] * r + rgb2[2] * ir;
        if (red < 0.0f) {
            red = 0.0f;
        } else if (red > 255.0f) {
            red = 255.0f;
        }
        if (green < 0.0f) {
            green = 0.0f;
        } else if (green > 255.0f) {
            green = 255.0f;
        }
        if (blue < 0.0f) {
            blue = 0.0f;
        } else if (blue > 255.0f) {
            blue = 255.0f;
        }
        Color color3 = null;
        try {
            color3 = new Color(red, green, blue);
        } catch (IllegalArgumentException exp) {
            NumberFormat nf = NumberFormat.getNumberInstance();
            System.out.println(nf.format(red) + "; " + nf.format(green) + "; " + nf.format(blue));
            exp.printStackTrace();
        }
        return color3;
    }

    public static boolean qualifiesESP(Entity e2) {
        if (e2 == mc.thePlayer) {
            return false;
        } else if (e2 instanceof EntityPlayer && player.get()) {
            return true;
        } else if (e2 instanceof EntityMob && mobs.get()) {
            return true;
        } else if (e2 instanceof EntityAnimal && animals.get()) {
            return true;
        } else if (e2 instanceof EntityVillager && animals.get()) {
            return true;
        } else return e2 instanceof EntityArmorStand && armorstand.get();
    }

    public static void drawBorderedRect(float x, float y, float x2, float y2, float l1, int col1, int col2) {
        drawRect(x, y, x2, y2, col2);
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glLineWidth(l1);
        GL11.glBegin(1);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y);
        GL11.glVertex2d(x2, y);
        GL11.glVertex2d(x, y2);
        GL11.glVertex2d(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    public static void drawRect(float g, float h, float i, float j, int col1) {
        float f = (float) (col1 >> 24 & 255) / 255.0F;
        float f1 = (float) (col1 >> 16 & 255) / 255.0F;
        float f2 = (float) (col1 >> 8 & 255) / 255.0F;
        float f3 = (float) (col1 & 255) / 255.0F;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glBegin(7);
        GL11.glVertex2d(i, h);
        GL11.glVertex2d(g, h);
        GL11.glVertex2d(g, j);
        GL11.glVertex2d(i, j);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
    }

    private static boolean isValid(Entity entity) {
        if (entity == mc.thePlayer) {
            return false;
        }
        if (entity.isDead) {
            return false;
        }
        if (entity.isInvisible()) {
            return false;
        }
        if (entity instanceof EntityItem) {
            return false;
        }
        if (entity instanceof EntityAnimal) {
            return false;
        }
        return entity instanceof EntityPlayer;
    }

    @EventHandler
    private void on2D(EventRender2D e) {
        if (mode.get().equals(TwoD.Only2DBox)) {
            GlStateManager.pushMatrix();
            for (Entity entity : this.entityConvertedPointsMap.keySet()) {
                EntityPlayer ent = (EntityPlayer) entity;
                double[] renderPositions = this.entityConvertedPointsMap.get(ent);
                double[] renderPositionsBottom = new double[]{renderPositions[4], renderPositions[5], renderPositions[6]};
                double[] renderPositionsX = new double[]{renderPositions[7], renderPositions[8], renderPositions[9]};
                double[] renderPositionsX2 = new double[]{renderPositions[10], renderPositions[11], renderPositions[12]};
                double[] renderPositionsZ = new double[]{renderPositions[13], renderPositions[14], renderPositions[15]};
                double[] renderPositionsZ2 = new double[]{renderPositions[16], renderPositions[17], renderPositions[18]};
                double[] renderPositionsTop1 = new double[]{renderPositions[19], renderPositions[20], renderPositions[21]};
                double[] renderPositionsTop2 = new double[]{renderPositions[22], renderPositions[23], renderPositions[24]};
                GlStateManager.pushMatrix();
                GlStateManager.scale(0.5, 0.5, 0.5);
                if (!(ent.isInvisible() || ent instanceof EntityPlayerSP)) {
                    try {
                        double[] xValues = new double[]{renderPositions[0], renderPositionsBottom[0], renderPositionsX[0], renderPositionsX2[0], renderPositionsZ[0], renderPositionsZ2[0], renderPositionsTop1[0], renderPositionsTop2[0]};
                        double[] yValues = new double[]{renderPositions[1], renderPositionsBottom[1], renderPositionsX[1], renderPositionsX2[1], renderPositionsZ[1], renderPositionsZ2[1], renderPositionsTop1[1], renderPositionsTop2[1]};
                        double x2 = renderPositions[0];
                        double y2 = renderPositions[1];
                        double endx = renderPositionsBottom[0];
                        double endy = renderPositionsBottom[1];
                        for (double bdubs : xValues) {
                            if (!(bdubs < x2)) continue;
                            x2 = bdubs;
                        }
                        for (double bdubs : xValues) {
                            if (!(bdubs > endx)) continue;
                            endx = bdubs;
                        }
                        for (double bdubs : yValues) {
                            if (!(bdubs < y2)) continue;
                            y2 = bdubs;
                        }
                        for (double bdubs : yValues) {
                            if (!(bdubs > endy)) continue;
                            endy = bdubs;
                        }
                        if (mode.get().equals(TwoD.Only2DBox)) {
                            RenderUtil.rectangleBordered(x2 + 0.5, y2 + 0.5, endx - 0.5, endy - 0.5, 1.0, Colors.getColor(0, 0, 0, 0), new Color(-1).getRGB());
                            RenderUtil.rectangleBordered(x2 - 0.5, y2 - 0.5, endx + 0.5, endy + 0.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0, 150));
                            RenderUtil.rectangleBordered(x2 + 1.5, y2 + 1.5, endx - 1.5, endy - 1.5, 1.0, Colors.getColor(0, 0), Colors.getColor(0, 150));
                        }
//                        if (mode.isCurrentMode("CornerB")) {
//                            RenderUtil.rectangle(x2 + 0.5, y2 + 0.5, x2 + 1.5, y2 + xDiff + 0.5, color);
//                            RenderUtil.rectangle(x2 + 0.5, endy - 0.5, x2 + 1.5, endy - xDiff - 0.5, color);
//                            RenderUtil.rectangle(x2 - 0.5, y2 + 0.5, x2 + 0.5, y2 + xDiff + 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + 1.5, y2 + 2.5, x2 + 2.5, y2 + xDiff + 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 - 0.5, y2 + xDiff + 0.5, x2 + 2.5, y2 + xDiff + 1.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 - 0.5, endy - 0.5, x2 + 0.5, endy - xDiff - 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + 1.5, endy - 2.5, x2 + 2.5, endy - xDiff - 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 - 0.5, endy - xDiff - 0.5, x2 + 2.5, endy - xDiff - 1.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + 1.0, y2 + 0.5, x2 + x2Diff, y2 + 1.5, color);
//                            RenderUtil.rectangle(x2 - 0.5, y2 - 0.5, x2 + x2Diff, y2 + 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + 1.5, y2 + 1.5, x2 + x2Diff, y2 + 2.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + x2Diff, y2 - 0.5, x2 + x2Diff + 1.0, y2 + 2.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + 1.0, endy - 0.5, x2 + x2Diff, endy - 1.5, color);
//                            RenderUtil.rectangle(x2 - 0.5, endy + 0.5, x2 + x2Diff, endy - 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + 1.5, endy - 1.5, x2 + x2Diff, endy - 2.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(x2 + x2Diff, endy + 0.5, x2 + x2Diff + 1.0, endy - 2.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - 0.5, y2 + 0.5, endx - 1.5, y2 + xDiff + 0.5, color);
//                            RenderUtil.rectangle(endx - 0.5, endy - 0.5, endx - 1.5, endy - xDiff - 0.5, color);
//                            RenderUtil.rectangle(endx + 0.5, y2 + 0.5, endx - 0.5, y2 + xDiff + 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - 1.5, y2 + 2.5, endx - 2.5, y2 + xDiff + 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx + 0.5, y2 + xDiff + 0.5, endx - 2.5, y2 + xDiff + 1.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx + 0.5, endy - 0.5, endx - 0.5, endy - xDiff - 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - 1.5, endy - 2.5, endx - 2.5, endy - xDiff - 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx + 0.5, endy - xDiff - 0.5, endx - 2.5, endy - xDiff - 1.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - 1.0, y2 + 0.5, endx - x2Diff, y2 + 1.5, color);
//                            RenderUtil.rectangle(endx + 0.5, y2 - 0.5, endx - x2Diff, y2 + 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - 1.5, y2 + 1.5, endx - x2Diff, y2 + 2.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - x2Diff, y2 - 0.5, endx - x2Diff - 1.0, y2 + 2.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - 1.0, endy - 0.5, endx - x2Diff, endy - 1.5, color);
//                            RenderUtil.rectangle(endx + 0.5, endy + 0.5, endx - x2Diff, endy - 0.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - 1.5, endy - 1.5, endx - x2Diff, endy - 2.5, Colors.getColor(0, 150));
//                            RenderUtil.rectangle(endx - x2Diff, endy + 0.5, endx - x2Diff - 1.0, endy - 2.5, Colors.getColor(0, 150));
//                        }
                        if (HEALTH.get() && (mode.get().equals(TwoD.Only2DBox))) {
                            float health = ent.getHealth();
                            float[] fractions = new float[]{0.0f, 0.5f, 1.0f};
                            Color[] colors = new Color[]{Color.RED, Color.YELLOW, Color.GREEN};
                            float progress = health / ent.getMaxHealth();
                            Color customColor = health >= 0.0f ? this.blendColors(fractions, colors, progress).brighter() : Color.RED;
                            double difference = y2 - endy + 0.5;
                            double healthLocation = endy + difference * (double) progress;
                            RenderUtil.rectangleBordered(x2 - 6.5, y2 - 0.5, x2 - 2.5, endy, 1.0, Colors.getColor(0, 100), Colors.getColor(0, 150));
                            RenderUtil.rectangle(x2 - 5.5, endy - 1.0, x2 - 3.5, healthLocation, customColor.getRGB());
                            if (-difference > 50.0) {
                                for (int i2 = 1; i2 < 10; ++i2) {
                                    double dThing = difference / 10.0 * (double) i2;
                                    RenderUtil.rectangle(x2 - 6.5, endy - 0.5 + dThing, x2 - 2.5, endy - 0.5 + dThing - 1.0, Colors.getColor(0));
                                }
                            }
                            if ((int) this.getIncremental(progress * 100.0f, 1.0) <= 40) {
                                GlStateManager.pushMatrix();
                                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                                GlStateManager.popMatrix();
                            }
                        }
                    } catch (Exception exception) {
                        // empty catch block
                    }
                }
                GlStateManager.popMatrix();
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            }
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
            RenderUtil.rectangle(0.0, 0.0, 0.0, 0.0, -1);
        }
    }

    public double getIncremental(double val, double inc) {
        double one = 1.0 / inc;
        return (double) Math.round(val * one) / one;
    }

    public Color blendColors(float[] fractions, Color[] colors, float progress) {
        if (fractions == null) {
            throw new IllegalArgumentException("Fractions can't be null");
        }
        if (colors == null) {
            throw new IllegalArgumentException("Colours can't be null");
        }
        if (fractions.length != colors.length) {
            throw new IllegalArgumentException("Fractions and colours must have equal number of elements");
        }
        int[] indicies = this.getFractionIndicies(fractions, progress);
        float[] range = new float[]{fractions[indicies[0]], fractions[indicies[1]]};
        Color[] colorRange = new Color[]{colors[indicies[0]], colors[indicies[1]]};
        float max = range[1] - range[0];
        float value = progress - range[0];
        float weight = value / max;
        return blend(colorRange[0], colorRange[1], 1.0f - weight);
    }

    public int[] getFractionIndicies(float[] fractions, float progress) {
        int startPoint;
        int[] range = new int[2];
        for (startPoint = 0; startPoint < fractions.length && fractions[startPoint] <= progress; ++startPoint) {
        }
        if (startPoint >= fractions.length) {
            startPoint = fractions.length - 1;
        }
        range[0] = startPoint - 1;
        range[1] = startPoint;
        return range;
    }

    @EventHandler
    public void onRender(EventRender3D event) {
        if (mode.get().equals(TwoD.Box2D)) {
            doOther2DESP(event);
        } else if (mode.get().equals(TwoD.LB2D)) {
            doCornerESP(event);
        }
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLiving = (EntityLivingBase) entity;
                if (mode.get().equals(TwoD.Box3D) && qualifiesESP(entity)) {
                    if (FriendManager.isFriend(entity.getName())) {
                        LiquidRender.drawEntityBox(entity, new Color(255, 233, 105, a.get().intValue()), false);
                    } else {
                        LiquidRender.drawEntityBox(entity, new Color(entityLiving.hurtTime > 0 ? 255 : r.get().intValue(), entityLiving.hurtTime > 0 ? 0 : g.get().intValue(), entityLiving.hurtTime > 0 ? 0 : b.get().intValue(), a.get().intValue()), false);
                    }
                }
                if (mode.get().equals(TwoD.Box3D2) && qualifiesESP(entity)) {
                    LiquidRender.drawEntityBox(entity, new Color(entityLiving.hurtTime > 0 ? 255 : r.get().intValue(), entityLiving.hurtTime > 0 ? 0 : g.get().intValue(), entityLiving.hurtTime > 0 ? 0 : b.get().intValue(), a.get().intValue()), true);
                }
            }
        }
        try {
            this.updatePositions();
        } catch (Exception ignored) {

        }
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (!skeleton.get()) return;
        startEnd(true);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glDisable(2848);
        entities.keySet().removeIf(this::doesntContain);
        mc.theWorld.playerEntities.forEach(player -> drawSkeleton(e, player));
        Gui.drawRect(0, 0, 0, 0, 0);
        startEnd(false);
    }

    private boolean doesntContain(EntityPlayer var0) {
        return !mc.theWorld.playerEntities.contains(var0);
    }

    private Vec3 getVec3(EventRender3D event, EntityPlayer var0) {
        float timer = event.getPartialTicks();
        double x = var0.lastTickPosX + (var0.posX - var0.lastTickPosX) * timer;
        double y = var0.lastTickPosY + (var0.posY - var0.lastTickPosY) * timer;
        double z = var0.lastTickPosZ + (var0.posZ - var0.lastTickPosZ) * timer;
        return new Vec3(x, y, z);
    }

    private void drawSkeleton(EventRender3D event, EntityPlayer e) {
        final Color color = new Color(FriendManager.isFriend(e.getName()) ? 0xFF7FCDFF : (e.getName().equalsIgnoreCase(mc.thePlayer.getName()) ? 0xFF99ff99 : new Color(0xFFF9F8).getRGB()));
        if (!e.isInvisible()) {
            float[][] entPos = entities.get(e);
            if (entPos != null && e.isEntityAlive() && ETBRenderUtil.isInViewFrustrum(e) && !e.isDead && e != mc.thePlayer && !e.isPlayerSleeping()) {
                GL11.glPushMatrix();
                GL11.glLineWidth(skeletonwidth.get().floatValue());
                GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1);
                Vec3 vec = getVec3(event, e);
                double x = vec.xCoord - RenderManager.renderPosX;
                double y = vec.yCoord - RenderManager.renderPosY;
                double z = vec.zCoord - RenderManager.renderPosZ;
                GL11.glTranslated(x, y, z);
                float xOff = e.prevRenderYawOffset + (e.renderYawOffset - e.prevRenderYawOffset) * event.getPartialTicks();
                GL11.glRotatef(-xOff, 0.0F, 1.0F, 0.0F);
                GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? -0.235D : 0.0D);
                float yOff = e.isSneaking() ? 0.6F : 0.75F;
                GL11.glPushMatrix();
                GlStateManager.color(1, 1, 1, 1);
                GL11.glTranslated(-0.125D, yOff, 0.0D);
                if (entPos[3][0] != 0.0F) {
                    GL11.glRotatef(entPos[3][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[3][1] != 0.0F) {
                    GL11.glRotatef(entPos[3][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[3][2] != 0.0F) {
                    GL11.glRotatef(entPos[3][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, (-yOff), 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.color(1, 1, 1, 1);
                GL11.glTranslated(0.125D, yOff, 0.0D);
                if (entPos[4][0] != 0.0F) {
                    GL11.glRotatef(entPos[4][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[4][1] != 0.0F) {
                    GL11.glRotatef(entPos[4][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[4][2] != 0.0F) {
                    GL11.glRotatef(entPos[4][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, (-yOff), 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glTranslated(0.0D, 0.0D, e.isSneaking() ? 0.25D : 0.0D);
                GL11.glPushMatrix();
                GlStateManager.color(1, 1, 1, 1);
                GL11.glTranslated(0.0D, e.isSneaking() ? -0.05D : 0.0D, e.isSneaking() ? -0.01725D : 0.0D);
                GL11.glPushMatrix();
                GlStateManager.color(1, 1, 1, 1);
                GL11.glTranslated(-0.375D, yOff + 0.55D, 0.0D);
                if (entPos[1][0] != 0.0F) {
                    GL11.glRotatef(entPos[1][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[1][1] != 0.0F) {
                    GL11.glRotatef(entPos[1][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[1][2] != 0.0F) {
                    GL11.glRotatef(-entPos[1][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -0.5D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.375D, yOff + 0.55D, 0.0D);
                if (entPos[2][0] != 0.0F) {
                    GL11.glRotatef(entPos[2][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                if (entPos[2][1] != 0.0F) {
                    GL11.glRotatef(entPos[2][1] * 57.295776F, 0.0F, 1.0F, 0.0F);
                }

                if (entPos[2][2] != 0.0F) {
                    GL11.glRotatef(-entPos[2][2] * 57.295776F, 0.0F, 0.0F, 1.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, -0.5D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glRotatef(xOff - e.rotationYawHead, 0.0F, 1.0F, 0.0F);
                GL11.glPushMatrix();
                GlStateManager.color(1, 1, 1, 1);
                GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
                if (entPos[0][0] != 0.0F) {
                    GL11.glRotatef(entPos[0][0] * 57.295776F, 1.0F, 0.0F, 0.0F);
                }

                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, 0.3D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GL11.glRotatef(e.isSneaking() ? 25.0F : 0.0F, 1.0F, 0.0F, 0.0F);
                GL11.glTranslated(0.0D, e.isSneaking() ? -0.16175D : 0.0D, e.isSneaking() ? -0.48025D : 0.0D);
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, yOff, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.125D, 0.0D, 0.0D);
                GL11.glVertex3d(0.125D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GlStateManager.color(1, 1, 1, 1);
                GL11.glTranslated(0.0D, yOff, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(0.0D, 0.0D, 0.0D);
                GL11.glVertex3d(0.0D, 0.55D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                GL11.glTranslated(0.0D, yOff + 0.55D, 0.0D);
                GL11.glBegin(3);
                GL11.glVertex3d(-0.375D, 0.0D, 0.0D);
                GL11.glVertex3d(0.375D, 0.0D, 0.0D);
                GL11.glEnd();
                GL11.glPopMatrix();
                GL11.glPopMatrix();
                GlStateManager.color(1, 1, 1, 1);
            }
        }
    }

    private void startEnd(boolean revert) {
        if (revert) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GL11.glEnable(2848);
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(770, 771);
            GL11.glHint(3154, 4354);
        } else {
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GL11.glDisable(2848);
            GlStateManager.enableDepth();
            GlStateManager.popMatrix();
        }

        GlStateManager.depthMask(!revert);
    }

    private void updatePositions() {
        this.entityConvertedPointsMap.clear();
        float pTicks = ESP.mc.timer.renderPartialTicks;
        for (Entity e2 : mc.theWorld.getLoadedEntityList()) {
            EntityPlayer ent;
            double topY;
            if (!(e2 instanceof EntityPlayer) || (ent = (EntityPlayer) e2) == mc.thePlayer) continue;
            double x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks - ESP.mc.getRenderManager().viewerPosX + 0.36;
            double y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double) pTicks - ESP.mc.getRenderManager().viewerPosY;
            double z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - ESP.mc.getRenderManager().viewerPosZ + 0.36;
            y = topY = y + ((double) ent.height + 0.15);
            double[] convertedPoints = RenderUtil.convertTo2D(x, y, z);
            double[] convertedPoints2 = RenderUtil.convertTo2D(x - 0.36, y, z - 0.36);
            double xd = 0.0;
            if (convertedPoints2[2] < 0.0 || convertedPoints2[2] >= 1.0) continue;
            x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks - ESP.mc.getRenderManager().viewerPosX - 0.36;
            z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - ESP.mc.getRenderManager().viewerPosZ - 0.36;
            double[] convertedPointsBottom = RenderUtil.convertTo2D(x, y, z);
            y = ent.lastTickPosY + (ent.posY - ent.lastTickPosY) * (double) pTicks - ESP.mc.getRenderManager().viewerPosY - 0.05;
            double[] convertedPointsx = RenderUtil.convertTo2D(x, y, z);
            x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks - ESP.mc.getRenderManager().viewerPosX - 0.36;
            z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - ESP.mc.getRenderManager().viewerPosZ + 0.36;
            double[] convertedPointsTop1 = RenderUtil.convertTo2D(x, topY, z);
            double[] convertedPointsx2 = RenderUtil.convertTo2D(x, y, z);
            x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks - ESP.mc.getRenderManager().viewerPosX + 0.36;
            z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - ESP.mc.getRenderManager().viewerPosZ + 0.36;
            double[] convertedPointsz = RenderUtil.convertTo2D(x, y, z);
            x = ent.lastTickPosX + (ent.posX - ent.lastTickPosX) * (double) pTicks - ESP.mc.getRenderManager().viewerPosX + 0.36;
            z = ent.lastTickPosZ + (ent.posZ - ent.lastTickPosZ) * (double) pTicks - ESP.mc.getRenderManager().viewerPosZ - 0.36;
            double[] convertedPointsTop2 = RenderUtil.convertTo2D(x, topY, z);
            double[] convertedPointsz2 = RenderUtil.convertTo2D(x, y, z);
            this.entityConvertedPointsMap.put(ent, new double[]{convertedPoints[0], convertedPoints[1], 0.0, convertedPoints[2], convertedPointsBottom[0], convertedPointsBottom[1], convertedPointsBottom[2], convertedPointsx[0], convertedPointsx[1], convertedPointsx[2], convertedPointsx2[0], convertedPointsx2[1], convertedPointsx2[2], convertedPointsz[0], convertedPointsz[1], convertedPointsz[2], convertedPointsz2[0], convertedPointsz2[1], convertedPointsz2[2], convertedPointsTop1[0], convertedPointsTop1[1], convertedPointsTop1[2], convertedPointsTop2[0], convertedPointsTop2[1], convertedPointsTop2[2]});
        }
    }

    private void doOther2DESP(EventRender3D e) {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (qualifiesESP(entity)) {
                GL11.glPushMatrix();
                GL11.glEnable(3042);
                GL11.glDisable(2929);
                GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                GlStateManager.enableBlend();
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(3553);
                double renderPosX = mc.getRenderManager().viewerPosX;
                double renderPosY = mc.getRenderManager().viewerPosY;
                double renderPosZ = mc.getRenderManager().viewerPosZ;
                float partialTicks = e.getPartialTicks();
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - renderPosY;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - renderPosZ;
                float DISTANCE = mc.thePlayer.getDistanceToEntity(entity);
                float SCALE = 0.035f;
                SCALE /= 2.0f;
                entity.isChild();
                GlStateManager.translate((float) x, (float) y + entity.height + 0.5f - (entity.isChild() ? (entity.height / 2.0f) : 0.0f), (float) z);
                GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                GL11.glScalef(-SCALE, -SCALE, -SCALE);
                float HEALTH = entity.getHealth();
                int COLOR = -1;
                if (HEALTH > 20.0) {
                    COLOR = -65292;
                } else if (HEALTH >= 10.0) {
                    COLOR = -16711936;
                } else if (HEALTH >= 3.0) {
                    COLOR = -23296;
                } else {
                    COLOR = -65536;
                }
                new Color(0, 0, 0);
                double thickness = 1.5f + DISTANCE * 0.01f;
                double xLeft = -20.0;
                double xRight = 20.0;
                double yUp = 27.0;
                double yDown = 130.0;
                Color color = new Color(255, 255, 255);
                if (entity.hurtTime > 0) {
                    color = new Color(255, 0, 0);
                }
                else if (ModuleManager.getModuleByClass(Teams.class).isEnabled() && Teams.isOnSameTeam(entity)) {
                    color = new Color(0, 255, 0);
                } else if (entity.isInvisible()) {
                }
                drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness + 0.5f, Color.BLACK.getRGB(), 0);
                drawBorderedRect((float) xLeft, (float) yUp, (float) xRight, (float) yDown, (float) thickness, color.getRGB(), 0);
                drawBorderedRect((float) xLeft - 3.0f - DISTANCE * 0.2f, (float) yDown - (float) (yDown - yUp), (float) xLeft - 2.0f, (float) yDown, 0.15f, Color.BLACK.getRGB(), new Color(100, 100, 100).getRGB());
                drawBorderedRect((float) xLeft - 3.0f - DISTANCE * 0.2f, (float) yDown - (float) (yDown - yUp) * Math.min(1.0f, entity.getHealth() / 20.0f), (float) xLeft - 2.0f, (float) yDown, 0.15f, Color.BLACK.getRGB(), COLOR);
                GL11.glEnable(3553);
                GL11.glEnable(2929);
                GlStateManager.disableBlend();
                GL11.glDisable(3042);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glNormal3f(1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            }
        }
    }

    private void doCornerESP(EventRender3D e) {
        for (EntityPlayer entity : mc.theWorld.playerEntities) {
            if (entity == mc.thePlayer) continue;
            if (!isValid(entity) && !entity.isInvisible()) {
                return;
            }
            if (entity.isInvisible()) {
                if (entity != mc.thePlayer) {
                    return;
                }
            }
            GL11.glPushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(2929);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.enableBlend();
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(3553);
            double renderPosX = ESP.mc.getRenderManager().viewerPosX;
            double renderPosY = ESP.mc.getRenderManager().viewerPosY;
            double renderPosZ = ESP.mc.getRenderManager().viewerPosZ;
            float partialTicks = e.getPartialTicks();
            double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks - renderPosX;
            double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks - renderPosY;
            double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks - renderPosZ;
            float DISTANCE = mc.thePlayer.getDistanceToEntity(entity);
            float SCALE = 0.035f;
            SCALE /= 2.0f;
            GlStateManager.translate((float) x, (float) y + entity.height + 0.5f - (entity.isChild() ? entity.height / 2.0f : 0.0f), (float) z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            mc.getRenderManager();
            GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GL11.glScalef(-SCALE, -SCALE, -SCALE);
            Color color = entity.hurtTime > 0 ? new Color(Color.RED.getRGB()) : new Color(Color.WHITE.getRGB());
            Color gray = new Color(0, 0, 0);
            double thickness = 2.0f + DISTANCE * 0.08f;
            double xLeft = -30.0;
            double xRight = 30.0;
            double yUp = 20.0;
            double yDown = 130.0;
            double size = 10.0;
            this.drawVerticalLine(xLeft + size / 2.0 - 1.0, yUp + 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xLeft + 1.0, yUp + size, size, thickness, gray);
            this.drawVerticalLine(xLeft + size / 2.0 - 1.0, yUp, size / 2.0, thickness, color);
            this.drawHorizontalLine(xLeft, yUp + size, size, thickness, color);
            this.drawVerticalLine(xRight - size / 2.0 + 1.0, yUp + 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xRight - 1.0, yUp + size, size, thickness, gray);
            this.drawVerticalLine(xRight - size / 2.0 + 1.0, yUp, size / 2.0, thickness, color);
            this.drawHorizontalLine(xRight, yUp + size, size, thickness, color);
            this.drawVerticalLine(xLeft + size / 2.0 - 1.0, yDown - 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xLeft + 1.0, yDown - size, size, thickness, gray);
            this.drawVerticalLine(xLeft + size / 2.0 - 1.0, yDown, size / 2.0, thickness, color);
            this.drawHorizontalLine(xLeft, yDown - size, size, thickness, color);
            this.drawVerticalLine(xRight - size / 2.0 + 1.0, yDown - 1.0, size / 2.0, thickness, gray);
            this.drawHorizontalLine(xRight - 1.0, yDown - size, size, thickness, gray);
            this.drawVerticalLine(xRight - size / 2.0 + 1.0, yDown, size / 2.0, thickness, color);
            this.drawHorizontalLine(xRight, yDown - size, size, thickness, color);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GlStateManager.disableBlend();
            GL11.glDisable(3042);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glNormal3f(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }

    private void drawVerticalLine(double xPos, double yPos, double xSize, double thickness, Color color) {
        Tessellator tesselator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tesselator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(xPos - xSize, yPos - thickness / 2.0, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos - xSize, yPos + thickness / 2.0, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + xSize, yPos + thickness / 2.0, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + xSize, yPos - thickness / 2.0, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        tesselator.draw();
    }

    private void drawHorizontalLine(double xPos, double yPos, double ySize, double thickness, Color color) {
        Tessellator tesselator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tesselator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldRenderer.pos(xPos - thickness / 2.0, yPos - ySize, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos - thickness / 2.0, yPos + ySize, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + thickness / 2.0, yPos + ySize, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        worldRenderer.pos(xPos + thickness / 2.0, yPos - ySize, 0.0).color((float) color.getRed() / 255.0f, (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f, (float) color.getAlpha() / 255.0f).endVertex();
        tesselator.draw();
    }


    enum TwoD {
        Box3D, Box3D2, Box2D, Only2DBox, LB2D, New2DBox
    }
}
