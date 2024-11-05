package me.skizzme.hwid.component.impl.misc;


import me.skizzme.hwid.component.HwidComponent;

public final class ComputerNameHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "COMPUTER_NAME";
    }

    @Override
    public String lookup()
    {
        return System.getenv("COMPUTERNAME");
    }
    
}