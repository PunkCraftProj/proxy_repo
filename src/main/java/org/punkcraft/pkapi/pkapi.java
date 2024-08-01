package org.punkcraft.pkapi;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.google.inject.Inject;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(id = "pkapi", name = "PKAPI", version = "0.1-SNAPSHOT", dependencies = {})
public class pkapi {

    @Inject
    public pkapi(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
    }
}