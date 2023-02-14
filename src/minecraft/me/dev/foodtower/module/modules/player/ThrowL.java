/*
Author:SuMuGod
Date:2022/7/10 4:46
Project:foodtower Reborn
*/
package me.dev.foodtower.module.modules.player;

import me.dev.foodtower.api.NMSL;
import me.dev.foodtower.api.events.EventKey;
import me.dev.foodtower.module.Module;
import me.dev.foodtower.module.ModuleType;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ThrowL extends Module {
    private final ArrayList<String> LMessagesCN = new ArrayList<>();

    public ThrowL() {
        super("ThrowL", "抛L", new String[]{"ltap", "l"}, ModuleType.Player);
        this.noToggle = true;
    }

    @Override
    public void onEnable() {
        if (LMessagesCN.size() < 2) {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("assets/minecraft/caonima/L.txt"))));
                String line = "";
                while (true) {
                    try {
                        if ((line = in.readLine()) == null) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    LMessagesCN.add(line);
                }

            } catch (NullPointerException nullPointerException) {
                //
            }
        }
    }

    @NMSL
    public void onKey(EventKey eventKey) {
        if (eventKey.getKey() == Keyboard.KEY_L && Minecraft.currentScreen == null) {
            String L = LMessagesCN.get(new Random().nextInt(LMessagesCN.size()));
            mc.thePlayer.sendChatMessage(L);
        }
    }
}

