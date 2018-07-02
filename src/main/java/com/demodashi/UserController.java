package com.demodashi;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.demodashi.pay.util.PayToolUtils;
import com.demodashi.pay.util.PayConfigUtils;
import com.demodashi.pay.util.QRUtils;
import com.demodashi.pay.util.XMLUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * demo大师 测试例子
 *
 * @author xgchen
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Inject
    UserService userApplication;

    @ResponseBody
    @RequestMapping("/qrcode")
    public void qrcode(HttpServletRequest request, HttpServletResponse response,
                       ModelMap modelMap) {
        try {
            String userId = "user01";
            String productId = request.getParameter("productId");
            String text = userApplication.weixinPay(userId, productId);

            int width = 300;
            int height = 300;
            //二维码的图片格式
            String format = "gif";
            Hashtable hints = new Hashtable();

            //内容所使用编码
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix;
            try {
                bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
                QRUtils.writeToStream(bitMatrix, format, response.getOutputStream());
            } catch (WriterException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
        }
    }

    @ResponseBody
    @RequestMapping("/hadPay")
    public Map<String, Object> hadPay(UserVO user, HttpServletRequest request, HttpServletResponse response,
                                      ModelMap modelMap) {
        try {
            //简单的业务逻辑：在微信的回调接口里面，已经定义了，回调返回成功的话，那么 _PAY_RESULT 不为空
            if (request.getSession().getAttribute("_PAY_RESULT") != null) {
                return success("支付成功！");
            }
            return error("没成功");
        } catch (Exception e) {
            return error(e);
        }
    }

    /**
     * 微信平台发起的回调方法，
     * 调用我们这个系统的这个方法接口，将扫描支付的处理结果告知我们系统
     *
     * @throws JDOMException
     * @throws Exception
     */
    public void weiXinNotify(HttpServletRequest request, HttpServletResponse response) throws JDOMException, Exception {
        //读取参数  
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String s;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((s = in.readLine()) != null) {
            sb.append(s);
        }
        in.close();
        inputStream.close();

        //解析xml成map,过滤空值，设置 TreeMap
        Map<String, String> m = XMLUtils.doXMLParse(sb.toString());
        SortedMap<Object, Object> packageParams = new TreeMap<>();
        Iterator it = m.keySet().iterator();
        while (it.hasNext()) {
            String parameter = (String) it.next();
            String parameterValue = m.get(parameter);
            String v = "";
            if (null != parameterValue) {
                v = parameterValue.trim();
            }
            packageParams.put(parameter, v);
        }

        // 账号信息  
        String key = PayConfigUtils.API_KEY;
        //判断签名是否正确
        if (PayToolUtils.isTenPaySign("UTF-8", packageParams, key)) {
            String resXml;
            if ("SUCCESS".equals(packageParams.get("result_code"))) {
                String mch_id = (String) packageParams.get("mch_id");
                String openid = (String) packageParams.get("openid");
                String is_subscribe = (String) packageParams.get("is_subscribe");
                String out_trade_no = (String) packageParams.get("out_trade_no");
                String total_fee = (String) packageParams.get("total_fee");

                //暂时使用最简单的业务逻辑来处理：只是将业务处理结果保存到session中，后续应该将返回成功的状态保留下来
                request.getSession().setAttribute("_PAY_RESULT", "OK");
                System.out.println("支付成功");

                //通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.  
                resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                        + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
            } else {
                resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                        + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            }

            BufferedOutputStream out = new BufferedOutputStream(
                    response.getOutputStream());
            out.write(resXml.getBytes());
            out.flush();
            out.close();
        } else {
            System.out.println("通知签名验证失败");
        }
    }
}
