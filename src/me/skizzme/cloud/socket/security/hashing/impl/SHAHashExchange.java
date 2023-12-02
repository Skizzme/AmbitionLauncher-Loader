package me.skizzme.cloud.socket.security.hashing.impl;


import me.skizzme.cloud.socket.security.hashing.api.IHashExchange;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class SHAHashExchange implements IHashExchange
{

    public static final SHAHashExchange INSTANCE = new SHAHashExchange();

    private static final String ALGORITHM = "SHA-512";

    @Override
    public String hash(final String data)
    {
        if (data == null)
            return null;

        try
        {
            final MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            return Base64.getEncoder().encodeToString(messageDigest.digest(data.getBytes(StandardCharsets.UTF_8)));
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

}
