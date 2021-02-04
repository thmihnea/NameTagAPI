package by.thmihnea.cache;

import java.util.ArrayList;
import java.util.List;

public class EntityIDCacheManager {

    /**
     * This {@link List} of {@link Integer} helps us
     * store more information about every single
     * fake entity id which we've created.
     * We use this class in order to listen to the
     * io.netty packets which are being sent through the pipeline.
     * Each time we encounter a PacketPlayOutEntityDestroy packet,
     * we hook into it and check whether or not there's an entity ID
     * in that packet which belongs to this List. If there is, we
     * simply block the packet to prevent entity deletion.
     */
    private static final List<Integer> cachedIDs = new ArrayList<>();

    /**
     * Returns the entire {@link List} of {@link Integer} which
     * we've cached.
     * @return {@link List}
     */
    public static List<Integer> getCache() {
        return cachedIDs;
    }

    /**
     * Method used to add another entity ID
     * to our list.
     * @param id Id which we want to add
     *           to the list.
     */
    public static void addEntry(int id) {
        cachedIDs.add(id);
    }

    /**
     * Removes an entry from our list.
     * Loops through the list and checks if
     * the said ID exists.
     * @param id ID to look after.
     */
    public static void removeEntry(int id) {
        for (int i = 0; i < cachedIDs.size(); i++) {
            if (cachedIDs.get(i) == id) {
                cachedIDs.remove(i);
                return;
            }
        }
    }

    /**
     * Returns whether or not a certain
     * entity id belongs to our list.
     * @param id ID to lookup for.
     * @return {@link Boolean}
     */
    public static boolean contains(int id) {
        return cachedIDs.contains(id);
    }
}
