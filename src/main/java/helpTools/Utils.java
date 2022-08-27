package helpTools;

import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import server.KVServer;

import java.io.IOException;
import java.time.LocalDateTime;

public class Utils {
    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }

    public static KVServer getKVServer() throws IOException {
        KVServer kvServer = new KVServer();
        kvServer.start();
        return kvServer;
    }
}
