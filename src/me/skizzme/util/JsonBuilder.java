package me.skizzme.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class JsonBuilder
{

    private final JsonObject object;
    public JsonBuilder()
    {
        this.object = new JsonObject();
    }

    public JsonBuilder addProperty(final String property, final Number value)
    {
        this.object.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(final String property, final Boolean value)
    {
        this.object.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(final String property, final Character value)
    {
        this.object.addProperty(property, value);
        return this;
    }

    public JsonBuilder addProperty(final String property, final String value)
    {
        this.object.addProperty(property, value);
        return this;
    }

    public JsonBuilder add(final String property, final JsonElement value)
    {
        this.object.add(property, value);
        return this;
    }

    public JsonObject build()
    {
        return this.object;
    }

}
