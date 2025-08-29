package com.littlepeople.core.interfaces;

import java.util.UUID;

/**
 * Base interface for all simulation entities.
 *
 * <p>This interface defines the fundamental contract that all entities
 * within the LittlePeople simulation must implement. It provides a
 * unique identifier for each entity and serves as the foundation
 * for the entity hierarchy.</p>
 *
 * <p>Design Pattern: This interface supports the Entity pattern,
 * ensuring all domain objects have a consistent identity mechanism.</p>
 *
 * @since 1.0.0
 * @version 1.0.0
 */
public interface Entity {

    /**
     * Returns the unique identifier for this entity.
     *
     * <p>Each entity must have a globally unique identifier that
     * remains constant throughout the entity's lifecycle. This
     * identifier is used for equality, hashing, and persistence
     * operations.</p>
     *
     * @return the unique identifier for this entity, never null
     */
    UUID getId();
}
