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

import by.thmihnea.runnable.PacketUpdateTask;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

public class PacketUpdateTaskManager {

    /**
     * Map which keeps track of every single
     * {@link PacketUpdateTask} object, pointed at by
     * each armor stand that holds it.
     * This is for easy access when dealing with multi line
     * deletion so that we can re-set armor stand locations while
     * deleting their {@link PacketUpdateTask} objects, and re-instantiating
     * them.,
     */
    private final static Map<Integer, PacketUpdateTask> cache = new HashMap<>();

    /**
     * Method which adds an entry to our
     * data structure.
     * @param entityId Armor stand to be added.
     * @param packetUpdateTask {@link PacketUpdateTask} object
     *                          to be added.
     */
    public static void addEntry(Integer entityId, PacketUpdateTask packetUpdateTask) {
        cache.put(entityId, packetUpdateTask);
    }

    /**
     * Returns a {@link PacketUpdateTask} object by pulling
     * it from our data structure. Requires passing an armor
     * stand as a parameter.
     * @param entityLiving The armor stand used for our search.
     * @return {@link PacketUpdateTask}
     */
    public static PacketUpdateTask getFromEntityLiving(LivingEntity entityLiving) {
        return cache.get(entityLiving.getEntityId());
    }

    /**
     * Removes an entry from our data structure.
     * @param entityLiving Armor Stand to be deleted
     *                     from our data structure, along with
     *                     all information regarding its
     *                     {@link PacketUpdateTask} object.
     */
    public static void removeEntry(LivingEntity entityLiving) {
        cache.remove(entityLiving.getEntityId());
    }

    /**
     * Returns a {@link PacketUpdateTask} object by pulling
     * it from our data structure. Requires passing an armor stand
     * entity id as a parameter.
     * @param id The armor stand ID used for our
     *           lookup.
     * @return {@link PacketUpdateTask}
     */
    public static PacketUpdateTask getFromEntityId(int id) {
        return cache.get(id);
    }
}
