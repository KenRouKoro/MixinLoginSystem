package cn.korostudio.mc.mixinlogin.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.korostudio.mc.mixinlogin.data.entity.Profile;

public class GetYggdrasilInfo {
    static public JSONObject getProfileInfo(Profile profile){
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("id", profile.getUuid());
        jsonObject.append("name", profile.getName());
        jsonObject.append("properties",getProfilePropertiesInfo(profile))

        return jsonObject;
    }
    static public JSONArray getProfilePropertiesInfo(Profile profile){
        JSONArray jsonArray = new JSONArray();
        JSONObject textures = new JSONObject();
        textures.append("name", "textures");
        String textures_str = Base64.encode(getProfileTexturesInfo(profile).toString());
        textures.append("value", textures_str);
        //! 签名还没加
        jsonArray.add(textures);
        return jsonArray;
    }
    static public JSONObject getProfileTexturesInfo(Profile profile){
        JSONObject jsonObject = new JSONObject();
        jsonObject.append("timestamp", System.currentTimeMillis());
        jsonObject.append("profileId", profile.getUuid());
        jsonObject.append("profileName", profile.getName());
        JSONObject textures = new JSONObject();

        //? 处理皮肤
        JSONObject skin = new JSONObject();
        skin.append("url", profile.getSkin_url());
        JSONObject skin_metadata = new JSONObject();
        if(profile.getModel_type().equals("slim")){
            skin_metadata.append("model","slim");
        }else {
            skin_metadata.append("model","default");
            //? 对于Steve和其他第三方扩展模型 在此注册为Steve
        }
        skin.append("metadata",skin_metadata);

        //? 处理披风
        if(! profile.getCape_url().isEmpty()){
            JSONObject cape = new JSONObject();
            cape.append("url",profile.getCape_url());
            //? Cape没有元数据

            textures.append("CAPE",cape);
        }
        textures.append("SKIN",skin);

    return textures;
    }
}
