package com.kemali.service.impl;

import com.kemali.mapper.StudentMapper;
import com.kemali.pojo.PageResult;
import com.kemali.pojo.Student;
import com.kemali.pojo.StudentQueryParam;
import com.kemali.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class StudentServiceImpl implements StudentService {
    
    @Autowired
    private StudentMapper studentMapper;
    
    @Override
    public PageResult page(StudentQueryParam studentQueryParam) {
        // 计算偏移量
        int offset = (studentQueryParam.getPage() - 1) * studentQueryParam.getPageSize();
        
        // 设置偏移量和每页大小到查询参数中
        studentQueryParam.setOffset(offset);
        studentQueryParam.setPageSize(studentQueryParam.getPageSize());
        
        // 查询学员列表
        List<Student> studentList = studentMapper.page(studentQueryParam);
        
        // 查询总记录数
        Long total = studentMapper.count(studentQueryParam);
        
        // 封装分页结果
        return new PageResult<>(total, studentList);
    }
    
    @Override
    public void save(Student student) {
        // 设置默认值和创建时间
        student.setViolationCount((short) 0);
        student.setViolationScore((short) 0);
        student.setCreateTime(LocalDateTime.now());
        student.setUpdateTime(LocalDateTime.now());
        
        // 调用Mapper添加学员
        studentMapper.save(student);
    }
    
    @Override
    public Student findById(Integer id) {
        // 调用Mapper根据ID查询学员
        return studentMapper.findById(id);
    }
    
    @Override
    public void update(Student student) {
        // 设置更新时间
        student.setUpdateTime(LocalDateTime.now());
        
        // 调用Mapper修改学员信息
        studentMapper.update(student);
    }
    
    @Override
    public void deleteByIds(Integer[] ids) {
        // 调用Mapper批量删除学员信息
        studentMapper.deleteByIds(ids);
    }
    
    @Override
    public void violation(Integer id, Short score) {
        // 先查询学员当前信息
        Student student = studentMapper.findById(id);
        if (student != null) {
            // 增加违纪次数
            student.setViolationCount((short) (student.getViolationCount() + 1));
            // 增加违纪扣分
            student.setViolationScore((short) (student.getViolationScore() + score));
            // 更新修改时间
            student.setUpdateTime(LocalDateTime.now());
            // 调用Mapper更新学员信息
            studentMapper.update(student);
        }
    }
}