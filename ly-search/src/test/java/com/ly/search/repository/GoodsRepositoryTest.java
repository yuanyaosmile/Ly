package com.ly.search.repository;

import com.leyou.item.pojo.Spu;
import com.ly.common.vo.PageResult;
import com.ly.search.client.GoodsClient;
import com.ly.search.pojo.Goods;
import com.ly.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest
public class GoodsRepositoryTest {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    SearchService goodsService;

    @Test
    public void test() {
        template.createIndex(Goods.class);
        template.putMapping(Goods.class);
    }

    @Test
    public void loadTest() {
        int page = 1;
        int rows = 100;
        int size = 0;
        do {
            //查询spu信息
            PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
            List<Spu> spuList = result.getItems();
            //构建goods
            List<Goods> goodsList = spuList.stream().map(goodsService::buildGoods).collect(Collectors.toList());
            //存入索引库
            goodsRepository.saveAll(goodsList);
            page++;
            size = spuList.size();
        } while (size == 100);
    }
}