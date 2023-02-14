/*
Author:SuMuGod
Date:2022/7/10 5:25
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.login;

import me.dev.foodtower.ui.font.FontManager;
import me.ratsiel.auth.model.mojang.MinecraftAuthenticator;
import me.ratsiel.auth.model.mojang.MinecraftToken;
import me.ratsiel.auth.model.mojang.profile.MinecraftProfile;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Session;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.IOException;

public class GuiMicrosoftLogin
        extends GuiScreen {
    private final GuiScreen previousScreen;
    String status;
    private GuiTextField password;
    private AltLoginThread thread;
    private GuiTextField username;
    private GuiTextField combined;

    public GuiMicrosoftLogin(GuiScreen previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: {
                this.mc.displayGuiScreen(this.previousScreen);
                break;
            }
            case 0: {
                final MinecraftAuthenticator minecraftAuthenticator = new MinecraftAuthenticator();

                try {
                    if (this.combined.getText().isEmpty()) {
                        final MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox(username.getText(), password.getText());
                        final MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
                        status = EnumChatFormatting.GREEN + "Login as " + minecraftProfile.getUsername();
                        mc.session = new Session(minecraftProfile.getUsername(), minecraftProfile.getUuid().toString(), minecraftToken.getAccessToken(), "mojang");
                    } else if (!this.combined.getText().isEmpty() && this.combined.getText().contains(":")) {
                        String u = this.combined.getText().split(":")[0];
                        String p = this.combined.getText().split(":")[1];
                        final MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox(u.replaceAll(" ", ""), p.replaceAll(" ", ""));
                        final MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
                        status = EnumChatFormatting.GREEN + "Login as " + minecraftProfile.getUsername();
                        mc.session = new Session(minecraftProfile.getUsername(), minecraftProfile.getUuid().toString(), minecraftToken.getAccessToken(), "mojang");
                    } else {
                        final MinecraftToken minecraftToken = minecraftAuthenticator.loginWithXbox(username.getText(), password.getText());
                        final MinecraftProfile minecraftProfile = minecraftAuthenticator.checkOwnership(minecraftToken);
                        status = EnumChatFormatting.GREEN + "Login as " + minecraftProfile.getUsername();
                        mc.session = new Session(minecraftProfile.getUsername(), minecraftProfile.getUuid().toString(), minecraftToken.getAccessToken(), "mojang");
                    }
                } catch (NullPointerException exception) {
                    status = EnumChatFormatting.RED + "Login failed!";
                }
//                status = EnumChatFormatting.RED + "You must choose one type of login!";
            }
        }
    }

    @Override
    public void drawScreen(int x, int y, float z) {
        ScaledResolution sr = new ScaledResolution(mc);
        drawRect(0, 0, sr.getScaledWidth(), sr.getScaledHeight(), new Color(26, 26, 26).getRGB());
        this.username.drawTextBox();
        this.password.drawTextBox();
        this.combined.drawTextBox();
        FontManager.F18.drawCenteredString("Microsoft Login", this.width / 2, 20, -1);
        FontManager.F18.drawCenteredString(status, this.width / 2, 29, -1);
        if (this.username.getText().isEmpty()) {
            FontManager.F18.drawStringWithShadow("E-Mail", this.width / 2 - 96, 66.0f, -7829368);
        }
        if (this.password.getText().isEmpty()) {
            FontManager.F18.drawStringWithShadow("Password", this.width / 2 - 96, 106.0f, -7829368);
        }
        if (this.combined.getText().isEmpty()) {
            FontManager.F18.drawStringWithShadow("Email:Password", this.width / 2 - 96, 146.0f, -7829368);
        }
        super.drawScreen(x, y, z);
    }

    @Override
    public void initGui() {
        int var3 = this.height / 4 + 24;
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, var3 + 72 + 12, "Login"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, var3 + 72 + 12 + 24, "Back"));
        this.username = new GuiTextField(1, this.mc.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.password = new GuiTextField(2, this.mc.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
        this.combined = new GuiTextField(var3, this.mc.fontRendererObj, this.width / 2 - 100, 140, 200, 20);
        this.username.setFocused(true);
        this.username.setMaxStringLength(200);
        this.password.setMaxStringLength(200);
        this.combined.setMaxStringLength(200);
        Keyboard.enableRepeatEvents((boolean) true);
    }

    @Override
    protected void keyTyped(char character, int key) {
        try {
            super.keyTyped(character, key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (character == '\t' && (this.username.isFocused() || this.combined.isFocused() || this.password.isFocused())) {
            this.username.setFocused(this.combined.isFocused());
            this.password.setFocused(this.username.isFocused());
            this.combined.setFocused(this.password.isFocused());
        }
        if (character == '\r') {
            this.actionPerformed((GuiButton) this.buttonList.get(0));
        }
        this.username.textboxKeyTyped(character, key);
        this.password.textboxKeyTyped(character, key);
        this.combined.textboxKeyTyped(character, key);
    }

    @Override
    protected void mouseClicked(int x, int y, int button) {
        try {
            super.mouseClicked(x, y, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.username.mouseClicked(x, y, button);
        this.password.mouseClicked(x, y, button);
        this.combined.mouseClicked(x, y, button);
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents((boolean) false);
    }

    @Override
    public void updateScreen() {
        this.username.updateCursorCounter();
        this.password.updateCursorCounter();
        this.combined.updateCursorCounter();
    }
}

