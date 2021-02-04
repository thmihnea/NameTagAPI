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

package by.thmihnea;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;

public interface INMSHandler {

    /**
     * Method used for setting the main Name Tag
     * of a specific {@link Entity}, passed as an actual
     * Bukkit API entity. This method actually changes only the
     * line number 0 of the {@link LineCacheManager} object. This also
     * is the base line for each NameTag, each additional line should only be
     * placed after you've set a Name Tag to the {@link Entity}.
     * @param player Player who should see this Name Tag.
     * @param entity The entity which holds the Name Tag.
     * @param tag The tag itself, what should be written
     *            above the entity's head.
     */
    void setNameTag(Player player, Entity entity, String tag);

    /**
     * Entirely deletes the Name Tag of an {@link Entity}.
     * This method should be called only when desiring
     * to actually delete the entire Name Tag of an
     * entity.
     * This method deletes the Name Tag only for a
     * single Player, so use {@link #deleteNameTag(Collection, Entity)} and
     * set the first parameter to {@link Bukkit#getOnlinePlayers()} in case you
     * want the effect to be global.
     * Don't bother making custom {@link org.bukkit.event.player.PlayerQuitEvent} events
     * which delete the Name Tags of each player, as our {@link PacketUpdateTask} runnable
     * already handles this.
     * @param player Player who we should delete the NameTag
     *               for.
     * @param entity Entity which we should delete the
     *               Name Tag for.
     */
    void deleteNameTag(Player player, Entity entity);

    /**
     * Method used for setting the main Name Tag
     * of a specific {@link Collection} of {@link Entity}, passed as actual
     * Bukkit API entities. This method actually changes only the
     * line number 0 of the {@link LineCacheManager} object. This also
     * is the base line for each NameTag, each additional line should only be
     * placed after you've set a Name Tag to the {@link Entity}.
     * @param players Players for which we should set the
     *                Name Tag. Use {@link Bukkit#getOnlinePlayers()}
     *                so that your changes have a global effect.
     * @param entity The entity which holds the Name Tag.
     * @param tag The tag itself, what should be written
     *            above the entity's head.
     */
    void setNameTag(Collection<? extends Player> players, Entity entity, String tag);

    /**
     * Entirely deletes the Name Tag of a {@link Collection} of
     * {@link Entity} entities.
     * This method should be called only when desiring
     * to actually delete the entire Name Tag of an
     * entity / collection of entities.
     * This method deletes the Name Tag for a specified
     * collection of Players. In order to have a global effect,
     * you must call {@link Bukkit#getOnlinePlayers()}.
     * Don't bother making custom {@link org.bukkit.event.player.PlayerQuitEvent} events
     * which delete the Name Tags of each player, as our {@link PacketUpdateTask} runnable
     * already handles this.
     * @param players Players who we should delete the NameTag
     *               for.
     * @param entity Entity which we should delete the
     *               Name Tag for.
     */
    void deleteNameTag(Collection<? extends Player> players, Entity entity);

    /**
     * Adds a line to an Entity Name Tag.
     * This method calculates the exact coordinates
     * that the line should have and then puts it on
     * top of the last line in the {@link LineCacheManager#getLines(Entity, Player)}
     * list of lines, which are actually {@link EntityLiving}, not
     * lines of Strings. :)
     * Note that this method only adds a line to a single player,
     * not to every single player on the server.
     * For effects to take place globally, make sure
     * to use {@link Bukkit#getOnlinePlayers()} and the method below this
     * one.
     * @param player The player for which we should add
     *               the line/display the packet.
     * @param entity The entity which we should add
     *               an additional line to.
     * @param tag The tag of the new line/what text it
     *            should display.
     */
    void addLine(Player player, Entity entity, String tag);

    /**
     * Adds a line to an Entity Name Tag.
     * This method calculates the exact coordinates
     * that the line should have and then puts it on
     * top of the last line in the {@link LineCacheManager#getLines(Entity, Player)}
     * list of lines, which are actually {@link EntityLiving}, not
     * lines of Strings. :)
     * Note that this method only adds a line to a single player,
     * not to every single player on the server.
     * @param players The players for which we should add
     *               the line/display the packet.
     * @param entity The entity which we should add
     *               an additional line to.
     * @param tag The tag of the new line/what text it
     *            should display.
     */
    void addLine(Collection<? extends Player> players, Entity entity, String tag);

