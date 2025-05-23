package foxo.flanty.proxyApi.commands;

import com.velocitypowered.api.command.SimpleCommand;
import foxo.flanty.proxyApi.settings.Language;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static foxo.flanty.proxyApi.settings.Config.*;

public class Reload implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        try {
            proxy.reload();
            invocation.source().sendMessage(miniMessage.deserialize(Language.reloadMessage));
        } catch (IOException | InterruptedException e) {
            logger.error(e.getMessage());
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
