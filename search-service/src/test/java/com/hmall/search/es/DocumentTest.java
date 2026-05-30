package com.hmall.search.es;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.common.utils.CollUtils;
import com.hmall.search.domain.dto.ItemDoc;
import com.hmall.search.domain.po.Item;
import com.hmall.search.service.IItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(properties = "spring.profiles.active=local")
@Slf4j
public class DocumentTest {

    private RestHighLevelClient client;
    @Autowired
    private IItemService itemService;

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://xxx")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    @Test
    void testAddDocument() throws IOException {
        Item item = itemService.getById(100002644680L);
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
        String doc = JSONUtil.toJsonStr(itemDoc);
        IndexRequest request = new IndexRequest("items").id(itemDoc.getId());
        request.source(doc, XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocumentById() throws IOException {
        GetRequest request = new GetRequest("items").id("100002644680");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        ItemDoc itemDoc = JSONUtil.toBean(json, ItemDoc.class);
        System.out.println("itemDoc= " + itemDoc);
    }

    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("item", "100002644680");
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("items", "100002644680");
        request.doc("price", 58800, "commentCount", 1);
        client.update(request, RequestOptions.DEFAULT);
    }

    @Test
    void testLoadItemDocs() throws IOException {
        int pageNo = 1;
        int size = 1000;
        while (true) {
            Page<Item> page = itemService.lambdaQuery().eq(Item::getStatus, 1)
                    .page(new Page<Item>(pageNo, size));
            List<Item> items = page.getRecords();
            if (CollUtils.isEmpty(items)) {
                return;
            }
            log.info("加载第{}页数据，共{}条", pageNo, items.size());
            BulkRequest request = new BulkRequest("items");
            for (Item item : items) {
                ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);
                request.add(new IndexRequest()
                        .id(itemDoc.getId())
                        .source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON));
            }
            client.bulk(request, RequestOptions.DEFAULT);
            pageNo++;
        }
    }
}
