package com.ly.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.item.pojo.*;
import com.ly.common.exception.GlobalException;
import com.ly.common.utils.JsonUtils;
import com.ly.common.vo.CodeMsg;
import com.ly.common.vo.PageResult;
import com.ly.search.client.BrandClient;
import com.ly.search.client.CategoryClient;
import com.ly.search.client.GoodsClient;
import com.ly.search.client.SpecificationClient;
import com.ly.search.pojo.Goods;
import com.ly.search.pojo.SearchRequest;
import com.ly.search.pojo.SearchResult;
import com.ly.search.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {

    @Autowired
    CategoryClient categoryClient;

    @Autowired
    BrandClient brandClient;

    @Autowired
    GoodsClient goodsClient;

    @Autowired
    SpecificationClient specificationClient;

    @Autowired
    GoodsRepository repository;

    @Autowired
    ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu) {
        //查询分类
        List<Category> categories = categoryClient.queryCategoryListByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());
        //查询品牌
        Brand brand = brandClient.queryBrandById(spu.getBrandId());
        if (brand == null) {
            throw new GlobalException(CodeMsg.BRAND_NOT_EXIST);
        }
        //搜索字段
        String all = spu.getTitle() + StringUtils.join(names, " ") + brand.getName();

        //查询sku
        List<Sku> skuList = goodsClient.querySkuBySpuId(spu.getId());
        if (CollectionUtils.isEmpty(skuList)) {
            throw new GlobalException(CodeMsg.GOODS_NOT_FOUND);
        }

        //对Sku进行处理
        List<Map<String, Object>> skus = new ArrayList<>();
        Set<Long> priceSet = new HashSet<>();
        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.substringBefore(sku.getImage(), ","));
            skus.add(map);

            //处理价格
            priceSet.add(sku.getPrice());
        }

        //Set<Long> priceSet = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());

        //查询规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, spu.getCid3(), true);
        if (CollectionUtils.isEmpty(params)) {
            throw new GlobalException(CodeMsg.SPU_DETAIL_NOT_FOUND);
        }

        //查询商品详情
        SpuDetail spuDetail = goodsClient.querySpuDetailById(spu.getId());
        //获取通用规格参数
        Map<Long, String> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, String.class);
        //获取特有规格参数
        Map<String, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<String>>>() {
        });

        //规格参数，key是规格参数的名字，value是规格参数的值
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam param : params) {
            //规格名称
            String key = param.getName();
            Object value = "";
            //判断是否是通用规格
            if (param.getGeneric()) {
                value = genericSpec.get(param.getId());
            } else {
                value = specialSpec.get(param.getId());
            }
            specs.put(key, value);
        }


        //构建对象
        Goods goods = new Goods();
        goods.setBrandId(spu.getBrandId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setCreateTime(spu.getCreateTime());
        goods.setId(spu.getId());
        goods.setAll(all); //搜索字段，包含标题，分类，品牌，规格
        goods.setPrice(priceSet); // 所有sku价格的集合
        goods.setSkus(JsonUtils.toString(skus)); //TODO 所有sku的集合的json格式
        goods.setSpecs(null); //TODO 搜有的可搜索的规格参数
        goods.setSubTitle(spu.getSubTitle());
        return goods;
    }

    public PageResult<Goods> search(SearchRequest request) {
        int page = request.getPage() - 1;
        int size = request.getSize();

        String key = request.getKey();

        //创建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subtitle", "skus"}, null));
        //分页
        queryBuilder.withPageable(PageRequest.of(page, size));
        //过滤
        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key);
        queryBuilder.withQuery(basicQuery);

        //聚合分类和品牌
        //聚合分类
        String categoryAggName = "category_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //聚合品牌
        String brandAggName = "brand_agg";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //查询
        //Page<Goods> result = repository.search(queryBuilder.build());
        Page<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        //分页结果
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> goodsList = result.getContent();
        //解析聚合结果
        Aggregations aggs = ((AggregatedPage<Goods>) result).getAggregations();
        List<Category> categories = parseCategoryAgg(aggs.get(categoryAggName));
        List<Brand> brands = parseBrandAgg(aggs.get(brandAggName));

        //规格参数聚合
        List<Map<String, Object>> specs = null;
        if (categories != null && categories.size() == 1) {
            //商品分类存在并且数量为·，可以聚合规格参数
            specs = buildSpecificationAgg(categories.get(0).getId(), basicQuery);
        }
        return new SearchResult(total, totalPages, goodsList, categories, brands, specs);
    }

    private List<Map<String, Object>> buildSpecificationAgg(Long cid, QueryBuilder basicQuery) {
        List<Map<String, Object>> specs = new ArrayList<>();
        //1、查询需要聚合的规格参数
        List<SpecParam> params = specificationClient.queryParamList(null, cid, true);
        //2、聚合
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //2.1 带上聚合条件
        queryBuilder.withQuery(basicQuery);
        //2、2 聚合
        for (SpecParam param : params) {
            String name = param.getName();
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." + name + ".keyword"));
        }
        //3、获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //4、解析结果
        Aggregations aggs = result.getAggregations();
        for (SpecParam param : params) {
            //规格参数名
            String name = param.getName();
            StringTerms terms = aggs.get(name);

            //准备map
            Map<String, Object> map = new HashMap<>(16);
            map.put("k", name);
            map.put("options", terms.getBuckets().stream().map(b -> b.getKeyAsString()).collect(Collectors.toList()));

            specs.add(map);
        }
        return specs;
    }

    private List<Brand> parseBrandAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Brand> brands = brandClient.queryBrandByIds(ids);
            return brands;
        } catch (Exception e) {
            return null;
        }


    }

    private List<Category> parseCategoryAgg(LongTerms terms) {
        try {
            List<Long> ids = terms.getBuckets().stream().map(b -> b.getKeyAsNumber().longValue()).collect(Collectors.toList());
            List<Category> categories = categoryClient.queryCategoryListByIds(ids);
            return categories;
        } catch (Exception e) {
            return null;
        }
    }
}
