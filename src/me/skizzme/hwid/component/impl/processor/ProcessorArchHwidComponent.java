package me.skizzme.hwid.component.impl.processor;


import me.skizzme.hwid.component.HwidComponent;

public final class ProcessorArchHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "PROCESS_ARCH";
    }

    @Override
    public String lookup()
    {
        return System.getenv("PROCESSOR_ARCHITECTURE");
    }

}