package me.skizzme.hwid.component.impl.user;


import me.skizzme.hwid.component.HwidComponent;

public final class UserDomainHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "USER_DOMAIN";
    }

    @Override
    public String lookup()
    {
        return System.getenv("USERDOMAIN");
    }
    
}