package me.skizzme.hwid.component.impl.processor;


import me.skizzme.hwid.component.HwidComponent;

public final class ProcessorIdHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "PROCESSOR_ID";
    }

    @Override
    public String lookup()
    {
        return System.getenv("PROCESSOR_IDENTIFIER");
    }
    
}