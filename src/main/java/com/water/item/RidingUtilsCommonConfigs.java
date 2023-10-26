package com.water.item;

import net.minecraftforge.common.ForgeConfigSpec;

public class RidingUtilsCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.DoubleValue reinsJumpHeight;

    static {
        BUILDER.push("Configs for Riding Utilities");

        reinsJumpHeight = BUILDER.comment("How high do mobs jump when using reins? (Defaults to 0.5)")
                .defineInRange("Reins Jump Height", 0.5, 0.1, 2.0);


        BUILDER.pop();
        SPEC = BUILDER.build();

    }

}