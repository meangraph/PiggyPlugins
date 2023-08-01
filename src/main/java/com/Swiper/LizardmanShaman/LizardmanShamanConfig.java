package com.Swiper.LizardmanShaman;


import lombok.RequiredArgsConstructor;
import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("lizardmanshaman")
public interface LizardmanShamanConfig extends Config
{
    @RequiredArgsConstructor
    enum SpawnOverlayConfig
    {
        EXPLOSION_ONLY("Explosion Only"),
        ALWAYS("Always"),
        DISABLED("Disabled");

        private final String name;

        @Override
        public String toString()
        {
            return name;
        }
    }


    @ConfigItem(
            keyName = "spawnOverlay",
            name = "Spawn Overlay",
            description = "Show an overlay for Spawn's explosion tiles.",
            section = "features",
            position = 1
    )
    default SpawnOverlayConfig showSpawnOverlay()
    {
        return SpawnOverlayConfig.ALWAYS;
    }

    @ConfigSection(
            name = "Colors",
            description = "",
            position = 0
    )
    String colorsSection = "colors";

    @Alpha
    @ConfigItem(
            keyName = "explosionBorderColor",
            name = "Explosion Border",
            description = "Spawn explosion tiles overlay border.",
            section = "colors",
            position = 1
    )
    default Color explosionBorderColor()
    {
        return Color.RED;
    }

    @Alpha
    @ConfigItem(
            keyName = "explosionFillColor",
            name = "Explosion Fill",
            description = "Spawn explosion tiles overlay fill.",
            section = "colors",
            position = 2
    )
    default Color explosionFillColor()
    {
        return new Color(255, 0, 0, 20);
    }

    @Alpha
    @ConfigItem(
            keyName = "spawnWalkableBorderColor",
            name = "Walkable Border",
            description = "Spawn walkable tiles overlay border.",
            section = "colors",
            position = 3
    )
    default Color spawnWalkableBorderColor()
    {
        return Color.ORANGE;
    }

    @Alpha
    @ConfigItem(
            keyName = "spawnWalkableFillColor",
            name = "Walkable Fill",
            description = "Spawn walkable tiles overlay fill.",
            section = "colors",
            position = 4
    )
    default Color spawnWalkableFillColor()
    {
        return new Color(255, 165, 0, 20);
    }
}