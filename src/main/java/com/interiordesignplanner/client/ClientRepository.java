package com.interiordesignplanner.client;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Client} entities.
 * <p>
 * Provides custom CRUD operations and query methods for accessing client data.
 */
@Repository
public interface ClientRepository extends ListCrudRepository<Client, Long> {

    /**
     * Finds a client by last name.
     *
     * @param lastName the last name of the client
     * @return the client if found, otherwise empty
     */
    Client findByLastNameIgnoreCase(String lastName);

}
