package com.kemali.controller;


import com.kemali.pojo.Dept;
import com.kemali.pojo.Result;
import com.kemali.service.DeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// 部门管理控制器
@Slf4j
@RestController
public class DeptController {

    @Autowired
    private DeptService deptService;

    /**
     * 查询部门列表
     */
//    @RequestMapping(value = "/depts",method = RequestMethod.GET)
    @GetMapping("/depts")
    public Result list(){
        List<Dept> deptList = deptService.findAll();
        return Result.success(deptList);
    }

    @DeleteMapping("/depts")
    public Result delete(Integer id){
//        System.out.println("根据id删除部门, id=" + id);
        log.info("根据id删除部门, id={}",id);
        try {
            deptService.deleteById(id);
            return Result.success();
        } catch (RuntimeException e) {
            // 捕获异常，返回错误信息
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/depts")
    public Result insert(@RequestBody Dept dept){
//        System.out.println("新增部门, dept=" + dept);
        log.info("新增部门, dept={}",dept);
        deptService.save(dept);
        return Result.success();
    }

    @GetMapping("/depts/{id}")
    public Result getById(@PathVariable Integer id){
//        System.out.println("新增部门的id=" + id);
        log.info("新增部门的id={}",id);
        Dept dept = deptService.getById(id);
        return Result.success(dept);
    }

    @PutMapping("/depts")
    public Result update(@RequestBody Dept dept){
//        System.out.println("修改部门, dept=" + dept);
        log.info("修改部门, dept={}",dept);
        deptService.update(dept);
        return Result.success();
    }
}
