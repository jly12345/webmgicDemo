package com.jly.maginc.task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jly.maginc.po.JdItem;
import com.jly.maginc.service.ItemService;
import com.jly.maginc.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ItemTask {
    @Autowired
    private HttpUtils httpUtils;
    
    @Autowired
    private ItemService itemService;

    private static final ObjectMapper mapper = new ObjectMapper();

    @Scheduled(fixedDelay = 100 * 1000)
    public void itemTask(){
        String url = "https://search.jd.com/Search?keyword=%E6%89%8B%E6%9C%BA&enc=utf-8&qrst=1&rt=1&stop=1&vt=2&wq=%E6%89%8B%E6%9C%BA&s=214&click=0&page=";
        for(int i = 1; i < 6; i+=2) {
            log.info("当前第{}页",(i+1)/2);
            String urlpage = url + i;
            log.info(urlpage);
            String html = httpUtils.doGetHtml(urlpage);
            //解析页面,获取商品数据并存储
            this.parse(html);
        }
        log.info("手机数据抓取完成！");

    }

    /**
     * 解析页面,获取商品数据并存储
     * @param html
     */
    private void parse(String html) {
        //解析html
        Document doc = Jsoup.parse(html);
        //获取spu
        Elements spuList = doc.select("div#J_goodsList > ul > li") ;
        for(Element spuEle :spuList  ) {
            //获取spu
            long spu = Long.parseLong(spuEle.attr("data-spu"));
            //获取sku
            Elements skuList = spuEle.select("li.ps-item");
            for(Element skuEle :skuList  ) {
                long sku = Long.parseLong(skuEle.select("[data-sku]").first().attr("data-sku"));
                //根据sku查询商品数据
                JdItem item = new JdItem();
                item.setSku(sku);
                List<JdItem> list = itemService.findAll(item);
                //如果商品已经存在
                if(list.size()>0){
                    continue;
                }

                item.setSpu(spu);
                String itemUrl ="https://item.jd.com/"+sku+".html";
                item.setUrl(itemUrl);
                String picUrl = "https:" + skuEle.select("img[data-sku]").first().attr("data-lazy-img");
                picUrl =picUrl.replace("/n9/","/n1/");
                String picName = httpUtils.doGetImage(picUrl);
                item.setPic(picName);

                String itemPage = this.httpUtils.doGetHtml(itemUrl);
                String title = Jsoup.parse(itemPage).select("div.sku-name").first().text();

                item.setTitle(title);
                String priceJson = this.httpUtils.doGetHtml("https://p.3.cn/prices/mgets?skuIds=" + sku);
                double price = 0;
                try {
                    price = mapper.readTree(priceJson).get(0).get("p").asDouble();
                } catch(JsonProcessingException e) {
                    e.printStackTrace();
                }
                item.setPrice(price);
                item.setCreated(new Date());
                item.setUpdated(item.getCreated());
                log.info(item.toString());
                itemService.save(item);
            }
        }
    }
}
