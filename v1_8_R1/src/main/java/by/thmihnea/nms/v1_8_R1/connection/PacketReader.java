package by.thmihnea.nms.v1_8_R1.connection;

import io.netty.channel.*;
import net.minecraft.server.v1_8_R1.NetworkManager;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class PacketReader {

    /**
     * Begin monitoring the pipeline (packets)
     * being sent and received by a certain {@link Player}.
     * This method is being used to monitor whenever the player
     * receives a {@link net.minecraft.server.v1_8_R2.PacketPlayOutEntityDestroy} packet
     * so that we can stop destroying fake entities when players
     * are too far away.
     * @param player Player to begin pipeline monitoring
     *               for.
     */
    public static void startPipelineMonitoring(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) {
                try {
                    super.channelRead(channelHandlerContext, packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) {
                try {
                    super.write(channelHandlerContext, packet, channelPromise);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        ChannelPipeline channelPipeline = getChannel(player).pipeline();
        channelPipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
    }

    /**
     * Abort monitoring the channel
     * pipeline for a specific player.
     * Used generally whenever the player leaves the
     * server.
     * @param player Player to abort pipeline monitoring
     *               for.
     */
    public static void abortPipelineMonitoring(Player player) {
        Channel channel = getChannel(player);
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    /**
     * Reflection method to access the channel
     * as for some reason, in v1_8_R1, the channel
     * field in {@link NetworkManager} has private
     * access.
     * @param player Player we want to get the io.netty
     *               channel for.
     * @return {@link Channel}
     */
    public static Channel getChannel(Player player) {
        NetworkManager networkManager = ((CraftPlayer) player).getHandle().playerConnection.networkManager;
        Class<?> clazz = networkManager.getClass();
        try {
            Field f = clazz.getDeclaredField("i");
            f.setAccessible(true);
            return (Channel) f.get(networkManager);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

}
