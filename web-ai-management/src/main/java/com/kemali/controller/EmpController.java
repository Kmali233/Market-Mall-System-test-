package com.kemali.controller;

import com.kemali.pojo.*;
import com.kemali.service.EmpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/emps")
public class EmpController {

    @Autowired
    private EmpService empService;

    /*
    分页查询
     */
    @GetMapping
    public Result page(EmpQueryParam empQueryParam) {
        log.info("查询请求参数： {}", empQueryParam);
        PageResult pageResult = empService.page(empQueryParam);
        return Result.success(pageResult);
    }

    @PostMapping
    public Result save(@RequestBody Emp emp) {
        log.info("请求参数emp： {}", emp);
        empService.save(emp);
        return Result.success();
    }

    @GetMapping("/list")
    public Result list() {
        log.info("查询所有员工信息");
        List<Emp> empList = empService.findAll();
        return Result.success(empList);
    }

    /*
    批量删除员工
     */
    @DeleteMapping
    public Result delete(@RequestParam List<Integer> ids) {
        log.info("批量删除员工，ids： {}", ids);
        empService.deleteByIds(ids);
        return Result.success();
    }

    /*
    根据ID查询员工信息
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id) {
        log.info("根据ID查询员工信息，id： {}", id);
        Emp emp = empService.findById(id);
        return Result.success(emp);
    }
    
    /*
    修改员工信息
     */
    @PutMapping
    public Result update(@RequestBody Emp emp) {
        log.info("修改员工信息，emp： {}", emp);
        empService.update(emp);
        return Result.success();
    }



}
