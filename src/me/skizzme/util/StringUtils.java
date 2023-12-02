package me.skizzme.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Random;

public final class StringUtils
{
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

    public static String bytesToHex(final byte[] data)
    {
        if (data == null)
            return null;

        final char[] chars = new char[data.length * 2];

        for (int i = 0; i < data.length; i++)
        {
            final int hexValue = data[i] & 0xFF;

            chars[i * 2] = Character.forDigit(hexValue >>> 4, 16);
            chars[i * 2 + 1] = Character.forDigit(hexValue & 0x0F, 16);
        }

        return new String(chars);
    }

    public static JsonObject readString(final String string)
    {
        if (string != null)
        {
            final JsonElement element = GSON.fromJson(string, JsonElement.class);

            if (element != null && element.isJsonObject())
            {
                return element.getAsJsonObject();
            }
        }

        return null;
    }

    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        while (sb.length() < length) {
            int index = (int) (rnd.nextFloat() * chars.length());
            sb.append(chars.charAt(index));
        }
        String rndStr = sb.toString();
        return rndStr;
    }

}
