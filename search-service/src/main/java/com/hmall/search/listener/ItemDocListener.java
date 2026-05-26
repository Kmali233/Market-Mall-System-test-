package com.hmall.search.listener;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.hmall.api.dto.ItemMsgDTO;
import com.hmall.search.domain.dto.ItemDoc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemDocListener {

    private static final String INDEX_NAME = "items";

    private final RestHighLevelClient client;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "item.insert.queue", durable = "true"),
            exchange = @Exchange(name = "item.direct"),
            key = "item.insert"
    ))
    public void listenItemInsert(ItemMsgDTO msg) {
        saveOrDeleteByStatus(msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "item.update.queue", durable = "true"),
            exchange = @Exchange(name = "item.direct"),
            key = "item.update"
    ))
    public void listenItemUpdate(ItemMsgDTO msg) {
        saveOrDeleteByStatus(msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "item.delete.queue", durable = "true"),
            exchange = @Exchange(name = "item.direct"),
            key = "item.delete"
    ))
    public void listenItemDelete(Long id) {
        deleteItemDoc(id);
    }

    private void saveOrDeleteByStatus(ItemMsgDTO msg) {
        if (msg.getStatus() == null || msg.getStatus() == 1) {
            saveItemDoc(msg);
            return;
        }
        deleteItemDoc(msg.getId());
    }

    private void saveItemDoc(ItemMsgDTO msg) {
        ItemDoc itemDoc = BeanUtil.copyProperties(msg, ItemDoc.class);
        IndexRequest request = new IndexRequest(INDEX_NAME)
                .id(itemDoc.getId())
                .source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);
        try {
            client.index(request, RequestOptions.DEFAULT);
            log.debug("ES item document saved, id: {}", itemDoc.getId());
        } catch (IOException e) {
            log.error("Failed to save ES item document, id: {}", itemDoc.getId(), e);
            throw new RuntimeException(e);
        }
    }

    private void deleteItemDoc(Long id) {
        DeleteRequest request = new DeleteRequest(INDEX_NAME, id.toString());
        try {
            client.delete(request, RequestOptions.DEFAULT);
            log.debug("ES item document deleted, id: {}", id);
        } catch (IOException e) {
            log.error("Failed to delete ES item document, id: {}", id, e);
            throw new RuntimeException(e);
        }
    }
}
