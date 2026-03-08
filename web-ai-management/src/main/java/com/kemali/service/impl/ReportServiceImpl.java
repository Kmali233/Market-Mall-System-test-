package com.kemali.service.impl;

import com.kemali.mapper.EmpMapper;
import com.kemali.mapper.StudentMapper;
import com.kemali.pojo.ClazzOption;
import com.kemali.pojo.JobOption;
import com.kemali.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private EmpMapper empMapper;
    
    @Autowired
    private StudentMapper studentMapper;

    @Override
    public JobOption getEmpJobData() {
        List<Map<String,Object>> list = empMapper.countEmpJobData();
        List<Object> jobList = list.stream().map(dataMap -> dataMap.get("pos")).toList();
        List<Object> dataList = list.stream().map(dataMap -> dataMap.get("total")).toList();
        return new JobOption(jobList, dataList);
    }

    @Override
    public List<Map> getEmpGenderData() {
        return empMapper.countEmpGenderData();
    }
    
    @Override
    public ClazzOption getStudentCountData() {
        // 查询每个班级的人数统计数据
        List<Map<String, Object>> list = studentMapper.countStudentByClazz();
        
        // 提取班级列表和对应的数据列表
        List<String> clazzList = list.stream()
                .map(dataMap -> (String) dataMap.get("clazzName"))
                .toList();
        
        List<Integer> dataList = list.stream()
                .map(dataMap -> ((Long) dataMap.get("studentCount")).intValue())
                .toList();
        
        // 封装为ClazzOption对象返回
        return new ClazzOption(clazzList, dataList);
    }
    
    @Override
    public List<Map<String, Object>> getStudentDegreeData() {
        // 查询学员学历统计数据
        return studentMapper.countStudentByDegree();
    }
}