package com.eamon.rtbau.rtbauUser.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class QRCallBackInfo {
    /**
     * appId
     */
    private Integer appId;
    /**
     * appKey
     */
    private String appKey;
    /**
     * appName
     */
    private String appName;
    /**
     * source
     */
    private String source;
    /**
     * userName
     */
    private String userName;
    /**
     * userHeadImg
     */
    private String userHeadImg;
    /**
     * time
     */
    private Long time;
    /**
     * uid
     */
    private String uid;
    /**
     * extra
     */
    private String extra;
}
