package foxo.flanty.proxyApi.handlers.wrapper;

import com.velocitypowered.api.proxy.Player;
import foxo.flanty.proxyApi.settings.Config;
import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;
import org.slf4j.Logger;

import java.util.Stack;
import java.util.concurrent.ScheduledFuture;

public abstract class LimboWrapper implements LimboSessionHandler {

    boolean disconnectReason = false;
    Player player;
    public Stack<ScheduledFuture<?>> tasks = new Stack<>();

    @Override
    public void onChat(String message) {
        String[] args;
        boolean isCommand = false;
        if (message.startsWith("/")){
            args = message.substring(1).split(" ");
            isCommand = true;
        }
        else args = message.split(" ");

        handleChat(message, args, isCommand);
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer limboPlayer) {
        this.player = limboPlayer.getProxyPlayer();
        handleSpawn(server,limboPlayer,player, Config.logger);
    }

    @Override
    public void onDisconnect() {
        handleDisconnect();
    }

    @Override
    public void onMove(double posX, double posY, double posZ, float yaw, float pitch) {
        handleMove(posX,posY,posZ,yaw,pitch);
    }
    @Override
    public void onGeneric(Object packet) {
        handleGeneric(packet);
    }


    public abstract void handleSpawn(Limbo world, LimboPlayer limboPlayer, Player player, Logger logger);
    public abstract void handleChat(String message, String[] args, boolean isCommand);
    public abstract void handleMove(double posX, double posY, double posZ, float yaw, float pitch);
    public abstract void handleGeneric(Object packet);
    public abstract void handleDisconnect();
}
