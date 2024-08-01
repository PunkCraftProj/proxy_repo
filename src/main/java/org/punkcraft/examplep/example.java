package org.punkcraft.examplep;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.google.inject.Inject;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "example", name = "Example", version = "0.1-SNAPSHOT", dependencies = {})
public class example {
    @Inject
    public example(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
    }
}