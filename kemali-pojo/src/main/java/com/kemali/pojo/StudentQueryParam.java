package com.kemali.pojo;

import lombok.Data;

@Data
public class StudentQueryParam {
    private String name; // 姓名
    private Integer degree; // 学历
    private Integer clazzId; // 班级ID
    private Integer page; // 当前页码
    private Integer pageSize; // 每页条数
    private Integer offset; // 偏移量（用于SQL查询）
}