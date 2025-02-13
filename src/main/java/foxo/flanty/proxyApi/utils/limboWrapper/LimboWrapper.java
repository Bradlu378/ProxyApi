package foxo.flanty.proxyApi.utils.limboWrapper;

import net.elytrium.limboapi.api.Limbo;
import net.elytrium.limboapi.api.LimboSessionHandler;
import net.elytrium.limboapi.api.player.LimboPlayer;

public abstract class LimboWrapper implements LimboSessionHandler {
    protected final WrapperMode mode;

    public LimboWrapper(WrapperMode mode) {
        this.mode = mode;
    }
    @Override
    public void onChat(String message) {
        String[] args;
        if (message.startsWith("/")) args = message.split(" ");

        if (!handleChat(message)) {
            System.out.println("[Limbo] Неизвестная команда: " + message);
        }
    }

    @Override
    public void onSpawn(Limbo server, LimboPlayer player) {
        System.out.println("[Limbo] Игрок зашёл в лобби: " + player.getUsername());
    }

    @Override
    public void onDisconnect() {
        System.out.println("[Limbo] Игрок отключился.");
    }

    @Override
    public void onMove(double posX, double posY, double posZ, float yaw, float pitch) {

    }
    @Override
    public void onGeneric(Object packet) {

    }


    protected abstract void onSpawn(Limbo server, LimboPlayer player);
    protected abstract void onMessage(String message, String[] args);
    protected abstract void onMove(double posX, double posY, double posZ, float yaw, float pitch);
    protected abstract void onGeneric(Object packet);
    protected abstract void onDisconnect();

}
