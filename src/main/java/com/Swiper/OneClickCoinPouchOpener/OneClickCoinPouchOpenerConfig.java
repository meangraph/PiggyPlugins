package com.Swiper.OneClickCoinPouchOpener;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("oneclickcoinpouchopener")
public interface OneClickCoinPouchOpenerConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "minimumCoinPouch",
            name = "Maximum Coin Pouches you can open",
            description = "This is the maximum amount of coin pouches you can open"
    )
    default int minimumCoinPouch()
    {
        return 15;
    }
}