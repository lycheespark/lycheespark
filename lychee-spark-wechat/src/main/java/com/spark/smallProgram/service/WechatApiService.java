package com.spark.smallProgram.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.spark.exception.LycheeSparkException;
import com.spark.smallProgram.entity.AccessTokenModel;
import com.spark.smallProgram.entity.LoginSessionModel;
import com.spark.smallProgram.enums.WechatApiEnum;
import com.spark.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author LYCHEE
 * 微信小程序API接口
 */
@Slf4j
public class WechatApiService {

    /**
     * 获取微信小程序AccessToken
     * @param appId 小程序 appId
     * @param secret 小程序 appSecret
     * @return token结果
     */
    public static AccessTokenModel getAccessToken(String appId, String secret) {
        Assert.hasText(appId,"appId不能为空");
        Assert.hasText(secret,"secret不能为空");
        String url = WechatApiEnum.API_PREFIX.getUrl()
                .concat(WechatApiEnum.GET_ACCESS_TOKEN.getUrl()) + "?grant_type=client_credential&appid=" + appId + "&secret=" + secret;
        String res = HttpUtils.get(url);
        if (StringUtils.isEmpty(res)) {
            throw new LycheeSparkException("获取微信小程序AccessToken失败");
        }
        JSONObject result = JSONObject.parseObject(res);
        if (StringUtils.isNotEmpty(result.getString("access_token"))) {
            String accessToken = result.getString("access_token");
            Long expiresIn = result.getLong("expires_in");
            return new AccessTokenModel(accessToken, expiresIn);
        } else {
            // 获取异常信息
            Long errCode = result.getLong("errcode");
            String errMsg = result.getString("errmsg");
            throw new LycheeSparkException("获取微信小程序AccessToken失败,异常code："+ errCode +" 异常描述：" + errMsg);
        }
    }

    /**
     * 根据code获取小程序openID和UnionID
     * @param appId 小程序 appId
     * @param secret 小程序 appSecret
     * @param code 登录时获取的 code，可通过wx.login获取
     * @return 结果
     */
    public LoginSessionModel login(String appId, String secret, String code) {
        Assert.hasText(appId,"appId不能为空");
        Assert.hasText(secret,"secret不能为空");
        Assert.hasText(code,"code不能为空");
        // 参数封装
        Map<String, String> params = new HashMap<>();
        params.put("appid", appId);
        params.put("secret", secret);
        params.put("js_code", code);
        // 固定值
        params.put("grant_type", "authorization_code");
        String res = HttpUtils.get(WechatApiEnum.API_PREFIX.getUrl()
                .concat(WechatApiEnum.LOGIN.getUrl()), params);
        if (StringUtils.isEmpty(res)) {
            throw new LycheeSparkException("小程序登录失败");
        }
        JSONObject result = JSONObject.parseObject(res);
        String errcode = result.getString("errcode");
        if (StringUtils.isEmpty(errcode) || "0".equals(errcode)) {
            // 正常返回
            String sessionKey = result.getString("session_key");
            String unionid = result.getString("unionid");
            String errmsg = result.getString("errmsg");
            String openid = result.getString("openid");
            return new LoginSessionModel(sessionKey, unionid, errmsg, openid, errcode);
        } else if ("40029".equals(errcode)) {
            throw new LycheeSparkException("js_code无效");
        } else if ("45011".equals(errcode)) {
            throw new LycheeSparkException("API 调用太频繁，请稍候再试");
        } else if ("40226".equals(errcode)) {
            throw new LycheeSparkException("高风险等级用户，小程序登录拦截 。风险等级详见用户安全解方案");
        } else if ("-1".equals(errcode)) {
            throw new LycheeSparkException("系统繁忙，此时请开发者稍候再试");
        } else {
            throw new LycheeSparkException("未知错误");
        }
    }

