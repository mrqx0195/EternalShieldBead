package net.mrqx.protector;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLACKLIST;

    static {
        ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
        commonBuilder.push("EternalShieldBead settings");
        BLACKLIST = commonBuilder
                .comment("Set the blacklist of Eternal Shield Bead.")
                .defineListAllowEmpty("black_list", List.of("minecraft:wither", "minecraft:ender_dragon", "minecraft:player"), str -> str instanceof String);

        commonBuilder.pop();
        COMMON_CONFIG = commonBuilder.build();
    }
}
