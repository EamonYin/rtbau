package com.eamon.rtbau.rtbauUser.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class PushMsg {

    /**
     * appToken
     */
    private String appToken;
    /**
     * content
     */
    private String content;
    /**
     * summary
     */
    private String summary;
    /**
     * contentType
     */
    private Integer contentType;
    /**
     * topicIds
     */
    private List<Integer> topicIds;
    /**
     * uids
     */
    private List<String> uids;
    /**
     * url
     */
    private String url;
    /**
     * verifyPay
     */
    private Boolean verifyPay;
    /**
     * verifyPayType
     */
    private Integer verifyPayType;

    public String extra;
}
