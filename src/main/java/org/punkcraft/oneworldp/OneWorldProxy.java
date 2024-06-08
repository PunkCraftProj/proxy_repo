package org.punkcraft.oneworldp;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.ServerConnection;
import com.google.inject.Inject;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "oneworldproxy", name = "OneWorldProxy", version = "0.1-SNAPSHOT", dependencies = {})
public class OneWorldProxy {
    private final ProxyServer server;
    private final Logger logger;
    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.create("custom", "main");

    @Inject
    public OneWorldProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;

        server.getCommandManager().register("server", new MyCommand(this));
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(IDENTIFIER);
        logger.info("Channel registered: " + IDENTIFIER.getId());
    }

    @Subscribe
    public void onPluginMessageFromBackend(PluginMessageEvent event) {
        if (!(event.getSource() instanceof ServerConnection)) {
            return;
        }
        ServerConnection backend = (ServerConnection) event.getSource();

        // Ensure the identifier is what you expect before trying to handle the data
        if (!event.getIdentifier().equals(IDENTIFIER)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        // Process the received packet data
        String message = in.readUTF(); // Example of reading a UTF string from the packet
        logger.info("Received message from backend: " + message);
        // Add more handling of the data as needed
    }
}