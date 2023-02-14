/*
Author:SuMuGod
Date:2022/7/10 3:41
Project:foodtower Reborn
*/
package me.dev.foodtower.module;

public enum ModuleType {
    Combat("a"),
    Render("c"),
    Movement("b"),
    Player("d"),
    World("e"),
    Ghost("f");

    String icon;

    ModuleType(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }
}
