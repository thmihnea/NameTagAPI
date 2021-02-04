package by.thmihnea.listener;

import by.thmihnea.INMSHandler;
import by.thmihnea.NameTagAPI;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    /**
     * Event called whenever a {@link org.bukkit.entity.Player} joins
     * the server. We make use of this event
     * in order to hook into the channel pipeline
     * and read packets being transmitted between the server
     * and the player.
     * @param e The event itself.
     */
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        NameTagAPI.getNMSHandler().startPipelineMonitoring(player);
        NameTagAPI.getInstance().logInfo("Began monitoring the pipeline for player " + player.getName() + ".");
    }

}
