package foxo.flanty.proxyApi.commands;

import com.velocitypowered.api.command.SimpleCommand;
import foxo.flanty.proxyApi.settings.Config;
import foxo.flanty.proxyApi.settings.Language;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Reload implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        try {
            Config.proxy.reload();
            invocation.source().sendMessage(MiniMessage.miniMessage().deserialize(Language.reloadMessage));
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return SimpleCommand.super.hasPermission(invocation);
    }
}
