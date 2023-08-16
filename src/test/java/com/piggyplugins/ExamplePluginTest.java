package com.piggyplugins;

import com.Swiper.CoxHelper.CoxPlugin;
import com.Swiper.LizardmanShaman.LizardmanShamanPlugin;
import com.Swiper.OneClickRunEnable.OneClickRunEnablePlugin;
import com.ethan.EthanApiPlugin.EthanApiPlugin;
import com.ethan.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,
                //new stuff
                CoxPlugin.class, LizardmanShamanPlugin.class, OneClickRunEnablePlugin.class);
        RuneLite.main(args);
    }
}