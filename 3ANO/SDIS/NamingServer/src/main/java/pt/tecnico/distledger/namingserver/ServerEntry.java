package pt.tecnico.distledger.namingserver;

import pt.ulisboa.tecnico.distledger.contract.namingserver.NamingServerDistLedger;

public class ServerEntry {
    private String address;
    private String qualifier;

    /**
     * Creates a new server entry with the given qualifier and address.
     *
     * @param qualifier the qualifier of the server
     * @param address   the address of the server
     */
    public ServerEntry(String qualifier, String address){
        this.qualifier = qualifier;
        this.address = address;
    }

    /**
     * Returns the address of the server entry.
     *
     * @return the address of the server
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the qualifier of the server entry.
     *
     * @return the qualifier of the server
     */
    public String getQualifier() {
        return qualifier;
    }

    /**
     * Converts the server entry to a gRPC object.
     *
     * @return the gRPC object
     */
    public NamingServerDistLedger.ServerInfo toGrpc(){
        return NamingServerDistLedger.ServerInfo.newBuilder()
                .setAddress(
                        getAddress()
                )
                .setQualifier(
                        getQualifier()
                ).build();
    }

    /**
     * Compares this server entry to another object for equality. Returns true if and only if the other object is also
     * a server entry and both have the same address.
     *
     * @param o the other object to compare to
     * @return true if the other object is a server entry with the same address, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerEntry other = (ServerEntry) o;
        return this.getAddress() != null ? this.getAddress().equals(other.getAddress()) : other.getAddress() == null;
    }

    /**
     * Returns a hash code value for the server entry based on its address.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return this.getAddress() != null ? this.getAddress().hashCode() : 0;
    }

}
