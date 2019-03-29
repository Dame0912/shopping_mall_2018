package com.dame.gmall.common;

/**
 * @param
 * @return
 */
public class WebConst {
    /**
     * cookie中存放token的有效时间
     */
    public static final int COOKIE_MAXAGE=7*24*3600;
    /**
     * 身份认证地址
     */
    public static  final  String VERIFY_ADDRESS="http://passport.gmall.com/verify";
    /**
     * 用户登录地址
     */
    public static  final  String LOGIN_ADDRESS="http://passport.gmall.com/index";

}
