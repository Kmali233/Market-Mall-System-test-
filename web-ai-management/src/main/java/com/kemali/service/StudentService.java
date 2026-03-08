package com.kemali.service;

import com.kemali.pojo.PageResult;
import com.kemali.pojo.Student;
import com.kemali.pojo.StudentQueryParam;

public interface StudentService {
    
    // 条件分页查询学员列表
    PageResult page(StudentQueryParam studentQueryParam);
    
    // 添加学员信息
    void save(Student student);
    
    // 根据ID查询学员信息
    Student findById(Integer id);
    
    // 修改学员信息
    void update(Student student);
    
    // 批量删除学员信息
    void deleteByIds(Integer[] ids);
    
    // 违纪处理
    void violation(Integer id, Short score);
}