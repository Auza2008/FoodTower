/*
Author:SuMuGod
Date:2022/7/10 5:24
Project:foodtower Reborn
*/
package me.dev.foodtower.ui.login;

import java.util.ArrayList;
import java.util.List;

public class AltManager {
    static List<Alt> alts;
    static Alt lastAlt;

    public static void init() {
        AltManager.setupAlts();
        AltManager.getAlts();
    }

    public static void setupAlts() {
        alts = new ArrayList<Alt>();
    }

    public static List<Alt> getAlts() {
        return alts;
    }

    public Alt getLastAlt() {
        return lastAlt;
    }

    public void setLastAlt(Alt alt) {
        lastAlt = alt;
    }
}
