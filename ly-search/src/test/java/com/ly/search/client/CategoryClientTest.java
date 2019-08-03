package com.ly.search.client;

import com.leyou.item.pojo.Category;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {

    @Autowired
    CategoryClient categoryClient;

    @Test
    public void queryCategoryListByIds(){
        List<Category> categories = categoryClient.queryCategoryListByIds(Arrays.asList(1L, 2L));
        Assert.assertEquals(2, categories.size());
        categories.forEach(System.out::println);
    }
}