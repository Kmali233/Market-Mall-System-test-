package com.hmall.api.client.fallback;

import com.hmall.api.client.AddressClient;
import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.AddressDTO;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import com.hmall.common.exception.BizIllegalException;
import com.hmall.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

@Slf4j
public class AddressClientFallback implements FallbackFactory<AddressClient> {
    @Override
    public AddressClient create(Throwable cause) {
        return new AddressClient() {



        };
    }
}