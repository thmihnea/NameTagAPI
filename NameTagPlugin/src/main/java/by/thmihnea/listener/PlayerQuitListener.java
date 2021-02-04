package by.thmihnea.listener;

import by.thmihnea.NameTagAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    /**
     * Event called whenever a player leaves the
     * server. We use this event to abort the by.thmihnea.nms.v1_16_R2.by.thmihnea.nms.v1_16_R2.connection
     * to the channel pipeline which monitors the packets
     * being sent between the server and the player.
     * We use this to prevent server overload.
     * @param e The event itself.
     */
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        NameTagAPI.getNMSHandler().abortPipelineMonitoring(player);
        NameTagAPI.getInstance().logInfo("Player " + player.getName() + " has left. Aborting io.netty pipeline monitoring.");
    }
}
