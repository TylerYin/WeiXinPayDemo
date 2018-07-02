<%@ page language="java" pageEncoding="UTF-8"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" charset="utf-8" src="resource/js/jquery-2.min.js"></script>
<script type="text/javascript" charset="utf-8" src="resource/js/layer/layer.js"></script>
<title>微信扫码支付</title>
</head>
<body>
    <form id="pay_form" method="post" >
        <h1>测试商品：0.1元 <input id="pay_submit" name="but" type="button" value="微信支付"/></h1>
    </form>
</body>
<script>
    $(function(){
        $("#pay_submit").click(function(){
            buy('001');
        });
    });

    function buy(productId){
        layer.open({
            area: ['300px', '300px'],
            type: 2,
            closeBtn: false,
            title: false,
            shift: 2,
            shadeClose: true,
            content:'./user/qrcode?productId=' + productId
        });

        //轮询是否已经付费
        var t1 = window.setInterval("getPayState('" + productId + "')",1500);
    }

    function getPayState(productId){
        var url = './user/hadPay?productId=' + productId;
        $.ajax({
            type:'post',
            url:url,
            data:{productId:productId},
            cache:false,
            async:true,
            success:function(json){
                if(json.result == 0){
                    location.href = 'result.jsp';
                }
            },
            error:function(){
                layer.msg("执行错误！", 8);
            }
        });
    }
</script> 
</html>