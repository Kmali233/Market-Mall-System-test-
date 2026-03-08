package com.kemali.controller;

import com.kemali.pojo.PageResult;
import com.kemali.pojo.Result;
import com.kemali.pojo.Student;
import com.kemali.pojo.StudentQueryParam;
import com.kemali.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/students")
public class StudentController {
    
    @Autowired
    private StudentService studentService;
    
    /**
     * 学员列表条件分页查询接口
     * @param studentQueryParam 查询参数
     * @return 分页查询结果
     */
    @GetMapping
    public Result page(StudentQueryParam studentQueryParam) {
        log.info("学员列表条件分页查询，参数：{}", studentQueryParam);
        PageResult pageResult = studentService.page(studentQueryParam);
        return Result.success(pageResult);
    }
    
    /**
     * 添加学员信息接口
     */
    @PostMapping
    public Result save(@RequestBody Student student) {
        log.info("添加学员信息，学员：{}", student);
        studentService.save(student);
        return Result.success();
    }
    
    /**
     * 根据ID查询学员信息接口
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        log.info("根据ID查询学员信息，ID：{}", id);
        Student student = studentService.findById(id);
        return Result.success(student);
    }
    
    /**
     * 修改学员信息接口
     */
    @PutMapping
    public Result update(@RequestBody Student student) {
        log.info("修改学员信息，学员：{}", student);
        studentService.update(student);
        return Result.success();
    }
    
    /**
     * 批量删除学员信息接口
     */
    @DeleteMapping("/{ids}")
    public Result deleteByIds(@PathVariable String ids) {
        log.info("批量删除学员信息，IDs：{}", ids);
        // 将逗号分隔的字符串转换为Integer数组
        String[] idStrs = ids.split(",");
        Integer[] idArray = new Integer[idStrs.length];
        for (int i = 0; i < idStrs.length; i++) {
            idArray[i] = Integer.parseInt(idStrs[i]);
        }
        // 调用Service层批量删除
        studentService.deleteByIds(idArray);
        return Result.success();
    }
    
    /**
     * 违纪处理接口
     */
    @PutMapping("/violation/{id}/{score}")
    public Result violation(@PathVariable Integer id, @PathVariable Short score) {
        log.info("违纪处理，学员ID：{}，扣除分数：{}", id, score);
        // 调用Service层进行违纪处理
        studentService.violation(id, score);
        return Result.success();
    }
}