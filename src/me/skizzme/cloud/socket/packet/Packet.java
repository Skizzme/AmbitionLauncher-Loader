package me.skizzme.cloud.socket.packet;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Packet
{

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().serializeNulls().create();

    private final int id;
    private final String body;
    private String salt;

    public Packet(final int id, final String data, String salt)
    {
        this.id = id;
        this.body = data;
        this.salt = salt;
    }

    public Packet(final int id, final String data)
    {
        this.id = id;
        this.body = data;
    }

    public Packet(final int id, final JsonElement element)
    {
        this(id, GSON.toJson(element));
    }

    public Packet(final int id)
    {
        this(id, "");
    }

    public int getId()
    {
        return this.id;
    }

    public String getBody()
    {
        return this.body;
    }

    public JsonObject serialize()
    {
        final JsonObject object = new JsonObject();
        object.addProperty("id", this.id);
        if (this.salt != null) object.addProperty("salt", this.salt);
        object.add("body", this.jsonBody());
        return object;
    }

    public JsonObject jsonBody()
    {
        if (this.body != null)
        {
            final JsonElement element = GSON.fromJson(this.body, JsonElement.class);

            if (element != null && element.isJsonObject())
            {
                return element.getAsJsonObject();
            }
        }

        return null;
    }

    public static Packet read(final String data)
    {
        if (data.equals("")) {
            return null;
        }
        final JsonElement element = GSON.fromJson(data, JsonElement.class);

        if (element.isJsonObject())
        {
            final JsonObject object = element.getAsJsonObject();

            if (!object.has("salt")) {
                System.out.println("PACKET DID NOT HVE SALT");
                return null;
            }
            if (object.has("id") && object.has("body"))
            {
                return new Packet(object.get("id").getAsInt(), GSON.toJson(object.get("body")), object.get("salt").getAsString());
            }
        }

        return null;
    }

    public void setSalt(String salt) {
        if (this.salt == null) {
            this.salt = salt;
        } else{
            System.out.println("Tried setting salt that was already set!");
        }
    }

    public String getSalt() {
        return salt;
    }
}
