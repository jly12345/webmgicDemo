package com.jly.maginc.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Component
@Slf4j
public class HttpUtils {
    private PoolingHttpClientConnectionManager manager;

    @Value("${image.path}")
    private String imagePath;

    public HttpUtils() {
        this.manager = new PoolingHttpClientConnectionManager();
        //设置最大连接数
        this.manager.setMaxTotal(100);
        //设置每个主机的最大连接数
        this.manager.setDefaultMaxPerRoute(10);
    }

    private RequestConfig getConfig() {
        RequestConfig config = RequestConfig.custom()
                //创建连接的最长时间
                .setConnectTimeout(10000)
                //获取连接的最长时间
                .setConnectionRequestTimeout(5000)
                //数据传输的最长时间
                .setSocketTimeout(30*1000).build();
        return config;
    }

    /**
     * 下载页面数据
     * @param url
     * @return
     */
    public String doGetHtml(String url){
        if(StringUtils.isBlank(url)){
            return "";
        }
        log.info("html url:{}",url);
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(manager).build();
        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(this.getConfig());
//        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
//        httpGet.setHeader("Accept-Encoding", "gzip, deflate");
//        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
//        httpGet.setHeader("Connection", "keep-alive");
//        httpGet.setHeader("Cookie", "__jdu=1129058077; areaId=22; ipLoc-djd=22-1960-38574-0; PCSYCityID=CN_510000_510700_510703; shshshfpa=2e993a46-c721-3c13-7052-f106c5f5cbb7-1581345475; shshshfpb=b91KyTULvpnWcUMXoLV9ulg%3D%3D; xtest=8589.cf6b6759; unpl=V2_ZzNtbUFQFBVxDk9cexpfVmIDEVtLUUcdIQ8RUC8ZD1dvBxNcclRCFnQUR1dnGVwUZwcZWURcQBdFCEdkeBBVAWMDE1VGZxBFLV0CFSNGF1wjU00zQwBBQHcJFF0uSgwDYgcaDhFTQEJ2XBVQL0oMDDdRFAhyZ0AVRQhHZHsZXw1mBxtcRFFzJXI4dmR%2fEVUCZgIiXHJWc1chVE9TeBBVBCoDEl5KVkccdA5AZHopXw%3d%3d; qrsc=3; __jdv=76161171|baidu-pinzhuan|t_288551095_baidupinzhuan|cpc|0f3d30c8dba7459bb52f2eb5eba8ac7d_0_26f15789122b41278759e6f5e1bc9500|1581413307346; __jdc=122270672; rkv=V0800; 3AB9D23F7A4B3C9B=W6HY46Z4MEC2VPZJNTEPGS5SEXZVIGUIZC3ZSQWISH3YP3OTGTXI5ZUEURNRHNPEXPRAHDJM2EMK4NCWGJ4MIP5B3A; __jda=122270672.1129058077.1576825571.1581413307.1581415416.4; shshshfp=f07b0062dea63d38285e7536e731d4c0; __jdb=122270672.2.1129058077|4.1581415416; shshshsID=f6280d0c3d823f362c1ba637df294230_2_1581415453600");
//        httpGet.setHeader("Host", "search.jd.com");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36");

        try(CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                HttpEntity entity = response.getEntity();
                if(entity !=null){
                    String content = EntityUtils.toString(entity,"utf8");
                    return content;
                }
            }
        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }



    /**
     * 下载图片
     * @param url
     * @return
     */
    public String doGetImage(String url){
        log.info("image:{}",url);
        if("https:".equals(StringUtils.trim(url))){
            return "";
        }
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(manager).build();

        HttpGet httpGet = new HttpGet(url);

        //设置请求信息
        httpGet.setConfig(this.getConfig());
        try(CloseableHttpResponse response = httpClient.execute(httpGet)) {
            if(response.getStatusLine().getStatusCode() ==200){
                if(response.getEntity()!=null){
                    //获取图片后缀
                    String extName = url.substring(url.lastIndexOf("."));
                    //创建图片名，重命名图片
                    String picName = UUID.randomUUID().toString() + extName;
                    //下载图片
                    try(OutputStream outputStream = new FileOutputStream(new File(imagePath + picName))) {
                        response.getEntity().writeTo(outputStream);
                        return picName;
                    }


                }
            }
        } catch(ClientProtocolException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
