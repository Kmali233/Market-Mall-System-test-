package com.kemali.mapper;

import com.kemali.pojo.Student;
import com.kemali.pojo.StudentQueryParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudentMapper {
    
    // 条件分页查询学员列表
    List<Student> page(StudentQueryParam studentQueryParam);
    
    // 查询条件匹配的总记录数
    Long count(StudentQueryParam studentQueryParam);
    
    // 添加学员信息
    void save(Student student);
    
    // 根据ID查询学员信息
    Student findById(Integer id);
    
    // 修改学员信息
    void update(Student student);
    
    // 批量删除学员信息
    void deleteByIds(Integer[] ids);
    
    // 统计每个班级的人数
    List<java.util.Map<String, Object>> countStudentByClazz();
    
    // 统计学员的学历信息
    List<java.util.Map<String, Object>> countStudentByDegree();
}