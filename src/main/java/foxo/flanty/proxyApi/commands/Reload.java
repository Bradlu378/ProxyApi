package foxo.flanty.proxyApi.commands;

import com.velocitypowered.api.command.SimpleCommand;
import foxo.flanty.proxyApi.settings.Config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Reload implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {
        try {
            Config.proxy.reload();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
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