    /**
     * Method used to remove a certain
     * line, passed as a number in the parameters list.
     * Note that counting begins at 0. Deletion of
     * line number 0 is prohibited and not permitted, as it will
     * have some negative effects with {@link CacheManager}.
     * I'm aiming to fix this problem in the next version
     * of the API.
     * @param player Player for which we should remove
     *               the line.
     * @param entity Entity holding the NameTag.
     * @param line The number of the line. Notation
     *             begins at 0.
     */
    void removeLine(Player player, Entity entity, int line);

    /**
     * Method used to remove a certain
     * line, passed as a number in the parameters list.
     * Note that counting begins at 0. Deletion of
     * line number 0 is prohibited and not permitted, as it will
     * have some negative effects with {@link CacheManager}.
     * I'm aiming to fix this problem in the next version
     * of the API.
     * @param players Players for which we should remove
     *               the line.
     * @param entity Entity holding the NameTag.
     * @param line The number of the line. Notation
     *             begins at 0.
     */
    void removeLine(Collection<? extends Player> players, Entity entity, int line);

    /**
     * Begin monitoring the pipeline (packets)
     * being sent and received by a certain {@link Player}.
     * This method is being used to monitor whenever the player
     * receives a PacketPlayOutEntityDestroy packet
     * so that we can stop destroying fake entities when players
     * are too far away.
     * @param player Player to begin pipeline monitoring
     *               for.
     */
    void startPipelineMonitoring(Player player);

    /**
     * Abort monitoring the channel
     * pipeline for a specific player.
     * Used generally whenever the player leaves the
     * server.
     * @param player Player to abort pipeline monitoring
     *               for.
     */
    void abortPipelineMonitoring(Player player);

    /**
     * Method responsible for sending packets to
     * a player, by parsing them to an {@link Object}, so that
     * we can use this method no matter the Spigot version.
     * This is a workaround NMS, every interface has one of
     * these. Pretty much the Packets are being transformed
     * in Objects, and the sending method puts them back together.
     * @param player The player to whom we want to send
     *               this packet.
     * @param object The packet itself. Make sure it is
     *               compatible before sending.
     */
    void sendPacket(Player player, Object packet);

    /**
     * Returns a packet that should destroy
     * a certain {@link LivingEntity}.
     * Doesn't send it to the player; make sure to use
     * the method above, {@link #sendPacket(Player, Object)} to
     * send it as well.
     * @param livingEntity The {@link LivingEntity} which shall
     *                     be destroyed.
     * @return {@link Object}
     */
    Object getDestroyPacket(LivingEntity livingEntity);

    /**
     * Returns a packet that should spawn
     * a certain {@link LivingEntity}.
     * Doesn't send it to the player; make sure to use
     * the method {@link #sendPacket(Player, Object)} to
     * send it as well.
     * @param livingEntity The {@link LivingEntity} which shall be
     *                     spawned.
     * @return {@link Object}
     */
    Object getSpawnPacket(LivingEntity livingEntity);

    /**
     * Returns a packet that should teleport
     * a certain {@link LivingEntity}.
     * Doesn't send it to the player; make sure to use
     * the method {@link #sendPacket(Player, Object)} to
     * send it as well.
     * @param livingEntity The {@link LivingEntity} which shall be
     *                     teleported.
     * @return {@link Object}
     */
    Object getTeleportPacket(LivingEntity livingEntity);

    /**
     * Utility method designed to give us a fake {@link org.bukkit.entity.ArmorStand}
     * entity at the desired {@link Location}.
     * Entity can only be seen by the specified {@link Player}, meaning each
     * player can see a different text depending on how you want
     * to achieve it. This class shouldn't be used when coding
     * with the API, but it's up to the user to decide what he's going to
     * do.
     * @param player Player to have the fake {@link org.bukkit.entity.ArmorStand} packet
     *               displayed to him.
     * @param tag The {@link ArmorStand#getCustomName()} which the fake
     *            entity will have when displaying the said packet.
     * @param location Location at which the entity
     *                 shall be displayed.
     * @param y Parameter which fixes the y-axis hitbox, in case
     *          our entity is smaller or bigger. You can
     *          go and check {@link by.thmihnea.EntityHitbox} for
     *          more information.
     * @return {@link EntityArmorStand}
     */
    Object getEntityArmorStand(Player player, String tag, Location location, double y);

    /**
     * Converts an EntityArmorStand, which is an
     * NMS object, into a {@link LivingEntity}.
     * This method might be a bit consuming as it casts
     * the object three times so make sure to use it
     * only when 100% needed.
     * @param armorStand The armorstand itself.
     * @return {@link LivingEntity}
     */
    LivingEntity getLivingEntity(Object armorStand);

}
