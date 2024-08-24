package com.spark.autoNavi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LYCHEE
 * 高德请求接口API
 */
@Getter
@AllArgsConstructor
public enum AutoNaviEnum {

    /**
     * 高德地图请求路径
     */
    API_PREFIX("https://restapi.amap.com/v3","高德地图请求路径前缀"),

    /**
     * 地理编码 API 服务地址
     * <p>
     *     https://lbs.amap.com/api/webservice/guide/api/georegeo
     * </p>
     */
    GEO("/geocode/geo","地理编码");

    /**
     * URL
     */
    private final String url;

    /**
     * 描述
     */
    private final String description;
}
