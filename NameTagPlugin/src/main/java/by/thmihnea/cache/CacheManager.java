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

package by.thmihnea.cache;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.*;

public class CacheManager {

    /**
     * Our data structure is based off of a map, which
     * points a certain entity to a Map<Player, EntityLiving>.
     * This helps us keep track of every entity and its NameTags which
     * are kept in the Map along with each Player, since we potentially can have
     * different Name Tags per player.
     */
    private static final Map<Entity, Map<Player, LivingEntity>> cache = new HashMap<>();

    /**
     * Returns {@link #cache}, which is
     * our data structure, pretty much.
     * @return {@link Map}
     */
    public static Map<Entity, Map<Player, LivingEntity>> getCache() {
        return cache;
    }

    /**
     * Returns the cached Map off of
     * a single entity. Basically, returns us every single Player with his specific
     * lines per entity.
     * @param entity Entity to lookup for.
     * @return {@link Map<Player, LivingEntity>}
     */
    public static Map<Player, LivingEntity> getCachedMapByEntity(Entity entity) {
        return cache.get(entity);
    }

    /**
     * Returns the armor stand object off of
     * a {@link Player} & {@link Entity}.
     * @param player Player to look up for.
     * @param entity Entity to look up for.
     * @return {@link EntityLiving}
     */
    public static LivingEntity getArmorStand(Player player, Entity entity) {
        Map<Player, LivingEntity> map = cache.get(entity);
        if (map == null) return null;
        if (!(map.containsKey(player))) return null;
        return map.get(player);
    }

    /**
     * Returns a player from his specific armor stand
     * object, which points us to an entity.
     * @param entity Entity to look up for.
     * @param entityLiving The armor stand object which
     *                     we have.
     * @return {@link Player}
     */
    public static Player getPlayerByArmorStand(Entity entity, LivingEntity entityLiving) {
        Map<Player, LivingEntity> map = getCachedMapByEntity(entity);
        for (Player player : map.keySet()) {
            if (map.get(player).equals(entityLiving)) return player;
        }
        return null;
    }

    /**
     * Returns a set of the players that can
     * see a certain entity's nametag.
     * @param entity Entity to look up for.
     * @return {@link Set<Player>}
     */
    public static Set<Player> getPlayersByEntity(Entity entity) {
        return new HashSet<>(getCachedMapByEntity(entity).keySet());
    }

    /**
     * Returns every single armor stand a player has
     * got in his cache memory, not limited by entities.
     * @param player Player to look up for.
     * @return {@link Set<LivingEntity>}
     */
    public static Set<LivingEntity> getAllArmorStandsByPlayer(Player player) {
        Set<LivingEntity> set = new HashSet<>();
        cache.keySet().forEach(entity -> {
            Map<Player, LivingEntity> map = getCachedMapByEntity(entity);
            if (map.containsKey(player)) set.add(map.get(player));
        });
        return set;
    }

    /**
     * Completely removes an entity from
     * cached memory. Empties our data structure and
     * saves up disk space.
     * @param entity Entity to look up for and obviously, delete.
     */
    public static void removeEntityFromCache(Entity entity) {
        if (EntityIDCacheManager.contains(entity.getEntityId()))
            EntityIDCacheManager.removeEntry(entity.getEntityId());
        cache.remove(entity);
    }

    /**
     * Adds an entry to our data structure.
     * Keep in mind, this data structure only keeps track of
     * the actual NameTag of the Entity, meaning the line
     * number 0!
     * @param player Player to be added to our data structure.
     * @param entity The entity which we're generating a name tag for.
     * @param entityLiving The armorstand fake entity.
     */
    public static void addEntry(Player player, Entity entity, LivingEntity entityLiving) {
        Map<Player, LivingEntity> map = getCachedMapByEntity(entity);
        if (map == null) map = new HashMap<>();
        if (!EntityIDCacheManager.contains(entity.getEntityId()))
            EntityIDCacheManager.addEntry(entity.getEntityId());
        map.put(player, entityLiving);
        cache.put(entity, map);
    }

    /**
     * Removes a player from cached data, looping
     * through each and every {@link Entity} key
     * to make sure that he's getting fully removed from
     * the cache.
     * @param player Player to look up for.
     */
    public static void removePlayerFromCache(Player player) {
        Map<Entity, Map<Player, LivingEntity>> copyMap = new HashMap<>(getCache());
        copyMap.keySet().forEach(entity -> {
            Map<Player, LivingEntity> map = getCachedMapByEntity(entity);
            if (map == null) return;
            if (!(map.containsKey(player))) return;
            map.remove(player);
            if (map.size() == 0) {
                removeEntityFromCache(entity);
                return;
            }
            cache.put(entity, map);
        });
    }

    /**
     * Removes a fake armor stand entity
     * from the cahce/data structure, from his
     * {@link Player} key.
     * @param entity Entity to look up for.
     * @param player Player to look up for.
     */
    public static void removeArmorStandFromCache(Entity entity, Player player) {
        Map<Player, LivingEntity> map = getCachedMapByEntity(entity);
        if (!(map.containsKey(player))) return;

        map.remove(player);
        cache.put(entity, map);
    }

    /**
     * Tells us if an entity is already in
     * our cached data structure/if it has
     * a NameTag.
     * @param entity Entity to look up after.
     * @return {@link Boolean}
     */
    public static boolean hasNameTag(Entity entity) {
        return cache.containsKey(entity);
    }

}
