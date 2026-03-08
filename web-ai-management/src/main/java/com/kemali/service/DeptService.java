package com.kemali.service;

import com.kemali.pojo.Dept;

import java.util.List;

public interface DeptService {
    // 查询所有部门
    List<Dept> findAll();


    // 删除指定id部门
    void deleteById(Integer id);

    // 添加部门
    void save(Dept dept);

    // 指定id查询部门
    Dept getById(Integer id);


    // 修改部门数据
    void update(Dept dept);
}
