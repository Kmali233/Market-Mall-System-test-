package com.kemali.service.impl;

import com.kemali.mapper.ClazzMapper;
import com.kemali.pojo.Clazz;
import com.kemali.pojo.ClazzQueryParam;
import com.kemali.pojo.PageResult;
import com.kemali.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ClazzServiceImpl implements ClazzService {
    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public void Clazzdelete(Integer id) {
        clazzMapper.ClazzDelete(id);
    }

    @Override
    public void ClazzInsert(Clazz clazz) {
        log.info("开始导入数据...");
        clazzMapper.ClassInsert(clazz);
    }

    @Override
    public List<Clazz> getAllclass() {
        return clazzMapper.getAllclass();
    }

    @Override
    public PageResult page(ClazzQueryParam clazzQueryParam) {
        // 计算偏移量
        int offset = (clazzQueryParam.getPage() - 1) * clazzQueryParam.getPageSize();
        
        // 设置偏移量和每页大小到查询参数中
        clazzQueryParam.setOffset(offset);
        clazzQueryParam.setPageSize(clazzQueryParam.getPageSize());
        
        // 查询班级列表
        List<Clazz> clazzList = clazzMapper.page(clazzQueryParam);
        
        // 查询总记录数
        Long total = clazzMapper.count(clazzQueryParam);
        
        // 封装分页结果
        return new PageResult<>(total, clazzList);
    }

    @Override
    public Clazz getClazzById(Integer id) {
        return clazzMapper.getClazzById(id);
    }

    @Override
    public void update(Clazz clazz) {
        clazzMapper.update(clazz);
    }
}
