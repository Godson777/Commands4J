package com.darichey.discord.api;

public enum CommandCategory {

    ADMIN("Administration"), MEME("Memes <Requires \"Living Meme\" role!>"), FUN("Fun"), GENERAL("General"), BOT_OWNER("Owner");

    private String name;

    CommandCategory(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
