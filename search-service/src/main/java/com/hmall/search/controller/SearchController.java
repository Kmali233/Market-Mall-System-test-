package com.hmall.search.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.common.domain.PageDTO;
import com.hmall.search.domain.dto.ItemDTO;
import com.hmall.search.domain.dto.ItemDoc;
import com.hmall.search.domain.query.ItemPageQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "搜索相关接口")
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private static final String INDEX_NAME = "items";
    private static final String CATEGORY_AGG_NAME = "categoryAgg";
    private static final String BRAND_AGG_NAME = "brandAgg";
    private static final int FILTER_AGG_SIZE = 1000;

    private final RestHighLevelClient client;

    @ApiOperation("搜索商品")
    @GetMapping("/list")
    public PageDTO<ItemDTO> search(ItemPageQuery query) throws IOException {
        BoolQueryBuilder basicQuery = buildBasicQuery(query);

        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                basicQuery,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true),
                                ScoreFunctionBuilders.weightFactorFunction(1000f)
                        )
                }
        ).boostMode(CombineFunction.SUM);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(functionScoreQuery)
                .from(query.from())
                .size(query.getPageSize());

        boolean defaultSort = StrUtil.isBlank(query.getSortBy());
        String sortBy = defaultSort ? "updateTime" : query.getSortBy();
        SortOrder sortOrder = !defaultSort && Boolean.TRUE.equals(query.getIsAsc()) ? SortOrder.ASC : SortOrder.DESC;
        sourceBuilder.sort(convertSortField(sortBy), sortOrder);

        SearchResponse response = executeSearch(sourceBuilder);
        return handleResponse(response, query.getPageSize());
    }

    @ApiOperation("聚合搜索过滤项")
    @PostMapping("/filters")
    public Map<String, List<String>> searchFilters(@RequestBody ItemPageQuery query) throws IOException {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder()
                .query(buildBasicQuery(query))
                .size(0)
                .aggregation(AggregationBuilders.terms(CATEGORY_AGG_NAME)
                        .field("category")
                        .size(FILTER_AGG_SIZE))
                .aggregation(AggregationBuilders.terms(BRAND_AGG_NAME)
                        .field("brand")
                        .size(FILTER_AGG_SIZE));

        SearchResponse response = executeSearch(sourceBuilder);
        Map<String, List<String>> filters = new LinkedHashMap<>(2);
        filters.put("category", parseAggValues(response, CATEGORY_AGG_NAME));
        filters.put("brand", parseAggValues(response, BRAND_AGG_NAME));
        return filters;
    }

    private SearchResponse executeSearch(SearchSourceBuilder sourceBuilder) throws IOException {
        SearchRequest request = new SearchRequest(INDEX_NAME);
        request.source(sourceBuilder);
        return client.search(request, RequestOptions.DEFAULT);
    }

    private BoolQueryBuilder buildBasicQuery(ItemPageQuery query) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StrUtil.isBlank(query.getKey())) {
            boolQuery.must(QueryBuilders.matchAllQuery());
        } else {
            boolQuery.must(QueryBuilders.matchQuery("name", query.getKey()));
        }
        if (StrUtil.isNotBlank(query.getBrand())) {
            boolQuery.filter(QueryBuilders.termQuery("brand", query.getBrand()));
        }
        if (StrUtil.isNotBlank(query.getCategory())) {
            boolQuery.filter(QueryBuilders.termQuery("category", query.getCategory()));
        }
        if (query.getMinPrice() != null || query.getMaxPrice() != null) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
            if (query.getMinPrice() != null) {
                rangeQuery.gte(query.getMinPrice());
            }
            if (query.getMaxPrice() != null) {
                rangeQuery.lte(query.getMaxPrice());
            }
            boolQuery.filter(rangeQuery);
        }
        return boolQuery;
    }

    private String convertSortField(String sortBy) {
        if ("update_time".equals(sortBy)) {
            return "updateTime";
        }
        return sortBy;
    }

    private List<String> parseAggValues(SearchResponse response, String aggName) {
        Aggregations aggregations = response.getAggregations();
        if (aggregations == null) {
            return Collections.emptyList();
        }
        Terms terms = aggregations.get(aggName);
        if (terms == null) {
            return Collections.emptyList();
        }
        List<String> values = new ArrayList<>(terms.getBuckets().size());
        for (Terms.Bucket bucket : terms.getBuckets()) {
            values.add(bucket.getKeyAsString());
        }
        return values;
    }

    private PageDTO<ItemDTO> handleResponse(SearchResponse response, int pageSize) {
        SearchHit[] hits = response.getHits().getHits();
        long total = response.getHits().getTotalHits().value;
        long pages = total == 0 ? 0 : (total + pageSize - 1) / pageSize;
        List<ItemDTO> list = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            ItemDoc itemDoc = JSONUtil.toBean(hit.getSourceAsString(), ItemDoc.class);
            list.add(BeanUtil.copyProperties(itemDoc, ItemDTO.class));
        }
        return new PageDTO<>(total, pages, list);
    }
}
