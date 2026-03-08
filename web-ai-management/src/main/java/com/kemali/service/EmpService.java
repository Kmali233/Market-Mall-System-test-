package com.kemali.service;

import com.kemali.pojo.Emp;
import com.kemali.pojo.EmpQueryParam;
import com.kemali.pojo.LoginInfo;
import com.kemali.pojo.PageResult;

import java.util.List;

public interface EmpService {
//    PageResult page(Integer page, Integer pageSize, String name, Integer gender, LocalDate begin, LocalDate end);

    PageResult<Emp> page(EmpQueryParam empQueryParam);

    void save(Emp emp);

    // 查询所有员工
    List<Emp> findAll();
    
    // 批量删除员工
    void deleteByIds(List<Integer> ids);
    
    // 根据ID查询员工信息（包括基本信息和工作经历）
    Emp findById(Integer id);
    
    // 修改员工信息
    void update(Emp emp);


    /**
     * 登录
     */
    LoginInfo login(Emp emp);
}