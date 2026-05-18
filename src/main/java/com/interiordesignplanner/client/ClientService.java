package com.interiordesignplanner.client;

import com.interiordesignplanner.mapper.ClientMapper;

import io.github.perplexhub.rsql.RSQLJPASupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.interiordesignplanner.authentication.User;
import com.interiordesignplanner.authentication.UserRepository;
import com.interiordesignplanner.designer.Designer;
import com.interiordesignplanner.designer.DesignerRepository;
import com.interiordesignplanner.exceptions.ClientNotFoundException;
import com.interiordesignplanner.exceptions.UserNotFoundException;

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
    private ClientMapper clientMapper;

    private final UserRepository userRepository;

    private final DesignerRepository designerRepository;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper, UserRepository userRepository,
            DesignerRepository designerRepository) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
        this.userRepository = userRepository;
        this.designerRepository = designerRepository;
    }

    /**
     * Returns all active clients and their details on the system.
     * 
     * *
     * <p>
     * The RSQL plugin automatically builds the JPA specification with
     * less code and provides filtering support for power users.
     * 
     * Used the plugin recommended which saved time:
     * {@link https://github.com/perplexhub/rsql-jpa-specification}
     * </p>
     * 
     * @return all clients on the system
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ClientDTO> getAllClients(String filter, Pageable pageable) {

        Page<Client> clients;

        if (filter != null) {
            Specification<Client> specfication = RSQLJPASupport.toSpecification(filter);
            clients = clientRepository.findAll(specfication, pageable);
        } else {
            clients = clientRepository.findAll(pageable);
        }

        return clients.map(client -> {
            ClientDTO clientDTO = clientMapper.toDto(client);
            return clientDTO;
        });

    }

    /**
     * Returns the designer's list of clients and their details on the system.
     * 
     * @param username retrieves the client object to be deleted
     * @throws UsernameNotFoundException if the user is not found
     * @throws DesignerNotFoundException if the designer is not found
     * @return logged in designer's list of clients
     */
    @PreAuthorize("hasRole('DESIGNER')")
    public Page<ClientSummaryDTO> getClientsByDesigner(String username, Pageable pageable) {

        User user = findUser(username);
        Designer designer = findDesigner(user.getId());

        return clientRepository.findClientsByDesignerId(designer.getId(), pageable);
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
     * @return client
     */
    @PreAuthorize("hasRole('ADMIN')")
    public ClientDTO getClientById(Long id) {

        Client client = findClient(id);
        ClientDTO clientDTO = clientMapper.toDto(client);
        return clientDTO;
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
    @PreAuthorize("hasRole('DESIGNER')")
    public ClientDTO createClient(ClientCreateDTO clientCreateDTO, String username) {

        // Finds the designer and assigns the user to the new client
        User user = findUser(username);
        Designer designer = findDesigner(user.getId());

        clientCreateDTO.setDesigner(designer);

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
    @PreAuthorize("hasRole('DESIGNER')")
    public ClientDTO updateClient(Long id, ClientUpdateDTO clientUpdateDTO, String username) {

        Client existingClient = findClient(id);

        if (existingClient.getDesigner().getUser().getUsername() != username) {
            throw new AccessDeniedException("User does not have authorization");
        }

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
    @PreAuthorize("hasRole('ADMIN')")
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

    /**
     * Retrieved the User's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the user object to be deleted
     * @throws UserNotFoundException if the user is not found
     * @return the user
     */
    public User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User is not found"));
    }

    /**
     * Retrieved the Designer's entity
     * 
     * Reduces code repetition
     * 
     * @param id retrieves the user object to be deleted
     * @throws UserNotFoundException if the user is not found
     * @return the user
     */
    public Designer findDesigner(Long userId) {
        return designerRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("userId", userId));
    }

}
