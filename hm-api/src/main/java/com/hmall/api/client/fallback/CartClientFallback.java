package com.hmall.api.client.fallback;

import com.hmall.api.client.CartClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;

@Slf4j
public class CartClientFallback implements FallbackFactory<CartClient> {
    @Override
    public CartClient create(Throwable cause) {
        return new CartClient() {
            @Override
            public void removeByItemIds(@RequestParam("ids") Collection<Long> ids) {
                log.error("远程调用CartClient#qremoveByItemIds方法出现异常，参数：{}", ids, cause);
            }

        };
    }
}