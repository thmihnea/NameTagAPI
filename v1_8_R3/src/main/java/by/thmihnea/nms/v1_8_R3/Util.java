/*
Copyright (c) thmihnea <mihneathm@gmail.com>
Copyright (c) contributors

This file is part of NameTagAPI, licensed under the MIT License.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE..
 */

package by.thmihnea.nms.v1_8_R3;

import by.thmihnea.cache.CacheManager;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Util {

    /**
     * The gap between two separate
     * lines of one NameTag. This number is
     * always being utilized when we add multiple
     * lines to a NameTag.
     */
    public static double GAP_BETWEEN_LINES = 0.245D;

    /**
     * This is something I had to fix when I
     * used reflection in {@link NMSHandler#getEntityArmorStand(Player, String, Location, double)}
     * so that I can disable entity collision/hitting the
     * armor stands. The armor stand then gets displaced by about
     * dy = -1.9D, and we have to re-arrange it.
     */
    public static double MARKER_GAP = 1.9D;

    /**
     * Maximum distance for which we should update
     * the NameTag packets for a player.
     */
    public static double MAX_DISTANCE = 16;

    /**
     * Utility method designed to send a specific
     * {@link Packet<PacketListenerPlayOut>} packet to the
     * desired player. Note that this method should only be used
     * for sending "Out" type of Packets, this can't be used for receiving
     * or whatnot.
     * @param player Player to which we should send
     *               the packet.
     * @param packet The packet object itself. You can check
     *               {@link Packet<>} for more information
     *               regarding net.minecraft.server packets.
     */
    public static void sendPacket(Player player, Object packet) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet) packet);
    }

    /**
     * Utility method designed to rename a specific
     * fake {@link ArmorStand} entity into something else,
     * but only for one player at a time. Used in case you want
     * to have a single Mob/Item/NPC that displays a line of text
     * in which you want to use Placeholders and what not.
     * @param player Player to which we should send
     *               the renamed {@link EntityArmorStand}
     *               MetaData change {@link PacketPlayOutEntityMetadata}
     *               packet.
     * @param entity The entity which holds the nametag.
     * @param tag New tag - what it should be changed
     *            to.
     */
    public static void rename(Player player, Entity entity, String tag) {
        LivingEntity entityLiving = CacheManager.getArmorStand(player, entity);
        if (entityLiving == null) return;
        EntityArmorStand entityArmorStand = (EntityArmorStand) entityLiving;
        entityArmorStand.setCustomName(tag);
        PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(entityArmorStand.getId(), entityArmorStand.getDataWatcher(), false);
        Util.sendPacket(player, packet);
    }
}
