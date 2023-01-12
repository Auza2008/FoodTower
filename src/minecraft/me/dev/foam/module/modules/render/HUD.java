/*
Author:SuMuGod
Date:2022/7/10 5:06
Project:foam Reborn
*/
package me.dev.foam.module.modules.render;

import me.dev.foam.Client;
import me.dev.foam.api.NMSL;
import me.dev.foam.api.events.EventRender2D;
import me.dev.foam.api.events.EventRenderCape;
import me.dev.foam.module.Module;
import me.dev.foam.module.ModuleManager;
import me.dev.foam.module.ModuleType;
import me.dev.foam.other.FriendManager;
import me.dev.foam.ui.font.FontManager;
import me.dev.foam.utils.math.Colors;
import me.dev.foam.utils.normal.GuiRenderUtils;
import me.dev.foam.utils.normal.Helper;
import me.dev.foam.utils.normal.MsgUtil;
import me.dev.foam.utils.normal.RenderUtil;
import me.dev.foam.value.Mode;
import me.dev.foam.value.Numbers;
import me.dev.foam.value.Option;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

import static org.lwjgl.opengl.GL11.*;

public class HUD extends Module {
    public static boolean shouldMove;
    public static boolean useFont;

    public static final ResourceLocation jigsawTexture512 = new ResourceLocation("foam/JIGSAW512.png");

    public static String wm = "Foam";
    private Option<Boolean> info = new Option<Boolean>("Information", "information", true);
    private Option<Boolean> rainbow = new Option<Boolean>("Rainbow", "rainbow", true);
    private Option<Boolean> background = new Option<>("BackGround", "background", true);
    private Option<Boolean> customfont = new Option<Boolean>("Font", "font", true);
    private Option<Boolean> logo = new Option<Boolean>("Logo", "logo", true);
    private Option<Boolean> capes = new Option<Boolean>("Capes", "capes", true);

