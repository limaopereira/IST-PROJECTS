package pt.tecnico.distledger.namingserver;

import pt.tecnico.distledger.namingserver.exceptions.ServerAddressAlreadyExistsException;
import pt.tecnico.distledger.namingserver.exceptions.ServerAddressNotFoundException;
import pt.tecnico.distledger.sharedutils.SharedUtils;

import java.util.Set;
import java.util.HashSet;


public class ServiceEntry {
    private String serviceName;
    private Set<ServerEntry> serverEntries;
    private boolean debug;

    /**
     * Constructs a new ServiceEntry object with the given service name.
     *
     * @param serviceName the name of the service
     */
    public ServiceEntry(String serviceName, boolean debug){
        this.serviceName = serviceName;
        this.serverEntries = new HashSet<>();
        this.debug = debug;
    }

    /**
     * Returns the set of server entries that provide the service.
     *
     * @return the set of server entries
     */
    public Set<ServerEntry> getServerEntries() {
        return serverEntries;
    }

    /**
     * Adds a new server entry for the service.
     *
     * @param qualifier the qualifier of the server entry
     * @param address the address of the server entry
     * @throws ServerAddressAlreadyExistsException if the server address is already registered for the service
     */
    public void addServerEntry(String qualifier, String address) throws ServerAddressAlreadyExistsException {
        ServerEntry serverEntry = new ServerEntry(qualifier,address);
        if(serverEntries.contains(serverEntry)){
            SharedUtils.debug("Registering server type '"+ qualifier + "' with address '" + address + "' for service '"+ serviceName + "' failed because server address for service name already exists.", debug);
            SharedUtils.debug("register releasing lock of naming server services.", debug);
            throw new ServerAddressAlreadyExistsException("Server address is already registered for service '" + serviceName + "'.");
        }
        else {
            serverEntries.add(serverEntry);
        }
    }

    /**
     * Deletes a server entry for the service.
     *
     * @param address the address of the server entry to be deleted
     * @throws ServerAddressNotFoundException if the server address is not registered for the service
     */
    public void deleteServerEntry(String address) throws ServerAddressNotFoundException {
        ServerEntry serverEntry = new ServerEntry(null,address);
        if(!serverEntries.contains(serverEntry)){
            SharedUtils.debug("Deleting server for address '"+ address + "' failed because server address is not registered for service name '"+ serviceName +"'.", debug);
            SharedUtils.debug("delete releasing lock of naming server services.", debug);
            throw new ServerAddressNotFoundException("Server address is not registered for service '" + serviceName + "'.");
        }
        serverEntries.remove(serverEntry);
    }
}
