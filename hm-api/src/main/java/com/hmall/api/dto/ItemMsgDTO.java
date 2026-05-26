package com.hmall.api.dto;

import lombok.Data;

@Data
public class ItemMsgDTO {
    private Long id;
    private String name;
    private Integer price;
    private String image;
    private String category;
    private String brand;
    private Integer sold;
    private Integer commentCount;
    private Boolean isAD;
    private Integer status;
}
