package com.Swiper.OneClickCoinPouchOpener;

import com.Swiper.OneClickRunEnable.OneClickRunEnableConfig;
import com.ethan.EthanApiPlugin.Collections.Inventory;
import com.ethan.EthanApiPlugin.Collections.query.ItemQuery;
import com.ethan.InteractionApi.InventoryInteraction;
import com.ethan.Packets.MousePackets;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.pf4j.Extension;

import javax.inject.Inject;
import java.util.Optional;

@Extension
@PluginDescriptor(
        name = "One Click Coin Pouch Opener",
        description = "Click anywhere to open coin pouches",
        tags = {"swiper", "Swiper"},
        enabledByDefault = false
)

@Slf4j
public class OneClickCoinPouchOpenerPlugin extends Plugin
{
    @Inject
    private OneClickCoinPouchOpenerConfig config;

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

        if (hasPouch() && !clicked && getPouchCount());
        {

            this.client.createMenuEntry(this.client.getMenuEntries().length)
                    .setOption("One Click Pouch Opener")
                    .setTarget("")
                    .setType(MenuAction.CC_OP)
                    .setIdentifier(0)
                    .onClick((e) -> {
                        clicked = true;
                    });

        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event)
    {
        if (event.getMenuOption().equals("One Click Pouch Opener"))
        {
            openPouches();
        }
    }

    public boolean hasPouch() {
        return !Inventory.search().nameContains("Coin pouch").empty();
    }

    public boolean getPouchCount() {
        boolean y = Inventory.search().nameContains("Coin pouch").quantityGreaterThan(config.minimumCoinPouch()-1).empty();
        return y;
    }

    @Subscribe
    private void onGameTick(GameTick event)
    {
        clicked = false;
    }

    public void openPouches() {
        Optional<Widget> coinPouch = Inventory.search().nameContains("Coin pouch").first();

        if (coinPouch.isPresent()) {
            MousePackets.queueClickPacket();
            InventoryInteraction.useItem(coinPouch.get(), "Open-All");
        }
    }

}
