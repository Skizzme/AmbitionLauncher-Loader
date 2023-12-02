package me.skizzme.cloud.socket.security.hashing.impl;


import me.skizzme.util.StringUtils;
import me.skizzme.cloud.socket.security.hashing.api.IHashExchange;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public final class MD5HashExchange implements IHashExchange
{

    public static final MD5HashExchange INSTANCE = new MD5HashExchange();

    private static final String ALGORITHM = "MD5";

    @Override
    public String hash(final String data)
    {
        try
        {
            final MessageDigest messageDigest = MessageDigest.getInstance(ALGORITHM);
            messageDigest.update(data.getBytes(StandardCharsets.UTF_8));
            return StringUtils.bytesToHex(messageDigest.digest(data.getBytes(StandardCharsets.UTF_8)));
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }

        return null;
    }

}
