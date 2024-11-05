package me.skizzme.hwid.component.impl.processor;


import me.skizzme.hwid.component.HwidComponent;

public final class ProcessorCountHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "PROCESSOR_COUNT";
    }

    @Override
    public String lookup()
    {
        return System.getenv("NUMBER_OF_PROCESSORS");
    }
    
}