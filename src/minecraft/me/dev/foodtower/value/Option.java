/*
Author:SuMuGod
Date:2022/7/10 3:43
Project:foodtower Reborn
*/
package me.dev.foodtower.value;

public class Option<V>
        extends Value<V> {
    public Option(String displayName, String name, V enabled) {
        super(displayName, name);
        this.setValue(enabled);
    }
}
