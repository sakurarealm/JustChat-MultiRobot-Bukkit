package ink.sakuralream.multirobot.api;

import ink.sakuralream.multirobot.LoadClass;
import ink.sakuralream.multirobot.socket.SocketClient;

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
