/*
Author:SuMuGod
Date:2022/7/10 5:52
Project:foodtower Reborn
*/
package me.dev.foodtower.utils.client;

public enum Direction {
    FORWARDS,
    BACKWARDS;

    public Direction opposite() {
        if (this == Direction.FORWARDS) {
            return Direction.BACKWARDS;
        } else return Direction.FORWARDS;
    }

}
