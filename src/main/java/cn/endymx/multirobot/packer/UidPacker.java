package cn.endymx.multirobot.packer;

import cn.endymx.multirobot.util.MessagePackType;
import cn.endymx.multirobot.util.MessageTools;
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
        uidMessage.put("isSakuraMain", MessageTools.Base64Encode(config.getString("isSakuraMainWorld")));
        return uidMessage.toString();
    }
}
