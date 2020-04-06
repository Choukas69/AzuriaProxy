package fr.choukas.azuria.azuria_proxy.api.players;

import com.google.gson.Gson;
import fr.choukas.azuria.azuria_common.players.AbstractPlayer;
import fr.choukas.azuria.azuria_common.players.AbstractPlayerManager;
import fr.choukas.azuria.azuria_proxy.AzuriaProxy;
import fr.choukas.azuria.azuria_proxy.api.utils.BungeeRunnable;
import fr.choukas.azuria.persistence.beans.PlayerBean;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class PlayerManager extends AbstractPlayerManager {

    private AzuriaProxy proxy;

    public PlayerManager(AzuriaProxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void loadPlayer(UUID uuid) {
        DataPlayer player;
        player = new Gson().fromJson(proxy.getRedisManager().getPlayersManager().getPlayer(uuid), DataPlayer.class);

        if (player == null) {
            AzuriaProxy.log("No player with uuid " + uuid + " was found in cache, load it from database");
            // If player isn't in cache, then load it from database
            PlayerBean bean = proxy.getDataService().getPlayersManager().loadPlayer(uuid);
            player = new DataPlayer(bean);

            // And save it in the cache
            proxy.getRedisManager().getPlayersManager().savePlayer(player);
        }

        this.players.put(uuid, new DataPlayer(player.getBean()));
    }

    @Override
    public void unloadPlayer(UUID uuid) {
        AbstractPlayer player = this.players.remove(uuid);

        // Keep player data in cache for 5 minutes
        proxy.getSchedulerManager().runRepeatingTask(new BungeeRunnable() {
            private long timer = 5 * 60;

            @Override
            public void run() {
                timer--;

                if (players.containsValue(player)) {
                    this.cancel();
                }

                if (timer == 0) {
                    // If player hasn't reconnected, then erase their data from cache
                    proxy.getRedisManager().getPlayersManager().deletePlayer(uuid);
                }
            }
        }, 5 * 60, 1, TimeUnit.SECONDS);
    }
}
