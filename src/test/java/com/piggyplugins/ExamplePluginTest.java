package com.piggyplugins;

import com.Swiper.CoxHelper.CoxPlugin;
import com.Swiper.OneClickRunEnable.OneClickRunEnablePlugin;
import com.example.EthanApiPlugin.EthanApiPlugin;
import com.example.PacketUtils.PacketUtilsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class ExamplePluginTest {
    public static void main(String[] args) throws Exception {
        ExternalPluginManager.loadBuiltin(EthanApiPlugin.class, PacketUtilsPlugin.class,
                //new stuff
                CoxPlugin.class);
        RuneLite.main(args);
    }
}