package ink.sakurarealm.multirobot.api;

import ink.sakurarealm.multirobot.LoadClass;
import ink.sakurarealm.multirobot.socket.SocketClient;

public class robotAPI {

    public static LoadClass api;
    public static SocketClient socket;

    public robotAPI(LoadClass pl){
        api = pl;
        socket = pl.client;
    }

    public static LoadClass getApi(){
        return api;
    }

    public static SocketClient getSocket(){
        return socket;
    }
}
