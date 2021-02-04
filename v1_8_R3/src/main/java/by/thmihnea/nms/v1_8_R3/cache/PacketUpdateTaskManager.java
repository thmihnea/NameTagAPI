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

import by.thmihnea.nms.v1_8_R3.runnable.PacketUpdateTask;
import net.minecraft.server.v1_8_R3.EntityLiving;

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
    private final static Map<EntityLiving, PacketUpdateTask> cache = new HashMap<>();

    /**
     * Method which adds an entry to our
     * data structure.
     * @param entityLiving Armor stand to be added.
     * @param packetUpdateTask {@link PacketUpdateTask} object
     *                          to be added.
     */
    public static void addEntry(EntityLiving entityLiving, PacketUpdateTask packetUpdateTask) {
        cache.put(entityLiving, packetUpdateTask);
    }

    /**
     * Returns a {@link PacketUpdateTask} object by pulling
     * it from our data structure. Requires passing an armor
     * stand as a parameter.
     * @param entityLiving The armor stand used for our search.
     * @return {@link PacketUpdateTask}
     */
    public static PacketUpdateTask getFromEntityLiving(EntityLiving entityLiving) {
        return cache.get(entityLiving);
    }

    /**
     * Removes an entry from our data structure.
     * @param entityLiving Armor Stand to be deleted
     *                     from our data structure, along with
     *                     all information regarding its
     *                     {@link PacketUpdateTask} object.
     */
    public static void removeEntry(EntityLiving entityLiving) {
        cache.remove(entityLiving);
    }
}
