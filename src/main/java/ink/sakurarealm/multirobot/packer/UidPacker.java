package ink.sakurarealm.multirobot.packer;

import ink.sakurarealm.multirobot.util.MessagePackType;
import ink.sakurarealm.multirobot.util.MessageTools;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONObject;

public class UidPacker extends Packer implements ISendable {

    public UidPacker(String id, String name, FileConfiguration config){
        super(getMsg(id, name, config));
    }

    private static String getMsg(String id, String name, FileConfiguration config){
        JSONObject uidMessage = new JSONObject();
        uidMessage.put("version", PackVersion);
        uidMessage.put("type", MessagePackType.UID);
        uidMessage.put("identity", 0);
        uidMessage.put("id", id);
        uidMessage.put("name", MessageTools.Base64Encode(name));
        uidMessage.put("SID", config.getInt("sakuraSID"));
        return uidMessage.toString();
    }
}
