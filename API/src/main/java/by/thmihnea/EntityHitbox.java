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

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Optional;

public enum EntityHitbox {

    ZOMBIE(EntityType.ZOMBIE, -0.1, 0, 0),
    CREEPER(EntityType.CREEPER, -0.5, 0, 0),
    SKELETON(EntityType.SKELETON, -0.15, 0, 0),
    SPIDER(EntityType.SPIDER, -1.25, 0, 0),
    SLIME(EntityType.SLIME, 0, 0, 0),
    GHAST(EntityType.GHAST, 2.5, 0, 0),
    ENDERMAN(EntityType.ENDERMAN, 0.6, 0, 0),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, -1.5, 0, 0),
    SILVERFISH(EntityType.SILVERFISH, -1.875, 0, 0),
    BLAZE(EntityType.BLAZE, -0.35, 0, 0),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, 0, 0, 0),
    BAT(EntityType.BAT, -1.65, 0, 0),
    WITCH(EntityType.WITCH, 0.25, 0, 0),
    ENDERMITE(EntityType.ENDERMITE, -1.875, 0, 0),
    GUARDIAN(EntityType.GUARDIAN, -1.1, 0, 0),
    PIG(EntityType.PIG, -1.25, 0, 0),
    SHEEP(EntityType.SHEEP, -0.85, 0, 0),
    COW(EntityType.COW, -0.75, 0, 0),
    CHICKEN(EntityType.CHICKEN, -1.3, 0, 0),
    SQUID(EntityType.SQUID, -1.25, 0, 0),
    WOLF(EntityType.WOLF, -1.25, 0, 0),
    OCELOT(EntityType.OCELOT, -1.4, 0, 0),
    MUSHROOM_COW(EntityType.MUSHROOM_COW, -0.55, 0, 0),
    HORSE(EntityType.HORSE, -0.5, 0, 0),
    RABBIT(EntityType.RABBIT, -1.25, 0, 0),
    VILLAGER(EntityType.VILLAGER, -0.15, 0, 0),
    WITHER_BOSS(EntityType.WITHER, 1.425, 0, 0),
    ENDER_DRAGON(EntityType.ENDER_DRAGON, 1.35, 0, 0),
    ENTITY_PLAYER(EntityType.PLAYER, 0.075, 0, 0),
    ENTITY_ITEM(EntityType.DROPPED_ITEM, -1.65, 0, 0);

    /**
     * The entity type for which we're
     * re-building the actual hitbox.
     */
    private EntityType entityType;

    /**
     * The height of the hitbox.
     */
    private double height;

    /**
     * OffsetX & OffsetY are currently not used
     * for anything, added in case I figure out
     * a way of setting the hitbox location to
     * entities' heads.
     */
    private double offsetX;
    private double offsetZ;

    /**
     * Default constructor for each Enum item.
     * @param entityType The entity type of the enum value.
     * @param height The height of the hitbox (offset y).
     * @param offsetX OffsetX
     * @param offsetZ OffsetZ
     */
    EntityHitbox(EntityType entityType, double height, double offsetX, double offsetZ) {
        this.entityType = entityType;
        this.height = height;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
    }

    /**
     * Returns the location modifier vector, with the
     * x, y and z offsets.
     * @return {@link Vector}
     */
    public Vector getLocationModifier() {
        return new Vector(this.getOffsetX(), this.getHeight() + 1.9D, this.getOffsetZ());
    }

    /**
     * Returns the height of the enum item
     * @return {@link Double}
     */
    protected double getHeight() {
        return this.height;
    }

    /**
     * Returns the Offset X of the enum item.
     * Still not yet implemented.
     * @return {@link Double}
     */
    protected double getOffsetX() {
        return this.offsetX;
    }

    /**
     * Returns the Offset Z of the enum item.
     * Still not yet implemented.
     * @return {@link Double}
     */
    protected double getOffsetZ() {
        return this.offsetZ;
    }

    /**
     * Returns the entity type of the
     * enum item.
     * @return {@link  EntityType}
     */
    public EntityType getEntityType() {
        return this.entityType;
    }

    /**
     * Returns the {@link EntityHitbox} enum object
     * based off of the {@link Entity} parameter.
     * Uses {@link Optional<T>} for better performance
     * overall.
     * @param entity Entity which we're comparing to.
     * @return {@link EntityHitbox}
     */
    public static EntityHitbox getFromEntity(Entity entity) {
        EntityType entityType = entity.getType();
        Optional<EntityHitbox> match = Arrays.stream(values()).filter(entityHitbox -> entityHitbox.getEntityType().equals(entityType)).findFirst();
        return match.orElse(ZOMBIE);
    }

    /**
     * Returns whether or not a certain
     * {@link Entity} exists in our
     * {@link EntityHitbox} Enum.
     * Uses {@link Optional<T>} for better performance
     * overall.
     * @param entity The entity parameter off of
     *               which we're basing our search.
     * @return {@link Boolean}
     */
    public static boolean exists(Entity entity) {
        EntityType entityType = entity.getType();
        Optional<EntityHitbox> match = Arrays.stream(values()).filter(entityHitbox -> entityHitbox.getEntityType().equals(entityType)).findFirst();
        return match.isPresent();
    }
}
