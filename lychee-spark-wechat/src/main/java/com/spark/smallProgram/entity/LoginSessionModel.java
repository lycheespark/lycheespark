package com.spark.smallProgram.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LYCHEE
 * 微信小程序登录结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginSessionModel implements Serializable {

    /**
     * 会话密钥
     */
    private String sessionKey;

    /**
     * 用户在开放平台的唯一标识符，若当前小程序已绑定到微信开放平台账号下会返回，详见 UnionID 机制说明。
     * <p>
     *     UnionID 机制说明:
     *     https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/union-id.html
     * </p>
     */
    private String unionid;

    /**
     * 错误信息
     */
    private String errmsg;

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 错误码
     */
    private String errcode;
}
