package com.hmall.api.client;

import com.hmall.api.config.DefaultFeignConfig;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.ItemPageQueryDTO;
import com.hmall.common.domain.PageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(value = "search-service", configuration = DefaultFeignConfig.class)
public interface SearchClient {

    @GetMapping("/search/list")
    PageDTO<ItemDTO> search(@SpringQueryMap ItemPageQueryDTO query);

    @PostMapping("/search/filters")
    Map<String, List<String>> searchFilters(@RequestBody ItemPageQueryDTO query);
}
