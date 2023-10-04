package pt.tecnico.distledger.namingserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import pt.tecnico.distledger.sharedutils.SharedUtils;
import pt.tecnico.distledger.namingserver.exceptions.*;


public class NamingServerServices {
    private ConcurrentMap<String,ServiceEntry> services;
    private boolean debug;

    /**
     * Creates a new instance of the NamingServerServices class.
     */
    public NamingServerServices(boolean debug){
        this.services = new ConcurrentHashMap<>();
        this.debug = debug;
    }

    /**
     * Registers a new server address for the given service name and qualifier.
     *
     * @param serviceName the name of the service to register
     * @param qualifier   the qualifier for the server address
     * @param address     the server address to register
     * @throws InvalidServiceArgumentsException     if the service name, qualifier, or address is invalid
     * @throws ServerAddressAlreadyExistsException if the server address already exists for the given service name and qualifier
     */
    public void register(String serviceName, String qualifier, String address) throws InvalidServiceArgumentsException, ServerAddressAlreadyExistsException {
        SharedUtils.debug("register acquiring lock of naming server services.", debug);
        SharedUtils.debug("Registering server type '"+ qualifier + "' with address '" + address + "' for service '"+ serviceName + "'.", debug);
        ServiceEntry serviceEntry;
        if(serviceName == null || serviceName.isEmpty() || serviceName.isBlank()){
            SharedUtils.debug("Registering server type '"+ qualifier + "' with address '" + address + "' for service '"+ serviceName + "' failed because of invalid service name.", debug);
            SharedUtils.debug("register releasing lock of naming server services.", debug);
            throw new InvalidServiceArgumentsException("Invalid service name.");
        }
        if(qualifier == null || qualifier.isEmpty() || qualifier.isBlank() || (!qualifier.equals("A") && !qualifier.equals("B") && !qualifier.equals("C"))){
            SharedUtils.debug("Registering server type '"+ qualifier + "' with address '" + address + "' for service '"+ serviceName + "' failed because of invalid qualifier.", debug);
            SharedUtils.debug("register releasing lock of naming server services.", debug);
            throw new InvalidServiceArgumentsException("Invalid qualifier.");
        }
        if(!SharedUtils.isValidAddress(address)){
            SharedUtils.debug("Registering server type '"+ qualifier + "' with address '" + address + "' for service '"+ serviceName + "' failed because of invalid address.", debug);
            SharedUtils.debug("register releasing lock of naming server services.", debug);
            throw new InvalidServiceArgumentsException("Invalid address.");
        }

        serviceEntry = services.computeIfAbsent(serviceName, v -> new ServiceEntry(serviceName, debug));
        serviceEntry.addServerEntry(qualifier, address);
        SharedUtils.debug("Server type '"+ qualifier + "' with address '" + address + "' for service '"+ serviceName + "' registration completed successfully.", debug);
        SharedUtils.debug("register releasing lock of naming server services.", debug);
    }

    /**
     * Looks up server addresses for the given service name and qualifier.
     *
     * @param serviceName the name of the service to look up
     * @param qualifier   the qualifier for the server address (optional)
     * @return the set of server entries for the given service name and qualifier (if provided), or all server entries for the given service name (if qualifier is null)
     */
    public Set<ServerEntry> lookup(String serviceName, String qualifier){
        SharedUtils.debug("lookup acquiring lock of naming server services.", debug);
        SharedUtils.debug("Looking for address of server type '"+ qualifier + "' for service '"+ serviceName + "'.", debug);
        SharedUtils.debug("lookup releasing lock of naming server services.", debug);
        return qualifier!=null ?
                services.get(serviceName).getServerEntries()
                        .stream()
                        .filter(
                                serverEntry -> serverEntry.getQualifier().equals(qualifier)
                        )
                        .collect(Collectors.toSet()) :
                services.get(serviceName).getServerEntries();
    }

    /**
     * Deletes the server address for the given service name and address.
     *
     * @param serviceName the name of the service to delete the server address from
     * @param address     the server address to delete
     * @throws ServiceNameNotFoundException    if the service name does not exist
     * @throws ServerAddressNotFoundException if the server address does not exist for the given service name
     */
    public void delete(String serviceName, String address) throws ServiceNameNotFoundException, ServerAddressNotFoundException {
        SharedUtils.debug("delete acquiring lock of naming server services.", debug);
        SharedUtils.debug("Deleting server for address '"+ address + "'.", debug);
        if(!services.containsKey(serviceName)){
            SharedUtils.debug("Deleting server for address '"+ address + "' failed because service name '"+ serviceName +"' does not exist.", debug);
            SharedUtils.debug("delete releasing lock of naming server services.", debug);
            throw new ServiceNameNotFoundException("Service name does not exist.");
        }

        services.get(serviceName).deleteServerEntry(address);
        SharedUtils.debug("Server for address '"+ address + "' removal completed successfully.", debug);
        SharedUtils.debug("delete releasing lock of naming server services.", debug);
    }
}
