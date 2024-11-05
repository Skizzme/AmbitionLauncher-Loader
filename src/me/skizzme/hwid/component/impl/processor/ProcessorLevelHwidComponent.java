package me.skizzme.hwid.component.impl.processor;


import me.skizzme.hwid.component.HwidComponent;

public final class ProcessorLevelHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "PROCESSOR_LEVEL";
    }

    @Override
    public String lookup()
    {
        return System.getenv("PROCESSOR_LEVEL");
    }
    
}