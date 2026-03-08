package com.kemali.service;

import com.kemali.pojo.Clazz;
import com.kemali.pojo.ClazzQueryParam;
import com.kemali.pojo.PageResult;

import java.util.List;

public interface ClazzService {

    void Clazzdelete(Integer id);

    void ClazzInsert(Clazz clazz);

    // 查询所有班级
    List<Clazz> getAllclass();
    
    // 条件分页查询班级列表
    PageResult page(ClazzQueryParam clazzQueryParam);
    
    // 根据ID查询班级
    Clazz getClazzById(Integer id);
    
    // 修改班级信息
    void update(Clazz clazz);
}
