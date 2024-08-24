package com.spark.smallProgram.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LYCHEE
 * 微信小程序获取token结果实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenModel implements Serializable {

    /**
     * 获取到的凭证
     */
    private String accessToken;

    /**
     * 凭证有效时间，单位：秒。目前是7200秒之内的值。
     */
    private Long expiresIn;
}
