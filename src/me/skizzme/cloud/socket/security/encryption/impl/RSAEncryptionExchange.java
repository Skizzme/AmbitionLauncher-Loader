package me.skizzme.cloud.socket.security.encryption.impl;


import me.skizzme.cloud.socket.security.encryption.api.IEncryptionExchange;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public final class RSAEncryptionExchange implements IEncryptionExchange
{

    private static final String CIPHER = "RSA/ECB/PKCS1Padding", ALGORITHM = "RSA";

    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    public RSAEncryptionExchange(final PublicKey publicKey, final PrivateKey privateKey)
    {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public RSAEncryptionExchange(final KeyPair keyPair)
    {
        this(keyPair.getPublic(), keyPair.getPrivate());
    }

    @Override
    public byte[] encrypt(final String data)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, this.publicKey);

            return cipher.doFinal(data.getBytes());
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] decrypt(final byte[] data)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, this.privateKey);

            return cipher.doFinal(data);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Generates a new cryptographic key pair for secure communication using the specified key size.
     *
     * @param size The size (in bits) of the key pair to generate.
     * @return A newly generated key pair with public and private keys, or null in case of an exception.
     */
    public static KeyPair newKeyPair(final int size)
    {
        try
        {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(size);

            return keyPairGenerator.generateKeyPair();
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    public static String encode(final PublicKey publicKey)
    {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public static PublicKey decode(final String publicKey)
    {
        try
        {
            final KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
            return keyFactory.generatePublic(keySpec);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

}
