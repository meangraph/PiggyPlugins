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
public class OneClickCoinPouchOpenerPlugin extends Plugin {
    @Inject
    private OneClickCoinPouchOpenerConfig config;

    @Inject
    private Client client;

    @Inject
    private ConfigManager configManager;

    @Provides
    OneClickCoinPouchOpenerConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(OneClickCoinPouchOpenerConfig.class);
    }

    @Override
    protected void startUp() {}

    @Override
    protected void shutDown() {}

    private boolean clicked;

    @Subscribe
    private void onClientTick(ClientTick event) {
        if (client.getLocalPlayer() == null
                || client.getGameState() != GameState.LOGGED_IN
                || client.isMenuOpen()
                || client.getWidget(378, 78) != null) {
            return;
        }

        // If pouches are present and their count meets the criteria, and hasn't been clicked yet.
        if (hasMinimumPouches() && !clicked) {
            this.client.createMenuEntry(this.client.getMenuEntries().length)
                    .setOption("One Click Pouch Opener")
                    .setTarget("")
                    .setType(MenuAction.RUNELITE_HIGH_PRIORITY)
                    .setIdentifier(0)
                    .onClick((e) -> clicked = true);
        }
    }

    @Subscribe
    private void onMenuOptionClicked(MenuOptionClicked event) {
        if (event.getMenuOption().equals("One Click Pouch Opener")) {
            openPouches();
        }
    }

    @Subscribe
    private void onGameTick(GameTick event) {
        clicked = false;
    }

    private Optional<Widget> getCoinPouchWidget() {
        return Inventory.search().nameContains("Coin pouch").first();
    }

    private boolean hasMinimumPouches() {
        Optional<Widget> coinPouch = getCoinPouchWidget();

        if (coinPouch.isPresent()) {
            return coinPouch.get().getItemQuantity() >= config.minimumCoinPouch();
        }
        return false;
    }

    private void openPouches() {
        Optional<Widget> coinPouch = getCoinPouchWidget();

        if (coinPouch.isPresent()) {
            MousePackets.queueClickPacket();
            InventoryInteraction.useItem(coinPouch.get(), "Open-All");
            clicked = false;
        }
    }
}
