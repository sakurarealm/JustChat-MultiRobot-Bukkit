package ink.sakurarealm.multirobot.packer;

import ink.sakurarealm.multirobot.util.MessageTools;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class Packer {
    private final static byte[] MessageHeader = {0x11,0x45,0x14};
    public static int PackVersion = 4;
    private String MSG;

    Packer(String MSG){
        this.MSG = MSG;
    }

    public byte[] parse() {
        byte[] body = MSG.getBytes(Charset.forName(MessageTools.getEncode()));
        ByteBuffer bb = ByteBuffer.allocate(MessageHeader.length + 4 + body.length);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(MessageHeader);
        bb.putInt(body.length);
        bb.put(body);
        return bb.array();
    }

    public static boolean isMessage(byte[] a){
        return (a[0] == MessageHeader[0]) && (a[1] == MessageHeader[1]) && (a[2] == MessageHeader[2]);
    }
}
