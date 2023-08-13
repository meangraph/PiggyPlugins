package com.Swiper.OneClickRunEnable;

;
import com.ethan.Packets.MousePackets;
import com.ethan.Packets.WidgetPackets;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.awt.*;


@Extension
@PluginDescriptor(
        name = "One Click Run Enable",
        description = "Click anywhere to reenable run",
        tags = {"sundar", "pajeet"},
        enabledByDefault = false
)

@Slf4j
public class OneClickRunEnablePlugin extends Plugin
{
    @Inject
    private OneClickRunEnableConfig config;

    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Provides
    OneClickRunEnableConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(OneClickRunEnableConfig.class);
    }

    @Override
    protected void startUp()
    {
    }

    @Override
    protected void shutDown()
    {
    }

    boolean clicked;

    @Subscribe
    private void onClientTick(ClientTick event)
    {
        if (client.getLocalPlayer() == null
                || client.getGameState() != GameState.LOGGED_IN
                || client.isMenuOpen()
                || client.getWidget(378,78) != null)//login button
            return;

        if (client.getVarpValue(173) == 0 && client.getEnergy() >= Math.min(config.minimumRun(),100) && !clicked)
        {

            this.client.createMenuEntry(this.client.getMenuEntries().length)
                    .setOption("One Click Enable Run")
                    .setTarget("")
                    .setType(MenuAction.CC_OP)
                    .setIdentifier(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getId())
                    .onClick((e) -> {
                        toggleRunOrb();
                        clicked = true;
                    });

        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.getMenuOption().equals("One Click Enable Run"))
        {
            toggleRunOrb();
        }
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        clicked = false;
    }

    public void toggleRunOrb() {
        int packedRunOrbWidgetID = WidgetInfo.TO_GROUP(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getId()) << 16 | WidgetInfo.TO_CHILD(WidgetInfo.MINIMAP_TOGGLE_RUN_ORB.getId());
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetActionPacket(1, packedRunOrbWidgetID, -1, -1);
    }

}