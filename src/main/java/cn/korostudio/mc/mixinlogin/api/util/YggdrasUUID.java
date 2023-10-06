package cn.korostudio.mc.mixinlogin.api.util;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class YggdrasUUID {
    public static String generateOfflineUUID(String characterName) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + characterName).getBytes(StandardCharsets.UTF_8)).toString();
    }

    public static String generateNormalUUID() {
        long time = System.currentTimeMillis();
        long random = (long) (Math.random() * Integer.MAX_VALUE);
        random = random * (long) (Math.random() * Integer.MAX_VALUE);
        time = time << 16;
        String uuid = new UUID(random, time).toString();
        return UUID.fromString(uuid.substring(0, uuid.length() - 4) + "ffff").toString();
    }
}
