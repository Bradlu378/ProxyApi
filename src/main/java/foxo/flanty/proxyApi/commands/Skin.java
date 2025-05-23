package foxo.flanty.proxyApi.commands;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.SkinRestorer.SRUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinVariant;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static foxo.flanty.proxyApi.settings.Config.miniMessage;
import static foxo.flanty.proxyApi.settings.Language.*;

public class Skin implements SimpleCommand {

    private static final List<String> SUBCOMMANDS = List.of("set");

    public enum SkinType {
        URL,
        NICKNAME
    }

    public static SkinType detectSkinType(String input) {
        if (input.startsWith("http://") || input.startsWith("https://")) {
            return SkinType.URL;
        } else {
            return SkinType.NICKNAME;
        }
    }


    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (!(invocation.source() instanceof Player player)) {
            invocation.source().sendMessage(miniMessage.deserialize("Только игроки могут использовать эту команду!"));
            return;
        }

        if (args.length == 1) {
            player.sendMessage(miniMessage.deserialize(wrongSkinCommand));
            return;
        }

        switch (detectSkinType(args[1])) {
            case URL -> {
                if (args.length != 3) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(wrongSkinCommand));
                    return;
                }
                if (!List.of("slim", "classic").contains(args[2].toLowerCase())) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(wrongCommandSkinUrlType));
                    return;
                }
                SRUtils.setSkin(args[1], player.getUsername(), SkinVariant.valueOf(args[2].toUpperCase()));
            }
            case NICKNAME -> {
                if (args.length != 2) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(wrongSkinCommand));
                    return;
                }
                try {
                    SRUtils.setSkin(args[1], player.getUsername());
                    invocation.source().sendMessage(miniMessage.deserialize(skinChangeSuccess));
                } catch (DataRequestException e) {
                    invocation.source().sendMessage(miniMessage.deserialize(skinChangeError));
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
                return CompletableFuture.completedFuture(List.of("url skinType", "nickname"));
            }
        }
        switch (detectSkinType(args[1])) {
            case URL -> {
                if (args.length == 2) return CompletableFuture.completedFuture(List.of("url skinType"));
                if (args.length == 3) return CompletableFuture.completedFuture(List.of("slim", "classic"));
            }
            case NICKNAME -> {
                if (args.length == 2) return CompletableFuture.completedFuture(List.of("nickname"));
            }
        }
        return CompletableFuture.completedFuture(List.of());
    }


    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source() instanceof Player;
    }
}
