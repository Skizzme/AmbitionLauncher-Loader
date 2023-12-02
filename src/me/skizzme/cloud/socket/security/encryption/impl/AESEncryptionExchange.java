package me.skizzme.cloud.socket.security.encryption.impl;


import me.skizzme.cloud.socket.security.encryption.api.IEncryptionExchange;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public final class AESEncryptionExchange implements IEncryptionExchange
{

    private static final String CIPHER = "AES/ECB/PKCS5Padding", ALGORITHM = "AES";

    private final SecretKey key;
    public AESEncryptionExchange(final SecretKey key)
    {
        this.key = key;
    }

    @Override
    public byte[] encrypt(final String data)
    {
        try
        {
            final Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, this.key);

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
            cipher.init(Cipher.DECRYPT_MODE, this.key);

            return cipher.doFinal(data);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    /**
     * Generates a new cryptographic secret key of the specified size for encryption.
     *
     * @param size The size (in bits) of the key to generate.
     * @return A newly generated secret key suitable for encryption, or null in case of an exception.
     */
    public static SecretKey newKey(final int size)
    {
        try
        {
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(size);

            return keyGenerator.generateKey();
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

    public static String encode(final SecretKey secretKey)
    {
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public static SecretKey decode(final String secretKey)
    {
        try
        {
            return new SecretKeySpec(Base64.getDecoder().decode(secretKey), ALGORITHM);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

}
