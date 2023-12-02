package me.skizzme.cloud.socket.security.hashing.api;

/**
 * Defines the contract for a hash exchange interface, facilitating the generation
 * of cryptographic hash values for data.
 */
public interface IHashExchange
{

    /**
     * Computes the cryptographic hash of the input data using a specific hashing algorithm.
     *
     * @param data The input data to be hashed.
     * @return The hashed data in a irreversible form for highly sensitive data.
     */
    String hash(final String data);

}
