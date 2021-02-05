package by.thmihnea.nms.v1_13_R2;

import by.thmihnea.EntityHitbox;
import by.thmihnea.INMSHandler;
import by.thmihnea.NameTagAPI;
import by.thmihnea.cache.CacheManager;
import by.thmihnea.cache.LineCacheManager;
import by.thmihnea.cache.PacketUpdateTaskManager;
import by.thmihnea.nms.v1_13_R2.connection.PacketReader;
import by.thmihnea.runnable.PacketUpdateTask;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class NMSHandler implements INMSHandler {

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
    @Override
    public void setNameTag(Player player, Entity entity, String tag) {
        if (CacheManager.hasNameTag(entity)) {
            NameTagAPI.getInstance().logInfo("Entity #" + entity.getUniqueId() + " (TYPE: " + entity.getType() + ") already has a Name Tag, therefore a new one can't be set!");
            return;
        }

        tag = ChatColor.translateAlternateColorCodes('&', tag);

        LivingEntity entityLiving = CacheManager.getArmorStand(player, entity);
        if (entityLiving != null) {
            Util.rename(player, entity, tag);
            return;
        }

        Location location = entity.getLocation();
        EntityHitbox entityHitbox = EntityHitbox.getFromEntity(entity);
        final double y = location.getY() + entityHitbox.getLocationModifier().getY();

        EntityArmorStand entityArmorStand = (EntityArmorStand) this.getEntityArmorStand(player, tag, location, y);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
        Util.sendPacket(player, packet);

        LivingEntity livingEntity = (LivingEntity) entityArmorStand.getBukkitEntity();

        CacheManager.addEntry(player, entity, livingEntity);
        LineCacheManager.addEntry(player, entity, livingEntity);
        new PacketUpdateTask(player, livingEntity, entity);
    }

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
    @Override
    public void deleteNameTag(Player player, Entity entity) {
        LivingEntity entityLiving = CacheManager.getArmorStand(player, entity);
        if (entityLiving == null) return;

        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityLiving.getEntityId());
        Util.sendPacket(player, packet);

        Objects.requireNonNull(LineCacheManager.getLines(entity, player)).forEach(ent -> {
            PacketPlayOutEntityDestroy destroyLinePacket = new PacketPlayOutEntityDestroy(ent.getEntityId());
            Util.sendPacket(player, destroyLinePacket);
        });

        CacheManager.removeArmorStandFromCache(entity, player);
        LineCacheManager.removeEntityFromCache(entity);
    }

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
    @Override
    public void setNameTag(Collection<? extends Player> players, Entity entity, String tag) {
        players.forEach(player -> {
            this.setNameTag(player, entity, tag);
        });
    }

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
    @Override
    public void deleteNameTag(Collection<? extends Player> players, Entity entity) {
        players.forEach(player -> {
            this.deleteNameTag(player, entity);
        });
    }

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
     * to use {@link Bukkit#getOnlinePlayers()} and the method
     * {@link #addLine(Collection, Entity, String)}.
     * @param player The player for which we should add
     *               the line/display the packet.
     * @param entity The entity which we should add
     *               an additional line to.
     * @param tag The tag of the new line/what text it
     *            should display.
     */
    @Override
    public void addLine(Player player, Entity entity, String tag) {

        List<LivingEntity> list = LineCacheManager.getLines(entity, player);
        if (list == null) {
            this.setNameTag(player, entity, tag);
            return;
        }

        tag = ChatColor.translateAlternateColorCodes('&', tag);

        int size = list.size();
        LivingEntity entityLiving = list.get(size - 1);
        Location location = entityLiving.getLocation();

        double additionalY = size * Util.GAP_BETWEEN_LINES;
        final double y = location.getY();

        EntityArmorStand entityArmorStand = (EntityArmorStand) this.getEntityArmorStand(player, tag, location, y);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(entityArmorStand);
        LivingEntity armorStand = (LivingEntity) entityArmorStand.getBukkitEntity();

        Util.sendPacket(player, packet);
        LineCacheManager.addEntry(player, entity, armorStand);
        new PacketUpdateTask(player, armorStand, entity, additionalY);
    }

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
    @Override
    public void addLine(Collection<? extends Player> players, Entity entity, String tag) {
        players.forEach(player -> {
            this.addLine(player, entity, tag);
        });
    }

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
    @Override
    public void removeLine(Player player, Entity entity, int line) {
        if (line == 0) {
            NameTagAPI.getInstance().logSevere("Line #0 can't be deleted! Use method NMSHandler#deleteNameTag to fully get rid of the NameTag!");
            return;
        }

        LivingEntity entityLiving = Objects.requireNonNull(LineCacheManager.getLines(entity, player)).get(line);
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(entityLiving.getEntityId());
        Util.sendPacket(player, packet);

        LineCacheManager.removeLine(player, entity, line);
        double y0 = entity.getLocation().getY() + EntityHitbox.getFromEntity(entity).getLocationModifier().getY();

        AtomicInteger i = new AtomicInteger(0);
        LineCacheManager.getLines(entity, player).forEach(ent -> {
            PacketUpdateTask packetUpdateTask = PacketUpdateTaskManager.getFromEntityLiving(ent);
            PacketUpdateTaskManager.removeEntry(ent);
            packetUpdateTask.clear();

            Location location = ent.getLocation();
            EntityLiving nmsEntity = (EntityLiving) ((CraftEntity) ent).getHandle();
            nmsEntity.setLocation(location.getX(), y0, location.getZ(), location.getYaw(), location.getPitch());
            PacketPlayOutEntityTeleport p = new PacketPlayOutEntityTeleport(nmsEntity);
            Util.sendPacket(player, p);
            new PacketUpdateTask(player, ent, entity, i.get() * Util.GAP_BETWEEN_LINES);

            i.set(i.get() + 1);
        });
    }

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
    @Override
    public void removeLine(Collection<? extends Player> players, Entity entity, int line) {
        players.forEach(player -> {
            this.removeLine(player, entity, line);
        });
    }

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
    @Override
    public void startPipelineMonitoring(Player player) {
        PacketReader.startPipelineMonitoring(player);
    }

    /**
     * Abort monitoring the channel
     * pipeline for a specific player.
     * Used generally whenever the player leaves the
     * server.
     * @param player Player to abort pipeline monitoring
     *               for.
     */
    @Override
    public void abortPipelineMonitoring(Player player) {
        PacketReader.abortPipelineMonitoring(player);
    }

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
    @Override
    public void sendPacket(Player player, Object object) {
        Util.sendPacket(player, object);
    }

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
    @Override
    public Object getDestroyPacket(LivingEntity livingEntity) {
        EntityLiving entityLiving = (EntityLiving) ((CraftEntity) livingEntity).getHandle();
        return new PacketPlayOutEntityDestroy(entityLiving.getId());
    }

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
    @Override
    public Object getSpawnPacket(LivingEntity livingEntity) {
        EntityLiving entityLiving = (EntityLiving) ((CraftEntity) livingEntity).getHandle();
        return new PacketPlayOutSpawnEntityLiving(entityLiving);
    }

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
    @Override
    public Object getTeleportPacket(LivingEntity livingEntity) {
        EntityLiving entityLiving = (EntityLiving) ((CraftEntity) livingEntity).getHandle();
        return new PacketPlayOutEntityTeleport(entityLiving);
    }

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
    @Override
    public Object getEntityArmorStand(Player player, String tag, Location location, double y) {
        EntityArmorStand entityArmorStand = new EntityArmorStand(((CraftWorld) player.getWorld()).getHandle());

        entityArmorStand.setLocation(location.getX(), y, location.getZ(), location.getYaw(), location.getPitch());
        entityArmorStand.setInvisible(true);
        entityArmorStand.setCustomNameVisible(true);

        tag = ChatColor.translateAlternateColorCodes('&', tag);
        String jsonName = "{\"text\":\"" + tag + "\"}";
        entityArmorStand.setCustomName(IChatBaseComponent.ChatSerializer.a(jsonName));

        entityArmorStand.setNoGravity(true);
        entityArmorStand.setMarker(true);

        return entityArmorStand;
    }

    /**
     * Converts an {@link EntityArmorStand}, which is an
     * NMS object, into a {@link LivingEntity}.
     * This method might be a bit consuming as it casts
     * the object three times so make sure to use it
     * only when 100% needed.
     * @param armorStand The armorstand itself.
     * @return {@link LivingEntity}
     */
    @Override
    public LivingEntity getLivingEntity(Object armorStand) {
        return (LivingEntity) ((EntityArmorStand) armorStand).getBukkitEntity();
    }
}
