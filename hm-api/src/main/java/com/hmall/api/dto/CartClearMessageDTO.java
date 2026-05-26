package com.hmall.api.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@ApiModel(description = "清理购物车MQ消息体")
@Data
@Accessors(chain = true)
public class CartClearMessageDTO {
    @ApiModelProperty("需要清理的商品id集合")
    private Set<Long> itemIds;
}
