package com.interiordesignplanner.room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Room} entities.
 *
 * <p>
 * Provides custom CRUD operations and query methods for accessing project data.
 * </p>
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    /**
     * Finds a room by type.
     *
     * @param type the type of room
     * @return an {@link List} containing all the rooms with the specific type if
     *         found, otherwise empty
     */
    List<Room> findRoomsByType(RoomType type);

}