    /**
     * 获取手机号
     * @param appId 小程序 appId
     * @param secret 小程序 appSecret
     * @param code 登录时获取的 code，可通过wx.login获取
     * @return 结果
     */
    public static String getUserPhoneNumber(String appId, String secret, String code) {
        Assert.hasText(appId,"appId不能为空");
        Assert.hasText(secret,"secret不能为空");
        Assert.hasText(code,"code不能为空");
        AccessTokenModel accessToken = getAccessToken(appId, secret);
        String url = WechatApiEnum.API_PREFIX.getUrl()
                .concat(WechatApiEnum.GET_USER_PHONE.getUrl())+ "?access_token="+accessToken.getAccessToken();
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        String res = HttpUtils.post(url, JSON.toJSONString(params));
        if (StringUtils.isEmpty(res)) {
            throw new LycheeSparkException("获取手机号失败");
        }
        JSONObject result = JSONObject.parseObject(res);
        String errcode = result.getString("errcode");
        if (StringUtils.isEmpty(errcode) || "0".equals(errcode)) {
            return result.getJSONObject("phone_info").getString("phoneNumber");
        } else if ("40029".equals(errcode)) {
            throw new LycheeSparkException("js_code无效");
        } else if ("45011".equals(errcode)) {
            throw new LycheeSparkException("API 调用太频繁，请稍候再试");
        } else if ("40013".equals(errcode)) {
            throw new LycheeSparkException("请求appid身份与获取code的小程序appid不匹配");
        } else if ("-1".equals(errcode)) {
            throw new LycheeSparkException("系统繁忙，此时请开发者稍候再试");
        } else {
            throw new LycheeSparkException("未知错误");
        }
    }

    /**
     * 获取小程序码，适用于需要的码数量较少的业务场景
     * @param params 请求参数【包含必须三个参数 appId secret path】
     *        path 扫码进入的小程序页面路径
     * @return 结果
     */
    public static BufferedImage getQrCode(Map<String, Object> params) throws IOException {
        // 包含三个参数 appId secret path
        if (params.containsKey("appId") && params.containsKey("secret") && params.containsKey("path")){
            AccessTokenModel accessToken = getAccessToken(params.get("appId").toString(), params.get("secret").toString());
            String url = WechatApiEnum.API_PREFIX.getUrl()
                    .concat(WechatApiEnum.GET_QR_CODE.getUrl())+ "?access_token="+accessToken.getAccessToken();
            // 清除 appId secret
            params.remove("appId");
            params.remove("secret");
            return HttpUtils.postImg(url, JSON.toJSONString(params));
        }
        throw new LycheeSparkException("缺少必要参数");
    }

    /**
     * 获取小程序码，适用于需要的码数量较少的业务场景
     * @param appId 小程序 appId
     * @param secret 小程序 appSecret
     * @param path 扫码进入的小程序页面路径
     * @return 结果
     */
    public static BufferedImage getQrCode(String appId, String secret, String path) throws IOException {
        Assert.hasText(appId,"appId不能为空");
        Assert.hasText(secret,"secret不能为空");
        Assert.hasText(path,"扫码进入的小程序页面路径不能为空");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appId", appId);
        params.put("secret", secret);
        params.put("path", path);
        return getQrCode(params);
    }

    /**
     * 获取不限制的小程序码，适用于需要的码数量极多的业务场景
     * @param params 请求参数【包含必须三个参数 appId secret scene 】
     *        scene: scene 字段的值会作为 query 参数传递给小程序/小游戏。用户扫描该码进入小程序/小游戏后，开发者可以获取到二维码中的 scene 值，再做处理逻辑。
     *               调试阶段可以使用开发工具的条件编译自定义参数 scene=xxxx 进行模拟，开发工具模拟时的 scene 的参数值需要进行 encodeURIComponent
     * @return 结果
     */
    public static BufferedImage getUnlimitedQrCode(Map<String, Object> params) throws IOException {
        // 包含三个参数 appId secret scene
        if (params.containsKey("appId") && params.containsKey("secret") && params.containsKey("scene")){
            AccessTokenModel accessToken = getAccessToken(params.get("appId").toString(), params.get("secret").toString());
            String url = WechatApiEnum.API_PREFIX.getUrl()
                    .concat(WechatApiEnum.GET_QR_CODE.getUrl())+ "?access_token="+accessToken.getAccessToken();
            // 清除 appId secret
            params.remove("appId");
            params.remove("secret");
            return HttpUtils.postImg(url, JSON.toJSONString(params));
        }
        throw new LycheeSparkException("缺少必要参数");
    }

    /**
     * 获取不限制的小程序码，适用于需要的码数量极多的业务场景
     * @param appId 小程序 appId
     * @param secret 小程序 appSecret
     * @param scene 扫码参数
     * @return 结果
     */
    public static BufferedImage getUnlimitedQrCode(String appId, String secret, String scene) throws IOException {
        Assert.hasText(appId,"appId不能为空");
        Assert.hasText(secret,"secret不能为空");
        Assert.hasText(scene,"参数字段scene不能为空");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appId", appId);
        params.put("secret", secret);
        params.put("path", scene);
        return getUnlimitedQrCode(params);
    }



}
