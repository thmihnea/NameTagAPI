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

package by.thmihnea.runnable;

import by.thmihnea.EntityHitbox;
import by.thmihnea.NameTagAPI;
import by.thmihnea.cache.CacheManager;
import by.thmihnea.cache.LineCacheManager;
import by.thmihnea.cache.PacketUpdateTaskManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.CompletableFuture;

public class PacketUpdateTask implements Runnable {

    /**
     * Player for which we're
     * constructing the said {@link Runnable}
     * object.
     */
    private Player player;

    /**
     * The {@link LivingEntity}, which is
     * pretty much the fake armor stand which
     * we display text on.
     */
    private LivingEntity entityLiving;

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
     * Used for {@link NameTagAPI} Protocol Library packet
     * listener to detect if a player is moving too far
     * away from a certain packet, therefore we shouldn't delete it.
     * Made this to differentiate normal entity destruction packets
     * from our own.
     */
    private boolean active;

    /**
     * Constructor for the Packet Update Task.
     * This class takes care of teleportation of
     * each line/name tag to the desired entity location.
     * Without it, lines can't move normally.
     * Utilizes packets.
     * @param player The player for which we're updating
     *               the armor stand.
     * @param entityLiving The armor stand itself.
     * @param entity The entity which holds the armor stand
     *               on its head.
     */
    public PacketUpdateTask(Player player, LivingEntity entityLiving, Entity entity) {
        this(player, entityLiving, entity, 0);
    }

    /**
     * Constructor for the Packet Update Task.
     * This class takes care of teleportation of
     * each line/name tag to the desired entity location.
     * Without it, lines can't move normally.
     * Utilizes packets.
     * @param player The player for which we're updating
     *               the armor stand.
     * @param entityLiving The armor stand itself.
     * @param entity The entity which holds the armor stand
     *               on its head.
     * @param additionalY Coordinate which helps when messing
     *                    around with multiple line coordinates.
     */
    public PacketUpdateTask(Player player, LivingEntity entityLiving, Entity entity, double additionalY) {
        this.player = player;
        this.entityLiving = entityLiving;
        this.entity = entity;
        this.additionalY = additionalY;
        this.active = true;
        this.task = Bukkit.getScheduler().runTaskTimer(NameTagAPI.getInstance(), this, 0L, 1L);
        PacketUpdateTaskManager.addEntry(entityLiving.getEntityId(), this);
    }

    /**
     * Inherited method from
     * {@link Runnable} interface.
     */
    @Override
    public void run() {
        if (this.entity.getLocation().getChunk().isLoaded())
            this.active = true;
        if (!this.isActive()) return;
        if (!(this.player.isOnline())) {
            CacheManager.removePlayerFromCache(this.player);
            LineCacheManager.removePlayerFromCache(this.player);
            this.clear();
            return;
        }
        if (this.entity.isDead() || this.entity == null) {
            this.removeEntityFromCache();
            this.clear();
            return;
        }
        if (!this.entity.getLocation().getChunk().isLoaded()) {
            this.removeEntityFromCache();
            this.clear();
            return;
        }
        Object teleportPacket = NameTagAPI.getNMSHandler().getTeleportPacket(this.entityLiving);

        Location location = this.entity.getLocation();
        final double y = location.getY() + EntityHitbox.getFromEntity(this.entity).getLocationModifier().getY() + this.additionalY;
        Location finalLocation = new Location(location.getWorld(), location.getX(), y, location.getZ(), location.getYaw(), location.getPitch());

        this.entityLiving.teleport(finalLocation);
        NameTagAPI.getNMSHandler().sendPacket(this.player, teleportPacket);
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
            this.active = false;
        }
    }

    /**
     * Completely removes an entity
     * from our cached memory.
     * This method is being run either when the
     * entity is too far away from the player or
     * when the entity is completely dead.
     */
    public void removeEntityFromCache() {
        CompletableFuture.runAsync(() -> {
            Object destroyPacket = NameTagAPI.getNMSHandler().getDestroyPacket(this.entityLiving);
            NameTagAPI.getNMSHandler().sendPacket(this.player, destroyPacket);
        }).thenRun(() -> {
            CacheManager.removeEntityFromCache(this.entity);
            LineCacheManager.removeEntityFromCache(this.entity);
        });
    }

    /**
     * Returns if the PacketUpdateTask itself is active
     * and should be running on the server.
     * Method used in differentiating normal entity destruction
     * packets from our own.
     * @return {@link Boolean}
     */
    public boolean isActive() {
        return this.active;
    }
}