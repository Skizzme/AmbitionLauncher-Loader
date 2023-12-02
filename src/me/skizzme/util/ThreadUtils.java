package me.skizzme.util;

public final class ThreadUtils
{

    public static void sleep(final long ms)
    {
        try
        {
            Thread.sleep(ms);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
        }
    }

}
