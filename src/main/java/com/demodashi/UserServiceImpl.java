package com.demodashi;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Named;

import com.demodashi.pay.util.HttpUtils;
import com.demodashi.pay.util.PayConfigUtils;
import com.demodashi.pay.util.PayToolUtils;
import com.demodashi.pay.util.XMLUtils;

/**
 * @author Tyler Yin
 */
@Named("userService")
public class UserServiceImpl implements UserService {

    @Override
    public String weixinPay(String userId, String productId) throws Exception {

        /**
         * 订单号
         */
        String out_trade_no = "" + System.currentTimeMillis();

        /**
         * 账号信息
         * appid
         */
        String appid = PayConfigUtils.APP_ID;
        //String appsecret = PayConfigUtils.APP_SECRET;

        /**
         * 商业号
         */
        String mch_id = PayConfigUtils.MCH_ID;

        /**
         * key
         */
        String key = PayConfigUtils.API_KEY;

        String currTime = PayToolUtils.getCurrTime();
        String strTime = currTime.substring(8, currTime.length());
        String strRandom = PayToolUtils.buildRandom(4) + "";
        String nonce_str = strTime + strRandom;

        // 获取发起电脑 ip
        String spbill_create_ip = PayConfigUtils.CREATE_IP;

        // 回调接口   
        String notify_url = PayConfigUtils.NOTIFY_URL;
        String trade_type = "NATIVE";

        SortedMap<Object, Object> packageParams = new TreeMap<>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("body", "测试商品");
        packageParams.put("out_trade_no", out_trade_no);

        /**
         * 价格的单位为分
         */
        packageParams.put("total_fee", "10");
        packageParams.put("spbill_create_ip", spbill_create_ip);
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", trade_type);

        String sign = PayToolUtils.createSign("UTF-8", packageParams, key);
        packageParams.put("sign", sign);
        String requestXML = PayToolUtils.getRequestXml(packageParams);
        String resXml = HttpUtils.postData(PayConfigUtils.UFDODER_URL, requestXML);
        Map map = XMLUtils.doXMLParse(resXml);
        return (String) map.get("code_url");
    }
}
