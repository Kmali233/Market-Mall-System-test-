package com.kemali.service;

import com.kemali.pojo.ClazzOption;
import com.kemali.pojo.JobOption;

import java.util.List;
import java.util.Map;

public interface ReportService {
    /**
     * 统计各个职位的员工人数
     * @return
     */
    JobOption getEmpJobData();

    List<Map> getEmpGenderData();
    
    /**
     * 统计每一个班级的人数
     * @return
     */
    ClazzOption getStudentCountData();
    
    /**
     * 统计学员的学历信息
     * @return
     */
    List<Map<String, Object>> getStudentDegreeData();
}