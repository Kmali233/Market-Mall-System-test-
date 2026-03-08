package com.kemali.service.impl;

import com.kemali.mapper.DeptMapper;
import com.kemali.mapper.EmpMapper;
import com.kemali.pojo.Dept;
import com.kemali.service.DeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptMapper deptMapper;
    
    @Autowired
    private EmpMapper empMapper;

    @Override
    public List<Dept> findAll() {
        return deptMapper.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        // 检查部门下是否有员工
        Integer count = empMapper.countByDeptId(id);
        if (count > 0) {
            // 如果有员工，抛出异常
            throw new RuntimeException("对不起，当前部门下有员工，不能直接删除！");
        }
        // 否则执行删除操作
        deptMapper.deleteById(id);
    }

    @Override
    public void save(Dept dept) {
        //补全基础属性
        dept.setCreateTime(LocalDateTime.now());
        dept.setUpdateTime(LocalDateTime.now());
        //保存部门
        deptMapper.insert(dept);
    }

    @Override
    public Dept getById(Integer id) {
        return deptMapper.getById(id);
    }

    @Override
    public void update(Dept dept) {
        dept.setUpdateTime(LocalDateTime.now());
        deptMapper.update(dept);
    }


}
