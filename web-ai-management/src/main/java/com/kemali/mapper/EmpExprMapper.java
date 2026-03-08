package com.kemali.mapper;

import com.kemali.pojo.EmpExpr;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmpExprMapper {

    void insertBatch(List<EmpExpr> exprList);
    
    // 根据员工ID批量删除工作经历
    void deleteByEmpIds(List<Integer> empIds);
    
    // 根据员工ID查询工作经历
    List<EmpExpr> findByEmpId(Integer empId);
}
