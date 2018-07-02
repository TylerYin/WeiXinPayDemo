package com.demodashi.pay.util;

/**
 * @author Tyler Yin
 */
public class PayConfigUtils {
    /**
     * 公众账号appid
     */
    public final static String APP_ID = "xxx";
    public final static String APP_SECRET = "xxx";

    /**
     * 商户号
     */
    public final static String MCH_ID = "xxx";

    /**
     * key设置路径：微信商户平台(pay.weixin.qq.com)-->账户设置-->API安全-->密钥设置
     */
    public final static String API_KEY = "xxx";

    /**
     * 有关url
     */
    public final static String UFDODER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

    /**
     * 微信支付回调接口，就是微信那边收到
     */
    public final static String NOTIFY_URL = "http://xxxxxxx";

    /**
     * 企业向个人账号付款的URL
     */
    public final static String SEND_EED_PACK_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";

    /**
     * 发起支付ip
     */
    public final static String CREATE_IP = "113.69.246.11";
}
