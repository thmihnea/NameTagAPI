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

package by.thmihnea.nms.v1_8_R3.cache;

import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

public class LineCacheManager {

    /**
     * Our data structure is based off of a map, which
     * points a certain entity to a Map<Player, List<EntityLiving>>.
     * This helps us keep track of every entity and its NameTags which
     * are kept in the Map along with each Player, since we potentially can have
     * different Name Tags per player.
     */
    private static final Map<Entity, Map<Player, List<EntityLiving>>> cache = new HashMap<>();

    /**
     * Returns {@link #cache}, which is
     * our data structure, pretty much.
     * @return {@link Map}
     */
    public static Map<Entity, Map<Player, List<EntityLiving>>> getCache() {
        return cache;
    }

    /**
     * Returns the cached Map off of
     * a single entity. Basically, returns us every single Player with his specific
     * lines per entity.
     * @param entity Entity to lookup for.
     * @return {@link Map<Player, List<EntityLiving>>}
     */
    public static Map<Player, List<EntityLiving>> getCachedMapByEntity(Entity entity) {
        return cache.get(entity);
    }

    /**
     * Returns the list of lines, parametrized
     * as {@link EntityLiving} objects for easier use.
     * @param entity Entity to look up for.
     * @param player Player to look up for.
     * @return {@link List<EntityLiving>}
     */
    public static List<EntityLiving> getLines(Entity entity, Player player) {
        Map<Player, List<EntityLiving>> map = cache.get(entity);
        if (map == null) return null;
        if (!(map.containsKey(player))) return null;
        return map.get(player);
    }

    /**
     * Returns a player object {@link Player}, given
     * his specific list of lines.
     * @param entity Entity to look up for.
     * @param set The set of lines.
     * @return {@link Player}
     */
    public static Player getPlayerByLines(Entity entity, List<EntityLiving> set) {
        Map<Player, List<EntityLiving>> map = getCachedMapByEntity(entity);
        for (Player player : map.keySet())
            if (map.get(player).equals(set)) return player;
        return null;
    }

    /**
     * Returns every single {@link Player} object
     * which can see the NameTag of a specific entity;
     * @param entity Entity to look up for.
     * @return {@link Set<Player>}
     */
    public static Set<Player> getPlayersByEntity(Entity entity) {
        return new HashSet<>(getCachedMapByEntity(entity).keySet());
    }

    /**
     * Returns every single line a player
     * can see, as saved in our cached memory.
     * @param player Player to look up for.
     * @return {@link List<List<EntityLiving>>}
     */
    public static List<List<EntityLiving>> getAllLinesByPlayer(Player player) {
        List<List<EntityLiving>> set = new ArrayList<>();
        cache.keySet().forEach(entity -> {
            Map<Player, List<EntityLiving>> map = getCachedMapByEntity(entity);
            if (map.containsKey(player)) set.add(map.get(player));
        });
        return set;
    }

    /**
     * Entirely removes an entity along
     * with every single NameTag/line it had from
     * cached memory.
     * @param entity Entity to delete from our
     *               data structure.
     */
    public static void removeEntityFromCache(Entity entity) {
        cache.remove(entity);
    }

    /**
     * Adds an entry to our data structure, as portrayed
     * above.
     * @param player Player to add an entry for.
     * @param entity Entity to add an entry for.
     * @param entityLiving The armor stand fake entity which we
     *                     want to add to our data structure.
     */
    public static void addEntry(Player player, Entity entity, EntityLiving entityLiving) {
        Map<Player, List<EntityLiving>> map = getCachedMapByEntity(entity);
        if (map == null) map = new HashMap<>();
        List<EntityLiving> set = map.get(player);
        if (set == null) set = new ArrayList<>();
        set.add(entityLiving);
        map.put(player, set);
        cache.put(entity, map);
    }

    /**
     * Entirely removes a player from cached memory, along
     * with every single line/name tag he can see.
     * @param player Player too delete/look up for.
     */
    public static void removePlayerFromCache(Player player) {
        Map<Entity, Map<Player, List<EntityLiving>>> copyMap = new HashMap<>(getCache());
        copyMap.keySet().forEach(entity -> {
            Map<Player, List<EntityLiving>> map = getCachedMapByEntity(entity);
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
     * Removes a line that a player can see
     * on a specific entity.
     * @param player Player to look up for.
     * @param entity Entity to look up for.
     * @param line Line number which we want to delete.
     */
    public static void removeLine(Player player, Entity entity, int line) {
        Map<Player, List<EntityLiving>> map = getCachedMapByEntity(entity);
        if (!(map.containsKey(player))) return;
        List<EntityLiving> set = map.get(player);
        if (set.size() < line) return;
        EntityLiving entityLiving = set.get(line);
        set.remove(entityLiving);
        map.put(player, set);
    }

    /**
     * Removes a line, but this time given the actual
     * armor stand object/instance, not only a number.
     * @param player Player to look up for.
     * @param entity Entity to look up for.
     * @param entityLiving The armor stand which we want to delete.
     */
    public static void removeLine(Player player, Entity entity, EntityLiving entityLiving) {
        List<EntityLiving> list = cache.get(entity).get(player);
        if (!(list.contains(entityLiving))) return;
        int line = list.indexOf(entityLiving);
        removeLine(player, entity, line);
    }

}
