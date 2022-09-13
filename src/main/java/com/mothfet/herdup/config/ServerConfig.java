package com.mothfet.herdup.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ServerConfig {

    public final ForgeConfigSpec.IntValue calfAgeInSeconds;
    public final ForgeConfigSpec.IntValue heiferInLoveInSeconds;

    ServerConfig(ForgeConfigSpec.Builder builder) {

        builder.push("Config");
        this.calfAgeInSeconds =
                builder
                        .comment("Specify how many seconds it takes for the calf to grow up. (Default is 1800 seconds = 30 minutes.)")
                        .defineInRange("calfAgeInSeconds", 1_800, 1, Integer.MAX_VALUE);
        this.heiferInLoveInSeconds =
                builder
                        .comment("Specify how many seconds it takes for the calf to grow up. (Default is 1800 seconds = 30 minutes.)")
                        .defineInRange("heiferInLoveInSeconds", 1_800, 1, Integer.MAX_VALUE);
        builder.pop();

    }
}
