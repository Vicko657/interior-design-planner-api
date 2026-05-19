package com.interiordesignplanner.authentication;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by username.
     *
     * @param username the username of the user
     * @return the user if found, otherwise empty
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by emailAddress or phoneNumber.
     *
     * @param emailAddress the emailAddress of the user
     * @param phoneNumber  the phoneNumber of the user
     * @return the user if found, otherwise empty
     */
    Optional<User> findByEmailAddressOrPhoneNumber(String emailAddress, String phoneNumber);

}
