package com.interiordesignplanner.client;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Rest Controller for managing clients
 * 
 * API endpoints to complete CRUD operations.
 */
@RestController
@RequestMapping("/api/clients")
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
    @Tag(name = "clients", description = "Information about the clients")
    @Operation(summary = "Retrieves all clients", description = "Retrieves all the clients details, including their name, email, phoneNo, address, projects and other details")
    @ApiResponse(responseCode = "200", description = "All clients are found")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public List<ClientDTO> getAllClients() {
        return clientService.getAllClients();
    }

    /**
     * GET: Returns Client with Id
     * 
     * @param id the client's unique identifier
     * @return client's entity
     * @response 200 if client was successfully found
     * @response 404 not found is the client doesnt exist
     */
    @Tag(name = "clients", description = "Information about the clients")
    @Operation(summary = "Finds client by ID", description = "Returns one clients details, including their name, email, phoneNo, address, projects and other details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client with id was found"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist") })
    @GetMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {

        ClientDTO client = clientService.getClientById(id);
        return ResponseEntity.ok(client);
    }

    /**
     * GET: Returns Client with LastName
     * 
     * @param lastName the client's lastname
     * @return client's entity
     * @response 200 if client was successfully found
     * @response 404 not found is the client doesnt exist
     */
    @Tag(name = "clients", description = "Information about the clients")
    @Operation(summary = "Finds client by lastname", description = "Returns the client details, including their name, email, phoneNo, address, projects and other details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client with lastname was found"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist") })
    @GetMapping(value = "/lastName/{lastName}", produces = "application/json")
    public ResponseEntity<ClientDTO> getClientsByLastName(@PathVariable("lastName") String lastName) {

        ClientDTO client = clientService.getClientsByLastName(lastName);
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
    @Tag(name = "clients", description = "Information about the clients")
    @Operation(summary = "Create a new client", description = "Creates a new client and add's their details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client was created"),
            @ApiResponse(responseCode = "404", description = "Client columns have not been filled") })
    @PostMapping(produces = "application/json")
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientCreateDTO clientCreateDTO) {

        ClientDTO savedClient = clientService.createClient(clientCreateDTO);
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
    @Tag(name = "clients", description = "Information about the clients")
    @Operation(summary = "Update client", description = "Updates the client's records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client with id was updated"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist") })
    @PutMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id,
            @Valid @RequestBody ClientUpdateDTO clientUpdateDTO) {

        ClientDTO updatedClient = clientService.updateClient(id, clientUpdateDTO);
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
    @Tag(name = "clients", description = "Information about the clients")
    @Operation(summary = "Deletes client", description = "Deletes the client's records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client with id was deleted"),
            @ApiResponse(responseCode = "404", description = "Client doesn't exist") })
    @DeleteMapping(value = "/{id}", produces = "application/json")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {

        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

}
