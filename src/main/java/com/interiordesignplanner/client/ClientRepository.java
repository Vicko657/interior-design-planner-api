package com.interiordesignplanner.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Client} entities.
 * <p>
 * Provides custom CRUD operations and query methods for accessing client data.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, JpaSpecificationExecutor<Client> {

    /**
     * Finds all clients for the logged in user with pagination.
     *
     * @param userId   the designers unique identification
     * @param pageable pagination info
     * @return paginated list of clients
     */
    @Query("SELECT new com.interiordesignplanner.client.ClientSummaryDTO(c.id, CONCAT(c.firstName,' ', c.lastName), c.email, c.phone, c.address, COUNT(p), c.notes) FROM Client c LEFT JOIN c.projects p LEFT JOIN c.designer d LEFT JOIN d.user u WHERE c.designer.id = :userId GROUP BY c.id")
    Page<ClientSummaryDTO> findClientsByDesignerId(@Param("userId") Long userId, Pageable pageable);
}