    private Option<Boolean> msg = new Option<Boolean>("Notification", "Notification", true);
    public static Numbers<Integer> colorRedValue = new Numbers<>("Red", "Red", 255, 0, 255, 1);
    public static Numbers<Integer> colorGreenValue = new Numbers<>("Green", "Green", 255, 0, 255, 1);
    public static Numbers<Integer> colorBlueValue = new Numbers<>("Blue", "Blue", 255, 0, 255, 1);
    private static final ResourceLocation wurstLogo =
            new ResourceLocation("foam/wurst_128.png");
    public static Mode<Enum<Lang>> lang = new Mode<>("Lang", "Lang", Lang.values(), Lang.English);
    public Mode<Enum<HUDMode>> mode = new Mode<>("Mode", "Mode", HUDMode.values(), HUDMode.Foam);
    public SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    public HUD() {
        super("HUD", "界面示之以表", new String[]{"gui"}, ModuleType.Render);
        this.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)).getRGB());
        this.setEnabled(true);
        this.setRemoved(true);
    }

    public static boolean isLightMode() {
        return false;
    }

    @NMSL
    private void renderHud(EventRender2D event) {
        if (msg.getValue()) {
            MsgUtil.INSTANCE.drawNotifications();
        }

        if (mode.getValue() == HUDMode.Foam) {
            if (this.customfont.getValue()) {
                useFont = true;
            } else if (!this.customfont.getValue()) {
                useFont = false;
            }
            if (!mc.gameSettings.showDebugInfo) {
                String name;
                if (logo.getValue()) {
                    String serverip = mc.isSingleplayer() ? "singleplayer" : !mc.getCurrentServerData().serverIP.contains(":") ? mc.getCurrentServerData().serverIP + ":25565" : mc.getCurrentServerData().serverIP;
                    String info = Client.instance.name + " | " + mc.thePlayer.getName() + " | " + Minecraft.getDebugFPS() + " fps | " + serverip + " | " + formatter.format(new Date());

                    GuiRenderUtils.drawRect(5, 5, FontManager.F13.getStringWidth(info) + 4, 12, new Color(40, 40, 40));
                    GuiRenderUtils.drawRoundedRect(5, 5, FontManager.F13.getStringWidth(info) + 4, 2, 1, new Color(255, 191, 0).getRGB(), 1, new Color(255, 191, 0).getRGB());
                    FontManager.F13.drawStringWithShadow(info, 7, 10f, Colors.WHITE.c);
                }
                ArrayList<Module> sorted = new ArrayList<>();
                Client.instance.getModuleManager();
                for (Module m : ModuleManager.getModules()) {
                    if (!m.isEnabled() || m.wasRemoved()) continue;
                    sorted.add(m);
                }
                if (useFont) {
                    sorted.sort((o1, o2) -> FontManager.F16.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - FontManager.F16.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix())));
                } else {
                    sorted.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s %s", o2.getName(), o2.getSuffix())) - mc.fontRendererObj.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix())));
                }
                int y = 1;
                int rainbowTick = 0;
                if (useFont) {
                    for (Module m : sorted) {
                        name = m.getSuffix().isEmpty() ? m.getName() : String.format("%s %s", m.getName(), m.getSuffix());
                        float x = RenderUtil.width() - FontManager.F16.getStringWidth(name);
                        if (background.getValue()) {
                            RenderUtil.drawRect(x - 4.0f, y, new ScaledResolution(mc).getScaledWidth(), y + 9, new Color(1, 1, 1, 120).getRGB());
                        }
                        Color rainbow = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                        FontManager.F16.drawStringWithShadow(name, x - 3.0f, y + 1, this.rainbow.getValue() != false ? rainbow.getRGB() : m.getColor());
                        if (++rainbowTick > 50) {
                            rainbowTick = 0;
                        }
                        y += 9;
                    }
                } else {
                    for (Module m : sorted) {
                        name = m.getSuffix().isEmpty() ? m.getName() : String.format("%s %s", m.getName(), m.getSuffix());
                        float x = RenderUtil.width() - mc.fontRendererObj.getStringWidth(name);
                        if (background.getValue()) {
                            RenderUtil.drawRect(x - 4.0f, y, new ScaledResolution(mc).getScaledWidth(), y + 9, new Color(1, 1, 1, 120).getRGB());
                        }
                        Color rainbow = new Color(Color.HSBtoRGB((float) ((double) mc.thePlayer.ticksExisted / 50.0 + Math.sin((double) rainbowTick / 50.0 * 1.6)) % 1.0f, 0.5f, 1.0f));
                        mc.fontRendererObj.drawStringWithShadow(name, x - 2.0f, y, this.rainbow.getValue() != false ? rainbow.getRGB() : m.getColor());
                        if (++rainbowTick > 50) {
                            rainbowTick = 0;
                        }
                        y += 9;
                    }
                }
                String text = (Object) EnumChatFormatting.GRAY + "X" + (Object) ((Object) EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(mc.thePlayer.posX) + " " + (Object) ((Object) EnumChatFormatting.GRAY) + "Y" + (Object) ((Object) EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(mc.thePlayer.posY) + " " + (Object) ((Object) EnumChatFormatting.GRAY) + "Z" + (Object) ((Object) EnumChatFormatting.WHITE) + ": " + MathHelper.floor_double(mc.thePlayer.posZ);
                int ychat;
                int n = ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 24 : 10;
                if (this.info.getValue()) {
                    FontManager.F16.drawStringWithShadow(text, 4.0, new ScaledResolution(mc).getScaledHeight() - ychat, new Color(11, 12, 17).getRGB());
                    this.drawPotionStatus(new ScaledResolution(mc));
                }
            }
        } else if (mode.getValue() == HUDMode.Wurst) {

            // GL settings
            glEnable(GL_BLEND);
            glDisable(GL_CULL_FACE);
            glDisable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            RenderUtil.setColor(new Color(255, 255, 255, 128));

            // get version string
            String version = "v2.30";

            // draw version background
            glBegin(GL_QUADS);
            {
                glVertex2d(0, 6);
                glVertex2d(FontManager.F22.getStringWidth(version) + 78, 6);
                glVertex2d(FontManager.F22.getStringWidth(version) + 78, 18);
                glVertex2d(0, 18);
            }
            glEnd();

            // draw version string
            glEnable(GL_TEXTURE_2D);
            glEnable(GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            FontManager.F18.drawString(version, 74, 8, 0xFF000000);

            // mod list
            int yCount = 22;
            if (Helper.onServer("hypixel")) {
                String name =
                        "YesCheat+: Hypixel";
                FontManager.F18.drawString(name, 3, yCount + 1, 0xFF000000);
                FontManager.F18.drawString(name, 2, yCount, 0xFFFFFFFF);
                yCount += 9;
            }

            LinkedList<String> modList = new LinkedList<>();
            for (Module mod : ModuleManager.getModules()) {
                if (mod.isEnabled() && !mod.wasRemoved())
                    modList.add(mod.getName());
            }

            ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
            if (yCount + modList.size() * 9 > sr.getScaledHeight()) {
                String tooManyMods = "";
                if (modList.isEmpty())
                    return;
                else if (modList.size() > 1)
                    tooManyMods = modList.size() + " mods active";
                else
                    tooManyMods = "1 mod active";
                FontManager.F18.drawString(tooManyMods, 3, yCount + 1, 0xFF000000);
                FontManager.F18.drawString(tooManyMods, 2, yCount, 0xFFFFFFFF);
            } else
                for (String name; (name = modList.poll()) != null; ) {
                    FontManager.F18.drawString(name, 3, yCount + 1, 0xFF000000);
                    FontManager.F18.drawString(name, 2, yCount, 0xFFFFFFFF);
                    yCount += 9;
                }


            // Wurst logo
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(wurstLogo);
            int x = 0;
            int y = 3;
            int w = 72;
            int h = 18;
            float fw = 72;
            float fh = 18;
            float u = 0;
            float v = 0;
            Gui.drawModalRectWithCustomSizedTexture(x, y, u, v, w, h, fw, fh);
            // GL resets
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            // is this needed?
            GL11.glPushMatrix();
            GL11.glPopMatrix();
        } else if (mode.getValue() == HUDMode.Jigsaw) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.scale(0.425, 0.425, 1);
            GlStateManager.translate(4, -97, 0);
            mc.getTextureManager().bindTexture(jigsawTexture512);
            GlStateManager.color(0.8f, 0.3f, 0.3f);
            new Gui().drawTexturedModalRect(2, 2, 0, 0, 512 / 2, 512 / 2);
            GlStateManager.color(1f, 1f, 1f);
            new Gui().drawTexturedModalRect(0, 0, 0, 0, 512 / 2, 512 / 2);
            GlStateManager.popMatrix();
            GlStateManager.translate(0, 0, 1000);
            mc.fontRendererObj.drawString("You are using a dev version of Jigsaw " + "0.26", 5,
                    new ScaledResolution(mc).getScaledHeight() - 22, 0xffdddddd, true);
            mc.fontRendererObj.drawString("Please report bugs and crashes to @ReachSaw on Twitter!", 5, new ScaledResolution(mc).getScaledHeight() - 11,
                    0xffdddddd, true);
            GlStateManager.translate(0, 0, -1000);
            ArrayList<Module> sorted = new ArrayList<>();
            Client.instance.getModuleManager();
            for (Module m : ModuleManager.getModules()) {
                if (!m.isEnabled() || m.wasRemoved()) continue;
                sorted.add(m);
            }
            int y = 1;
            sorted.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2.getSuffix().isEmpty() ? o2.getName() : String.format("%s " + EnumChatFormatting.GRAY.toString() + " - [%s]", o2.getName(), o2.getSuffix())) - mc.fontRendererObj.getStringWidth(o1.getSuffix().isEmpty() ? o1.getName() : String.format("%s %s", o1.getName(), o1.getSuffix())));
            for (Module m : sorted) {
                name = m.getSuffix().isEmpty() ? m.getName() : String.format("%s " + EnumChatFormatting.GRAY.toString() + " - [%s]", m.getName(), m.getSuffix());
                float x = RenderUtil.width() - mc.fontRendererObj.getStringWidth(name);
                Color rainbow = new Color(255, 109, 109);
                RenderUtil.drawRect(x - 4.0f, y, new ScaledResolution(mc).getScaledWidth(), y + 9, new Color(1, 1, 1, 144).getRGB());
                mc.fontRendererObj.drawStringWithShadow(name, x - 2.0f, y, this.rainbow.getValue() != false ? rainbow.getRGB() : m.getColor());
                y += 9;
            }
        }
    }


    enum HUDMode {
        Foam,
        Wurst,
        Jigsaw
    }

    public enum Lang {
        English,
        文言
    }


    private void drawPotionStatus(ScaledResolution sr) {
        int y = 0;
        for (PotionEffect effect : mc.thePlayer.getActivePotionEffects()) {
            int ychat;
            Potion potion = Potion.potionTypes[effect.getPotionID()];
            String PType = I18n.format(potion.getName(), new Object[0]);
            switch (effect.getAmplifier()) {
                case 1: {
                    PType = String.valueOf(PType) + " II";
                    break;
                }
                case 2: {
                    PType = String.valueOf(PType) + " III";
                    break;
                }
                case 3: {
                    PType = String.valueOf(PType) + " IV";
                    break;
                }
            }
            if (effect.getDuration() < 600 && effect.getDuration() > 300) {
                PType = String.valueOf(PType) + "\u00a77:\u00a76 " + Potion.getDurationString(effect);
            } else if (effect.getDuration() < 300) {
                PType = String.valueOf(PType) + "\u00a77:\u00a7c " + Potion.getDurationString(effect);
            } else if (effect.getDuration() > 600) {
                PType = String.valueOf(PType) + "\u00a77:\u00a77 " + Potion.getDurationString(effect);
            }
            int n = ychat = mc.ingameGUI.getChatGUI().getChatOpen() ? 5 : -10;
            if (useFont) {
                FontManager.F16.drawStringWithShadow(PType, sr.getScaledWidth() - FontManager.F16.getStringWidth(PType) - 2, sr.getScaledHeight() - FontManager.F16.getHeight() + y - 12 - ychat, potion.getLiquidColor());
            } else {
                mc.fontRendererObj.drawStringWithShadow(PType, sr.getScaledWidth() - mc.fontRendererObj.getStringWidth(PType) - 2, sr.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT + y - 12 - ychat, potion.getLiquidColor());
            }
            y -= 10;
        }
    }

    @NMSL
    public void onRender(EventRenderCape event) {
        if (this.capes.getValue().booleanValue() && mc.theWorld != null && FriendManager.isFriend(event.getPlayer().getName())) {
            event.setLocation(Client.CLIENT_CAPE);
            event.setCancelled(true);
        }
    }
}

