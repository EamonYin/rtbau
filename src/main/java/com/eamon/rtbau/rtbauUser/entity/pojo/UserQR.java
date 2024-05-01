package com.eamon.rtbau.rtbauUser.entity.pojo;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class UserQR {
    public Integer code;
    public String msg;
    public String success;
    public UserQRInfo data;
}
