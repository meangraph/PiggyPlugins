package com.example;

import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginInstantiationException;
import net.runelite.client.plugins.PluginManager;
import net.runelite.client.util.HotkeyListener;

import javax.inject.Inject;
import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

@Slf4j
@Singleton
@PluginDescriptor(
        name = "Packet Utils",
        description = "Packet Utils for Plugins",
        enabledByDefault = true,
        tags = {"ethan"}
)
public class PacketUtilsPlugin extends Plugin {
    @Inject
    PacketUtilsConfig config;
    @Inject
    ClientThread clientThread;
    @Inject
    Client client;
    @Inject
    WidgetPackets widgetPacket;
    @Inject
    PacketReflection packetReflection;
    @Inject
    ClientThread thread;
    @Inject
    MousePackets mousePackets;
    @Inject
    private KeyManager keyManager;
    @Inject
    MovementPackets movementPackets;
    public static final int CLIENT_REV = 211;
    private static boolean loaded = false;
    @Inject
    private PluginManager pluginManager;

    @Subscribe
    public void onGameStateChanged(GameStateChanged event) {
        if (event.getGameState() == GameState.LOGGED_IN && !loaded) {
            loaded = packetReflection.LoadPackets();
        }
        if (event.getGameState() == GameState.LOGIN_SCREEN) {
            loaded = false;
        }
        if (event.getGameState() == GameState.HOPPING) {
            loaded = false;
        }
        if (event.getGameState() == GameState.CONNECTION_LOST) {
            loaded = false;
        }
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Provides
    public PacketUtilsConfig getConfig(ConfigManager configManager) {
        return configManager.getConfig(PacketUtilsConfig.class);
    }

    @Override
    @SneakyThrows
    public void startUp() {
        if (client.getRevision() != CLIENT_REV) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "PacketUtils not updated for this rev please " +
                        "wait for " +
                        "plugin update");
                try {
                    pluginManager.setPluginEnabled(this, false);
                    pluginManager.stopPlugin(this);
                    keyManager.registerKeyListener(prayerToggle);
                } catch (PluginInstantiationException ignored) {
                }
            });
            return;
        }
        thread.invoke(() ->
        {
            if (client.getGameState() != null && client.getGameState() == GameState.LOGGED_IN) {
                loaded = packetReflection.LoadPackets();
            }
        });
    }
    private final HotkeyListener prayerToggle = new HotkeyListener(() -> config.toggle()) {
        @Override
        public void hotkeyPressed() {
            System.out.println("hotkey pressed");
            clientThread.invoke(() -> {
                if (client.getGameState() != GameState.LOGGED_IN) {
                    return;
                }
                System.out.println("queueing click");
                mousePackets.queueClickPacket();
                System.out.println("queueing movement");
                movementPackets.queueMovement(client.getLocalPlayer().getWorldLocation().dx(1).dy(1));
            });
        }
    };
    @Subscribe
    public void onGameTick(GameTick event) throws NoSuchFieldException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (client.getGameState() != GameState.LOGGED_IN) {
            return;
        }
    }

    @Override
    public void shutDown() {
        log.info("Shutdown");
        loaded = false;
        keyManager.unregisterKeyListener(prayerToggle);
    }
}
