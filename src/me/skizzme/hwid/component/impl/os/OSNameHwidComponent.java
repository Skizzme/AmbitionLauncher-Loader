package me.skizzme.hwid.component.impl.os;


import me.skizzme.hwid.component.HwidComponent;

public final class OSNameHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "OS_NAME";
    }

    @Override
    public String lookup()
    {
        return System.getProperty("os.name");
    }
    
}