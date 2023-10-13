package cn.korostudio.mc.mixinlogin.data.bean;

import com.nimbusds.jose.shaded.gson.Gson;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class YggError {
    String error;
    String errorMessage;
    String cause;


    public static YggError InvalidToken(String cause){
        return builder().cause(cause).errorMessage("Invalid token.").error("ForbiddenOperationException").build();
    }
    public static YggError InvalidToken(){
        return InvalidToken("");
    }
    public static YggError InvalidCredentials(String cause){
        return builder().cause(cause).errorMessage("Invalid credentials. Invalid username or password.").error("ForbiddenOperationException").build();
    }
    public static YggError InvalidCredentials(){
        return InvalidCredentials("");
    }
    public static YggError IllegalArgument(String cause){
        return builder().cause(cause).errorMessage("Access token already has a profile assigned.").error("IllegalArgumentException").build();
    }
    public static YggError IllegalArgument(){
        return IllegalArgument("");
    }

    public String toJsonStr(){
        return new Gson().toJson(this);
    }

}
