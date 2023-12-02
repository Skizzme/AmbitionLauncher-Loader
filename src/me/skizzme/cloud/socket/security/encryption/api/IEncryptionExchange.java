package me.skizzme.cloud.socket.security.encryption.api;

/**
 * Defines the contract for an encryption exchange interface, allowing for secure
 * encryption and decryption operations on data.
 */
public interface IEncryptionExchange
{

    /**
     * Encrypts the provided data, ensuring its confidentiality and security.
     *
     * @param data The plaintext data to be encrypted.
     * @return The encrypted data in a format suitable for secure transmission or storage.
     */
    byte[] encrypt(final String data);

    /**
     * Decrypts the encrypted data, restoring it to its original plaintext form.
     *
     * @param data The encrypted data to be decrypted.
     * @return The decrypted plaintext data.
     */
    byte[] decrypt(final byte[] data);

}
