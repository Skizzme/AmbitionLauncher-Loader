package me.skizzme.hwid.component.impl.os;

import me.skizzme.hwid.component.HwidComponent;

public final class OSArchHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "OS_ARCH";
    }

    @Override
    public String lookup()
    {
        return System.getProperty("os.arch");
    }
    
}