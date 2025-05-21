package com.eamon.rtbau.rtbauUser.entity.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class IP9Msg {
    // 状态码
    private Integer ret;
    private IP9Data data;
    // 查询时间-秒
    private String qt;
}
