package com.interiordesignplanner.room;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.interiordesignplanner.dashboard.DashboardDTO.RecentTasks;

/**
 * Repository interface for managing {@link Room} entities.
 *
 * <p>
 * Provides custom CRUD operations and query methods for accessing project data.
 * </p>
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    /**
     * Finds a room by type.
     *
     * @param type the type of room
     * @return an {@link List} containing all the rooms with the specific type if
     *         found, otherwise empty
     */
    Page<Room> findRoomsByType(RoomType type, Pageable pageable);

    @Query("SELECT new com.interiordesignplanner.dashboard.DashboardDTO$RecentTasks(t.taskName, t.completed, p.projectName) FROM Room r JOIN r.checklist t LEFT JOIN r.project p LEFT JOIN p.client c LEFT JOIN c.designer d LEFT JOIN d.user u WHERE c.designer.id = :userId ORDER BY t.date ASC ")
    List<RecentTasks> findRecentTasks(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT SUM(i.price * i.quantity) FROM Room r JOIN r.inventory i WHERE r.id = :roomId")
    BigDecimal findTotalInventory(@Param("roomId") Long roomId);

}
