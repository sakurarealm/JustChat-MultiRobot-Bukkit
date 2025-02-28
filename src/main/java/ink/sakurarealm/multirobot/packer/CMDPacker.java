package ink.sakurarealm.multirobot.packer;

import ink.sakurarealm.multirobot.util.MessagePackType;
import ink.sakurarealm.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import org.bukkit.entity.Player;
import org.json.JSONObject;

public class CMDPacker extends Packer implements ISendable {

    public CMDPacker(String sender, String world, String world_display, int max, Object[] players){
        super(getMsg(sender, world, world_display, max, players));
    }

    private static String getMsg(String sender, String world, String world_display, int max, Object[] players){
        JSONObject CMDMessage = new JSONObject();
        CMDMessage.put("version", PackVersion);
        CMDMessage.put("type", MessagePackType.CMD_List);
        CMDMessage.put("subtype", 1);
        CMDMessage.put("max", max);
        CMDMessage.put("count", players.length);
        String[] msg = new String[players.length];
        for (int i = 0; i < players.length; i++){
            msg[i] = MessageTools.Base64Encode(((Player)players[i]).getName());
        }
        CMDMessage.put("player_list", msg);
        CMDMessage.put("sender", sender);
        CMDMessage.put("world", world);
        CMDMessage.put("world_display", world_display);
        return CMDMessage.toString();
    }
}