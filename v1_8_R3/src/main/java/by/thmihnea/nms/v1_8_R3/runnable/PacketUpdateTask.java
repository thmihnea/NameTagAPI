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

package by.thmihnea.nms.v1_8_R3.runnable;

import by.thmihnea.EntityHitbox;
import by.thmihnea.NameTagAPI;
import by.thmihnea.nms.v1_8_R3.Util;
import by.thmihnea.nms.v1_8_R3.cache.CacheManager;
import by.thmihnea.nms.v1_8_R3.cache.LineCacheManager;
import by.thmihnea.nms.v1_8_R3.cache.PacketUpdateTaskManager;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PacketUpdateTask implements Runnable {

    /**
     * Player for which we're
     * constructing the said {@link Runnable}
     * object.
     */
    private Player player;

    /**
     * The {@link EntityLiving}, which is
     * pretty much the fake armor stand which
     * we display text on.
     */
    private EntityLiving entityLiving;

    /**
     * The entity which holds
     * the said name tag.
     */
    private Entity entity;

    /**
     * The bukkit task which
     * helps us keeping track of the
     * Runnable oject.
     */
    private BukkitTask task;

    /**
     * Coordinate used for taking care
     * of multi line support.
     */
    private double additionalY;

    /**
     * Constructor for the Packet Update Task.
     * This class takes care of teleportation of
     * each line/name tag to the desired entity location.
     * Without it, lines can't move normally.
     * Utilizes packets.
     * {@link PacketPlayOutEntityTeleport}
     * @param player The player for which we're updating
     *               the armor stand.
     * @param entityLiving The armor stand itself.
     * @param entity The entity which holds the armor stand
     *               on its head.
     */
    public PacketUpdateTask(Player player, EntityLiving entityLiving, Entity entity) {
       this(player, entityLiving, entity, 0);
    }

    /**
     * Constructor for the Packet Update Task.
     * This class takes care of teleportation of
     * each line/name tag to the desired entity location.
     * Without it, lines can't move normally.
     * Utilizes packets.
     * {@link PacketPlayOutEntityTeleport}
     * @param player The player for which we're updating
     *               the armor stand.
     * @param entityLiving The armor stand itself.
     * @param entity The entity which holds the armor stand
     *               on its head.
     * @param additionalY Coordinate which helps when messing
     *                    around with multiple line coordinates.
     */
    public PacketUpdateTask(Player player, EntityLiving entityLiving, Entity entity, double additionalY) {
        this.player = player;
        this.entityLiving = entityLiving;
        this.entity = entity;
        this.additionalY = additionalY;
        this.task = Bukkit.getScheduler().runTaskTimer(NameTagAPI.getInstance(), this, 0L, 1L);
        PacketUpdateTaskManager.addEntry(entityLiving, this);
    }

    /**
     * Inherited method from
     * {@link Runnable} interface.
     */
    @Override
    public void run() {
        if (!(this.player.isOnline())) {
            CacheManager.removePlayerFromCache(this.player);
            LineCacheManager.removePlayerFromCache(this.player);
            this.clear();
            return;
        }
        if (this.entity.isDead()) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(this.entityLiving.getId());
            Util.sendPacket(player, packet);
            CacheManager.removeEntityFromCache(this.entity);
            LineCacheManager.removeEntityFromCache(this.entity);
            this.clear();
            return;
        }
        Location location = this.entity.getLocation();
        final double y = location.getY() + EntityHitbox.getFromEntity(this.entity).getLocationModifier().getY() + this.additionalY;
        this.entityLiving.setLocation(location.getX(), y, location.getZ(), location.getYaw(), location.getPitch());
        PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(this.entityLiving);
        Util.sendPacket(player, packet);
    }

    /**
     * Method which allows us to
     * clean up disk space whenever we feel like
     * deleting the {@link PacketUpdateTask} object.
     * Ths makes the object go faster towards to
     * Garbage Collector, as it makes Bukkit completely
     * stop using it.
     */
    public void clear() {
        if (this.task != null) {
            Bukkit.getScheduler().cancelTask(task.getTaskId());
            this.task = null;
        }
    }
}
