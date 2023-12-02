package me.skizzme.cloud.socket.packet;

public class Packets
{

    public static final int
            KEEP_ALIVE = 0x0,
            HANDSHAKE = 0x1,
            AUTHORIZATION = 0x2,
            AUTHORIZATION_RESPONSE = 0x3,
            EDIT_USER = 0x4,
            USER_INFORMATION = 0x5,
            LOCALUSER_INFORMATION = 0x6,
            UPLOAD_FILE = 0x7,
            REQUEST_FILE = 0x8,
            FILE = 0x9,
            RESPONSE = 0x10,
            REQUEST_STORAGE_USED = 0x11,
            REQUEST_ALL_FILES = 0x12,
            DELETE_FILE = 0x13,
            CREATE_SHARE_KEY = 0x14,
            REQUEST_FILE_SHARE_KEYS = 0x15,
            ADD_MESSAGE = 0x16,
            REQUEST_MESSAGE_HISTORY = 0x17,
            REQUEST_USER_INFORMATION = 0x18,
            CHANNEL_DATA = 0x19,
            REQUEST_CHANNEL_INFORMATION = 0x20,
            REMOVE_MESSAGE = 0x21,
            CREATE_CHANNEL = 0x22,
            UPDATE_SESSION = 0x23,
            ADD_FRIEND = 0x24,
            REMOVE_FRIEND = 0x25,
            CLASSES = 0x26;

}