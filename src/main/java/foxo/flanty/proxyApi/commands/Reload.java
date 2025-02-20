package foxo.flanty.proxyApi.commands;

import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Reload implements SimpleCommand {

    @Override
    public void execute(Invocation invocation) {

    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        return SimpleCommand.super.suggestAsync(invocation);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        invocation
        return SimpleCommand.super.hasPermission(invocation);
    }
}
