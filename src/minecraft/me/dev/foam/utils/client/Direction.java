/*
Author:SuMuGod
Date:2022/7/10 5:52
Project:foam Reborn
*/
package me.dev.foam.utils.client;

public enum Direction {
    FORWARDS,
    BACKWARDS;

    public Direction opposite() {
        if (this == Direction.FORWARDS) {
            return Direction.BACKWARDS;
        } else return Direction.FORWARDS;
    }

}
