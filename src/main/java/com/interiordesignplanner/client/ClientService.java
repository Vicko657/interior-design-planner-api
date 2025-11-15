package com.interiordesignplanner.client;

import java.util.List;
import com.interiordesignplanner.mapper.ClientMapper;
import com.interiordesignplanner.project.ProjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.interiordesignplanner.exceptions.ClientNotFoundException;

/**
 * Client service class provides business logic and operations relating to a
 * client.
 * 
 * <p>
 * Creates, updates, retrieves and deletes client records, validating client
 * information, and managing relationships between clients and projects. Serves
 * as an interface between controllers and the persistence layer.
 * </p>
 */
@Service
public class ClientService {

    // Client CRUD Interface
    @Autowired
    public ClientRepository clientRepository;

    // Client Mapper
    @Autowired
    private final ClientMapper clientMapper;

    // Project Interface
    @Autowired
    private final ProjectRepository projectRepository;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper,
            ProjectRepository projectRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.projectRepository = projectRepository;
    }

    /**
     * Returns all active clients and their details on the system.
     */
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(client -> {
                    ClientDTO clientDTO = clientMapper.toDto(client);
                    clientDTO.setTotalProjects(projectRepository.countClientsProjects(client.getId()));
                    return clientDTO;
                })
                .toList();
    }

    /**
     * Returns a client using their clientId.
     * 
     * <p>
     * This method is used to retrieve a specific client
     * entity for CRUD operations.
     * </p>
     * 
     * @param id client's unique identifier
     * @throws ClientNotFoundException if the client is not found
     */
    public ClientDTO getClientById(Long id) {

        Client client = findClient(id);
        ClientDTO clientDTO = clientMapper.toDto(client);
        clientDTO.setTotalProjects(projectRepository.countClientsProjects(id));
        return clientDTO;
    }

    /**
     * Retrives a client using their lastname.
     * 
     * <p>
     * Retrieves a specific client entity by
     * their lastName. Custom query created in the repository.
     * </p>
     * 
     * @param lastName client's lastname
     * @throws ClientNotFoundException if the client is not found
     */
    public ClientDTO getClientsByLastName(String lastName) {

        Client client = clientRepository.findByLastNameIgnoreCase(lastName)
                .orElseThrow(() -> new ClientNotFoundException(
                        "lastName", lastName));

        return clientMapper.toDto(client);
    }

    /**
     * Create a new client on the system.
     * 
     * <p>
     * Used to register a new client on the system and
     * automatically assigned a unique identifier.
     * </p>
     * 
     * @param ClientCreateDTO the client object is created
     * @return client with a generated unique Id
     */
    public ClientDTO createClient(ClientCreateDTO clientCreateDTO) {

        Client client = clientMapper.toEntity(clientCreateDTO);
        Client savedClient = clientRepository.save(client);
        return clientMapper.toDto(savedClient);
    }

    /**
     * Updates existing client details on the system.
     * 
     * <p>
     * Use this method to modify details of a existing client on
     * the system and automatically assigned a unique identifier.
     * </p>
     * 
     * @param ClientUpdateDTO the client object is updated
     * @throws ClientNotFoundException if the client is not found
     * @return the updated client object
     */
    public ClientDTO updateClient(Long id, ClientUpdateDTO clientUpdateDTO) {

        Client existingClient = findClient(id);
        clientMapper.updateEntity(clientUpdateDTO, existingClient);
        return clientMapper.toDto(clientRepository.save(existingClient));
    }

    /**
     * Deletes client details from the system.
     * 
     * <p>
     * This method is used to remove a non active client.
     * The linked projects would also need to handled.
     * </p>
     * 
     * @param id retrieves the client object to be deleted
     * @throws ClientNotFoundException if the client is not found
     * @return client is removed
     */
    public void deleteClient(Long id) {
        Client client = findClient(id);
        clientRepository.delete(client);
    }

    /**
     * Retrieved the Client's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the client object to be deleted
     * @throws ClientNotFoundException if the client is not found
     * @return the client
     */
    public Client findClient(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException("clientId", id));
    }

}
