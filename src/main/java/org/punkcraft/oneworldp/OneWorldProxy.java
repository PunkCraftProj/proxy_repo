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
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;

import java.nio.file.Path;
import java.util.Optional;
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

        if (!event.getIdentifier().equals(IDENTIFIER)) {
            return;
        }
        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        // Process the received packet data
        String message = in.readUTF();
        String[] parts = message.split(" ");
        String player_name = parts[0];
        String server_to = parts[1];

        Optional<Player> playerOptional = server.getPlayer(player_name);
        if (!playerOptional.isPresent()) {
            return;
        }

        Player player = playerOptional.get();
        String uuid = player.getUniqueId().toString();


        Optional<RegisteredServer> hubServerOptional = getServer().getServer("hub");
        Optional<RegisteredServer> targetServerOptional = getServer().getServer(server_to);

        if (hubServerOptional.isPresent() && targetServerOptional.isPresent()) {
            RegisteredServer hubServer = hubServerOptional.get();
            RegisteredServer targetServer = targetServerOptional.get();

            if (isCurrentServer(player, hubServer)) {
                connectToServer(player, targetServer);
            } else {
                player.createConnectionRequest(hubServer).connect().thenAccept(result -> {

                    if (result.isSuccessful()) {
                        connectToServer(player, targetServer);
                    } else {
                        player.sendMessage(Component.text("Не удалось подключиться к hub"));
                    }
                });
            }
        }
    }

    private boolean isCurrentServer(Player player, RegisteredServer server) {
        return player.getCurrentServer().map(current -> current.getServerInfo().equals(server.getServerInfo())).orElse(false);
    }

    private void connectToServer(Player player, RegisteredServer server) {
        player.createConnectionRequest(server).connect().thenAccept(result -> {
            if (result.isSuccessful()) {
                player.sendMessage(Component.text("Успешный вход на " + server.getServerInfo().getName()));
            } else {
                player.sendMessage(Component.text("Не удалось подключиться к " + server.getServerInfo().getName()));
            }
        });
    }
}