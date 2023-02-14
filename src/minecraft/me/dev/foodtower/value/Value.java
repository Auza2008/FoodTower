/*
Author:SuMuGod
Date:2022/7/10 3:43
Project:foodtower Reborn
*/
package me.dev.foodtower.value;

public abstract class Value<V> {
    private String displayName;
    private String name;
    private V value;

    public Value(String displayName, String name) {
        this.displayName = displayName;
        this.name = name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getName() {
        return this.name;
    }

    public V getValue() {
        return this.value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public boolean show() {
        return true;
    }

}
