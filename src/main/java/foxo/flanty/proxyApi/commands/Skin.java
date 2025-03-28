package foxo.flanty.proxyApi.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.utils.SRUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinVariant;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Skin implements SimpleCommand {

    private static final List<String> SUBCOMMANDS = List.of("set");
    public enum SkinType {
        URL, ID, NICKNAME
    }
    public static SkinType detectSkinType(String input) {
        if (input.startsWith("http://") || input.startsWith("https://")) {
            return SkinType.URL;
        } else if (input.matches("\\d+")) {
            return SkinType.ID;
        } else {
            return SkinType.NICKNAME;
        }
    }


    @Override
    public void execute(Invocation invocation) {
        MiniMessage miniMessage = MiniMessage.miniMessage();
        String[] args = invocation.arguments();
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(miniMessage.deserialize("Только игроки могут использовать эту команду!"));//todo lang
            return;
        }

        if (args.length == 1) {
            player.sendMessage(miniMessage.deserialize("Использование: /skin set <id/url/nickname>"));//todo lang
            return;
        }

        switch (detectSkinType(args[1])) {
            case ID-> {
                if (args.length != 2) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("Неверная команда"));//todo lang
                    return;
                }
                //todo skin set by id

            }
            case URL-> {
                if (args.length != 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("Неверная команда"));//todo lang
                    return;
                }
                if (!List.of("slim", "classic").contains(args[1].toLowerCase())) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("Неверный тип скина"));//todo lang
                    return;
                }
                SRUtils.setSkin(args[2], player.getUsername(), SkinVariant.SLIM, false);
            }
            case NICKNAME-> {
                if (args.length != 2) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("Неверная команда"));//todo lang
                    return;
                }
                try {
                    SRUtils.setSkin(args[1], player.getUsername(), false);
                } catch (DataRequestException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments();

        if (args.length <= 1) {
            return CompletableFuture.completedFuture(SUBCOMMANDS);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            if (args[1].isEmpty()) {
                return CompletableFuture.completedFuture(List.of("<url> <skinType>", "<id>", "<nickname>"));
            }
        }
        switch (detectSkinType(args[1])) {
            case URL -> {
                if (args.length == 2) return CompletableFuture.completedFuture(List.of("<url> <skinType>"));
                if (args.length == 3) return CompletableFuture.completedFuture(List.of("<slim/classic>"));
            }
            case ID -> {
                if (args.length == 2) return CompletableFuture.completedFuture(List.of("<id>"));
            }
            case NICKNAME -> {
                if (args.length == 2) return CompletableFuture.completedFuture(List.of("<nickname>"));
            }
        }
        return CompletableFuture.completedFuture(List.of());
    }


    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source() instanceof Player;
    }
}
