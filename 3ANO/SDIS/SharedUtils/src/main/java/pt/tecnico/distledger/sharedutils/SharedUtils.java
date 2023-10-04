package pt.tecnico.distledger.sharedutils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;


import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger.*;
import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerServiceGrpc;
public class SharedUtils {

    /**
     * This method takes a service name and a qualifier, and returns a mapping of the
     * qualifier to a list of server addresses of that qualifier.
     *
     * @param serviceName The name of the service being looked up.
     * @param qualifier The qualifier being looked up.
     * @return A mapping of the qualifier to a list of server addresses of that qualifier.
     */
    public static Map<String,List<String>> lookup(String serviceName, String qualifier){

        // Create a channel to communicate with the naming server
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost",5001).usePlaintext().build();

        // Create a blocking stub to call the lookup method on the naming server
        NamingServerServiceGrpc.NamingServerServiceBlockingStub namingServerBlockingStub = NamingServerServiceGrpc.newBlockingStub(channel);

        // Maps a server qualifier to the list of server addresses of that qualifier
        Map<String,List<String>> qualifierServers = new HashMap<String, List<String>>();

        // Call the lookup method on the naming server and get the response
        LookupResponse lookupResponse = namingServerBlockingStub.lookup(
                LookupRequest.newBuilder()
                        .setRequestedService(serviceName)
                        .setQualifier(qualifier)
                        .build()
        );

        // Loop through the list of server info objects returned by the lookup response
        for(ServerInfo serverInfo : lookupResponse.getServerInfoList()){

            // If the qualifier is already in the map, add the address to the corresponding list
            if(qualifierServers.containsKey(serverInfo.getQualifier())){
                qualifierServers.get(serverInfo.getQualifier()).add(serverInfo.getAddress());
            }
            // Otherwise, create a new list and map it to the qualifier in the map
            else{
                List<String> servers = new ArrayList<String>();
                servers.add(serverInfo.getAddress());
                qualifierServers.put(serverInfo.getQualifier(),servers);
            }
        }

        // Close the channel
        channel.shutdownNow();

        // Return the mapping of the qualifier to a list of server addresses of that qualifier
        return qualifierServers;
    }

    /**
     * This method prints a debug message to the console if the flag is set to true.
     *
     * @param debugMessage The debug message to print to the console.
     * @param flag A boolean indicating whether to print the debug message or not.
     */
    public static void debug(String debugMessage, boolean flag){
        if(flag){
            System.err.println("[DEBUG]: " + debugMessage);
        }
    }

    /**
     * This method checks if an address is a valid host:port combination.
     *
     * @param address The address to check.
     * @return A boolean indicating whether the address is a valid host:port combination or not.
     */
    public static boolean isValidAddress(String address){

        // Define the regex pattern for a valid host name or IP address
        String hostPattern = "(localhost)|([a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)*)";

        // Define the regex pattern for a valid port number
        String portPattern = "\\d{1,5}";

        // Combine the host and port patterns to form a combined host:port pattern
        String hostPortPattern = hostPattern + ":" + portPattern;

        // Compile the pattern into a regular expression
        Pattern pattern = Pattern.compile(hostPortPattern);

        // Match the regular expression against the input address
        Matcher matcher = pattern.matcher(address);

        // Check if the address matches the pattern and the port number is valid
        if (matcher.matches()) {
            int port = Integer.parseInt(matcher.group(0).split(":")[1]);

            // Check if the port number is within the valid range of port numbers (0 to 65535)
            if (port >= 0 && port <=65535) {
                return true;
            }
        }
        // If the address does not match the pattern or the port number is invalid, return false
        return false;
    }
}
