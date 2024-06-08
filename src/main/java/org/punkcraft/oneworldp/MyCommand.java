
package org.punkcraft.oneworldp;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MyCommand implements SimpleCommand {
    private final OneWorldProxy plugin;

    public MyCommand(OneWorldProxy plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if (!(source instanceof Player)) {
            source.sendMessage(Component.text("This command can only be used by players."));
            return;
        }

        Player player = (Player) source;
        String[] args = invocation.arguments();

        String currentServerName = player.getCurrentServer()
                .map(server -> server.getServerInfo().getName())
                .orElse("unknown");

        if (args.length > 0) {
            String serverName = args[0];
            if (serverName.equalsIgnoreCase(currentServerName)) {
                source.sendMessage(Component.text("Ты уже на сервере " + currentServerName));
                return;
            }

            Optional<RegisteredServer> hubServerOptional = plugin.getServer().getServer("hub");
            Optional<RegisteredServer> targetServerOptional = plugin.getServer().getServer(serverName);

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
            } else {
                player.sendMessage(Component.text("Hub или целевой сервер не найден"));
            }
        } else {
            // Список доступных серверов и выделение текущего сервера
            CompletableFuture.runAsync(() -> {
                ArrayList<RegisteredServer> sortedServers = new ArrayList<>(plugin.getServer().getAllServers());
                sortedServers.sort(Comparator.comparing(server -> server.getServerInfo().getName()));

                // Порядок отображения: server1, server2, hub_server
                Component serverList = Component.join(
                    JoinConfiguration.separator(Component.text(", ").color(TextColor.color(0xaaaaaa))),
                    sortedServers.stream()
                        .map(server -> {
                            String name = server.getServerInfo().getName();
                            int onlinePlayers = server.getPlayersConnected().size();

                            // Определяем окончание слова "игрок"
                            String playerSuffix = onlinePlayers == 1 ? "" : (onlinePlayers > 4 || onlinePlayers == 0 ? "ов" : "а");

                            Component hoverText = Component.text("Кликните, чтобы присоединиться к этому серверу\n")
                                .append(Component.text(onlinePlayers + " игрок" + playerSuffix + " онлайн"));

                            return Component.text(name)
                                .color(name.equalsIgnoreCase(currentServerName) ? TextColor.color(0x55ff55) : TextColor.color(0xaaaaaa))
                                .hoverEvent(HoverEvent.showText(hoverText))
                                .clickEvent(ClickEvent.runCommand("/server " + name));
                        })
                        .collect(Collectors.toList())
                );

                player.sendMessage(Component.text("На данный момент вы подключены к серверу " + currentServerName + "\nДоступные серверы: ")
                        .color(TextColor.color(0xffff55))
                        .append(serverList));
            });
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
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

    private boolean isCurrentServer(Player player, RegisteredServer server) {
        return player.getCurrentServer().map(current -> current.getServerInfo().equals(server.getServerInfo())).orElse(false);
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length == 1) {
            String partialServer = args[0].toLowerCase();
            return plugin.getServer().getAllServers().stream()
                    .map(server -> server.getServerInfo().getName())
                    .filter(name -> name.toLowerCase().startsWith(partialServer))
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
