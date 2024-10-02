package com.collect.backend.service.adpter;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.collect.backend.domain.entity.Website;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class WebsiteAdpter{

    public static final int TIMEOUT = 1000;

    //判断url是否可访问
    public  boolean testUrlWithTimeOut(String urlString){
        String result = HttpRequest.get(urlString)
                .timeout(TIMEOUT)//超时，毫秒
                .execute().body();
        if(result.contains("html"))  //返回的是文档，说明不是图片
            return  false;
        return true;
    }
    //域名+logo路径拼接
    public String splicingUrl(String host,String  path){
        StringBuilder stringBuilder = new StringBuilder();
        if(HttpUtil.isHttp(path)){
            return stringBuilder.append("http://").append(host).
                    append(path.contains("/") ? "" : "/").append(path).toString();
        }else{
            return stringBuilder.append("https://").append(host).
                    append(path.contains("/") ? "" : "/").append(path).toString();
        }
    }

    public String getLog(Element element, String url,String host){
        String hrefUrl = element.attr("href");
        if(StringUtils.isBlank(hrefUrl)){
            return null;
        }
        //拿到的是一个路径,进行拼接
        if(!HttpUtil.isHttp(hrefUrl) && !HttpUtil.isHttps(hrefUrl)){
            hrefUrl = splicingUrl(host, hrefUrl);
        }
        //得到的logo链接可以访问
        if(testUrlWithTimeOut(hrefUrl)){
            return hrefUrl;
        }
        return null;
    }
    public String handleElements(Elements documents,String url,String host){
        for(Element element: documents){
            String resltUrl = getLog(element, url,host);
            if(StringUtils.isNotBlank(resltUrl)){
                return resltUrl;
            }
        }
        return null;
    }
    //解析url，获取其中的icon和title
    public Map<String,String> parseUrlGetIconTitle(Website website){
        String url = website.getUrl();
        Map<String,String> reslt  = new HashMap<>();
        if(!HttpUtil.isHttp(url) && !HttpUtil.isHttps(url)){
            reslt.put("message","非url链接");
            return reslt;
        }
        String faviconUrl = null;
        String title = website.getTitle();
        String host = null;
        try {
            host = URLUtil.getHost(new URL(url)).getHost();
        } catch (MalformedURLException e) {
            log.error("url 获取域名失败",e);
            reslt.put("message","url 获取域名失败");
            return reslt;
        }
        try {
            //拿到logo
            Document document = Jsoup.connect(url).get();
            Elements documents = null;
            if(StringUtils.isBlank(faviconUrl)){
                documents = document.select("link[rel$=icon]");
                //处理html文档
                faviconUrl = handleElements(documents,url,host);
            }
            //用户没有填title，则为自动获取
            if(StringUtils.isBlank(title)){
                title = document.select("title").first().text();
            }
            //前面找不到标签，有的网站可以通过 域名 + favicon.ico 获取到
            String baseFaviconUrl = splicingUrl(host, "favicon.ico");
            if(StringUtils.isBlank(faviconUrl) && testUrlWithTimeOut(baseFaviconUrl)){
                faviconUrl = baseFaviconUrl;
            }
            //域名 + favicon.ico 也获取不到，通过获取网站的第一张照片，可能是图标
            if(StringUtils.isBlank(faviconUrl)){
                Element img = document.select("img").first();
                if(Objects.nonNull(img)){
                    String src = splicingUrl(host,img.attr("src"));
                    if(testUrlWithTimeOut(src)){
                        faviconUrl = src;
                    }
                }
            }
            reslt.put("icon",faviconUrl);
            reslt.put("title",title);
            return reslt;
        } catch (IOException e) {
            log.error("url 链接无效 ",e);
            reslt.put("message","url 链接无效");
            return reslt;
        }
    }

}
