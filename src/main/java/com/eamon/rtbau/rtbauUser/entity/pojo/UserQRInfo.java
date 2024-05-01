package com.eamon.rtbau.rtbauUser.entity.pojo;

import lombok.Data;

@Data
public class UserQRInfo {
   public Long expires;
   public String code;
   public String shortUrl;
   public String extra;
   public String url;
}
