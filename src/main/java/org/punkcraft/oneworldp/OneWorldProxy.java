package org.punkcraft.oneworldp;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.google.inject.Inject;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "oneworldproxy", name = "OneWorldProxy", version = "0.1-SNAPSHOT", dependencies = {})
public class OneWorldProxy {
    private final ProxyServer server;
    private final Logger logger;

    @Inject
    public OneWorldProxy(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;

        // Register commands
        server.getEventManager().register(this, this);
        server.getCommandManager().register("server", new MyCommand(this));
    }

    public ProxyServer getServer() {
        return server;
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent event) {
        if (event.getIdentifier().getId().equals("velocity:command")) {
            byte[] data = event.getData();
            String command = new String(data, StandardCharsets.UTF_8);

            if (event.getSource() instanceof Player) {
                Player player = (Player) event.getSource();
                if ("server".equals(command)) {
                    server.getCommandManager().executeAsync(player, "/server");
                }
            }
        }
    }

    public Logger getLogger() {
        return logger;
    }
}