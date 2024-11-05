package me.skizzme.hwid.component.impl.user;


import me.skizzme.hwid.component.HwidComponent;

public final class UserNameHwidComponent implements HwidComponent
{

    @Override
    public String id()
    {
        return "USER_NAME";
    }

    @Override
    public String lookup()
    {
        return System.getProperty("user.name");
    }
    
}