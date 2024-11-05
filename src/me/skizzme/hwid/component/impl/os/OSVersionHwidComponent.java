package me.skizzme.hwid.component.impl.os;


import me.skizzme.hwid.component.HwidComponent;

public final class OSVersionHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "OS_VERSION";
    }

    @Override
    public String lookup()
    {
        return System.getProperty("os.version");
    }

}