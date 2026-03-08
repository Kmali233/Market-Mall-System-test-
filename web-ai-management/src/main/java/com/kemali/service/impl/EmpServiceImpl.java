package com.kemali.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.kemali.mapper.EmpExprMapper;
import com.kemali.mapper.EmpMapper;
import com.kemali.pojo.*;
import com.kemali.service.EmpService;
import com.kemali.service.EmpLogService;
import com.kemali.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmpServiceImpl implements EmpService {

    @Autowired
    private EmpMapper empMapper;
    @Autowired
    private EmpExprMapper empExprMapper;
    @Autowired
    private EmpLogService empLogService;

    @Override
    public PageResult<Emp> page(EmpQueryParam empQueryParam) {
        //1. 设置分页参数
        PageHelper.startPage(empQueryParam.getPage(), empQueryParam.getPageSize());

        //2. 执行查询
        List<Emp> empList = empMapper.list(empQueryParam);
        Page<Emp> p = (Page<Emp>) empList;

        //3. 封装结果
        return new PageResult<>(p.getTotal(), p.getResult());
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void save(Emp emp) {
        try {
            //1.补全基础属性
            emp.setCreateTime(LocalDateTime.now());
            emp.setUpdateTime(LocalDateTime.now());

            //2.保存员工基本信息
            empMapper.insert(emp);

            //3. 保存员工的工作经历信息 - 批量
            Integer empId = emp.getId();
            List<EmpExpr> exprList = emp.getExprList();
            if (!CollectionUtils.isEmpty(exprList)) {
                exprList.forEach(empExpr -> empExpr.setEmpId(empId));
                empExprMapper.insertBatch(exprList);
            }
        } finally {
            //记录操作日志
            EmpLog empLog = new EmpLog(null, LocalDateTime.now(), emp.toString());
            empLogService.insertLog(empLog);
        }
    }

    @Override
    public List<Emp> findAll() {
        List<Emp> empList = empMapper.findAll();
        // 过滤掉password字段，提高安全性
        empList.forEach(emp -> emp.setPassword(null));
        return empList;
    }

    @Override
    @Transactional
    public void deleteByIds(List<Integer> ids) {
        // 首先删除员工的工作经历信息
        empExprMapper.deleteByEmpIds(ids);
        
        // 然后删除员工基本信息
        empMapper.deleteByIds(ids);
    }

    @Override
    public Emp findById(Integer id) {
        // 通过一次SQL查询获取员工完整信息，包括工作经历
        return empMapper.findById(id);
    }

    @Transactional(rollbackFor = {Exception.class})
    @Override
    public void update(Emp emp) {
        try {
            // 1. 更新员工基本信息
            emp.setUpdateTime(LocalDateTime.now());
            empMapper.update(emp);
            
            // 2. 删除该员工的所有工作经历
            Integer empId = emp.getId();
            empExprMapper.deleteByEmpIds(List.of(empId));
            
            // 3. 插入新的工作经历（如果有）
            List<EmpExpr> exprList = emp.getExprList();
            if (!CollectionUtils.isEmpty(exprList)) {
                exprList.forEach(empExpr -> empExpr.setEmpId(empId));
                empExprMapper.insertBatch(exprList);
            }
        } finally {
            // 记录操作日志
            EmpLog empLog = new EmpLog(null, LocalDateTime.now(), emp.toString());
            empLogService.insertLog(empLog);
        }
    }


    @Override
    public LoginInfo login(Emp emp) {
        Emp empLogin = empMapper.getUsernameAndPassword(emp);
        if(empLogin != null){
            //1. 生成JWT令牌
            Map<String,Object> dataMap = new HashMap<>();
            dataMap.put("id", empLogin.getId());
            dataMap.put("username", empLogin.getUsername());

            String jwt = JwtUtils.generateJwt(dataMap);
            LoginInfo loginInfo = new LoginInfo(empLogin.getId(), empLogin.getUsername(), empLogin.getName(), jwt);
            return loginInfo;
        }
        return null;
    }

}
