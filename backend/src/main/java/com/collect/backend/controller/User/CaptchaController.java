package com.collect.backend.controller.User;

import com.alibaba.fastjson.JSONObject;
import com.collect.backend.common.Constants;
import com.collect.backend.utils.uuid.IdUtils;
import com.collect.backend.utils.redis.RedisUtils;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

@RestController
public class CaptchaController {
    @Resource(name = "captchaProducer")
    private Producer catchaCharProducer;
    @Resource(name = "captchaProducerMath")
    private Producer catchaMathProducer;
    @Value("${carbon.captchaType}")
    private String captchaType;

    @GetMapping("/captcha")
    public JSONObject getCode(){
        JSONObject reslt = new JSONObject();
        String uuid =  IdUtils.simpleUUID();//生成uuid，登陆时根据前端的uuid来判断是否安全
        String codeKey = Constants.codeKey +uuid;

        String capStr,code = null;
        BufferedImage bufferedImag = null;
        //生成验证码
        if("math".equals(captchaType)){
            String capText = catchaMathProducer.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@")); //得到表达式
            code = capText.substring(capText.lastIndexOf("@") + 1);  //得到结果
            bufferedImag = catchaMathProducer.createImage(capStr); //根据表达式生成结果
        }else if("char".equals(captchaType)){
            code = catchaCharProducer.createText();   //字符直接拿到结果即可
            bufferedImag = catchaMathProducer.createImage(code);
        }
        //存入redis中，登陆时判断验证码是否过期
        RedisUtils.set(codeKey,code,Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImag,"jpg",os);
        } catch (IOException e) {
            reslt.put("error_message","操作失败");
            return reslt;
        }
        reslt.put("error_message","successfully");
        reslt.put("uuid",uuid);
        reslt.put("img",Base64.getEncoder().encodeToString(os.toByteArray()));
        return  reslt;
    }


}
