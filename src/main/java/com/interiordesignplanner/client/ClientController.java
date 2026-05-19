package com.interiordesignplanner.client;

import org.springframework.web.bind.annotation.RestController;

import com.interiordesignplanner.security.ApplicationUserDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Rest Controller for managing clients
 * 
 * API endpoints to complete CRUD operations.
 */
@Tag(name = "Clients", description = "Information about the clients")
@Validated
@RestController
@RequestMapping("/api")
public class ClientController {

    // Client Service layer
    @Autowired
    public ClientService clientService;

    /**
     * GET: Returns all Clients
     * 
     * @return all clients entities on the system
     * @response 200 if all clients are found
     */
    @Operation(summary = "Retrieves all clients", description = "Retrieves all the clients details, including their name, email, phoneNo, address, projects and other details")
    @ApiResponse(responseCode = "200", description = "All clients are found")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/admin/clients", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClientDTO>> getAllClients(
            @Valid @RequestParam(required = false) String filter,
            Pageable pageable) {
        return ResponseEntity.ok(clientService.getAllClients(filter, pageable));
    }

    /**
     * GET: Returns all Clients
     * 
     * @return all clients entities on the system
     * @response 200 if all clients are found
     */
    @Operation(summary = "Retrieves all clients", description = "Retrieves all the clients details, including their name, email, phoneNo, address, projects and other details")
    @ApiResponse(responseCode = "200", description = "All clients are found")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/clients", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<ClientSummaryDTO> getClients(@AuthenticationPrincipal ApplicationUserDetails applicationUserDetails,
            Pageable pageable) {
        return clientService.getClientsByDesigner(applicationUserDetails.getUsername(), pageable);
    }

    /**
     * GET: Returns Client with Id
     * 
     * @param id the client's unique identifier
     * @return client's entity
     * @response 200 if client was successfully found
     * @response 404 not found is the client doesnt exist
     */
    @Operation(summary = "Finds client by ID", description = "Returns one clients details, including their name, email, phoneNo, address, projects and other details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client with id was found"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist") })
    @GetMapping(value = "/admin/clients/{id}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {

        ClientDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    /**
     * POST: Creates a new Client
     * 
     * @param client the client's object to be created
     * @return saved client with generated unique identifier
     * @response 201 if client was successfully created
     * @response 404 bad request is input data is invalid
     */
    @Operation(summary = "Create a new client", description = "Creates a new client and add's their details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client was created"),
            @ApiResponse(responseCode = "404", description = "Client columns have not been filled") })
    @PostMapping(value = "/clients", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientCreateDTO clientCreateDTO,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        ClientDTO savedClient = clientService.createClient(clientCreateDTO, applicationUserDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);

    }

    /**
     * PUT: Updates existing client details
     * 
     * @param id           the client's unique identifier
     * @param updateClient the client's object to be updated
     * @return updated client entity
     * @response 200 if client was successfully updated
     * @response 404 not found is the client doesnt exist
     */
    @Operation(summary = "Update client", description = "Updates the client's records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client with id was updated"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist") })
    @PutMapping(value = "/clients/{id}", produces = "application/json")
    @PreAuthorize("hasRole('DESIGNER')")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id,
            @Valid @RequestBody ClientUpdateDTO clientUpdateDTO,
            @AuthenticationPrincipal ApplicationUserDetails applicationUserDetails) {

        ClientDTO updatedClient = clientService.updateClient(id, clientUpdateDTO, applicationUserDetails.getUsername());
        return ResponseEntity.ok(updatedClient);

    }

    /**
     * DELETE: Deletes existing Client
     * 
     * @param id the client's unique identifier
     * @return deleted client entity off the system
     * @response 204 if client was successfully deleted
     * @response 404 Not found is the client doesnt exist
     */
    @Operation(summary = "Deletes client", description = "Deletes the client's records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client with id was deleted"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist") })
    @DeleteMapping(value = "admin/clients/{id}", produces = "application/json")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {

        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

}
