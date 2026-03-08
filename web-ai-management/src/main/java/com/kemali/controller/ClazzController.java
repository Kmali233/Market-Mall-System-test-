package com.kemali.controller;

import com.kemali.anno.LogOperation;
import com.kemali.pojo.Clazz;
import com.kemali.pojo.ClazzQueryParam;
import com.kemali.pojo.Result;
import com.kemali.service.ClazzService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class ClazzController {

    @Autowired
    private ClazzService clazzService;

    @DeleteMapping("/clazzs/{id}")
    public Result delete(@PathVariable Integer id){
        clazzService.Clazzdelete(id);
        return Result.success();
    }

    @PostMapping("/clazzs")
    public Result insert(@RequestBody Clazz clazz){
        log.info("开始添加数据...");
        clazzService.ClazzInsert(clazz);
        return Result.success();
    }

    @GetMapping("/clazzs/list")
    public Result getAllclass(){
        List<Clazz> Clazzlist = clazzService.getAllclass();
        return Result.success(Clazzlist);
    }

    /**
     * 班级列表条件分页查询接口
     * @param clazzQueryParam 查询参数
     * @return 分页查询结果
     */
    @LogOperation
    @GetMapping("/clazzs")
    public Result page(ClazzQueryParam clazzQueryParam) {
        log.info("班级列表条件分页查询，参数：{}", clazzQueryParam);
        return Result.success(clazzService.page(clazzQueryParam));
    }
    
    /**
     * 根据ID查询班级信息
     * @param id 班级ID
     * @return 班级信息
     */
    @GetMapping("/clazzs/{id}")
    public Result getClazzById(@PathVariable Integer id) {
        log.info("根据ID查询班级信息，ID：{}", id);
        Clazz clazz = clazzService.getClazzById(id);
        return Result.success(clazz);
    }
    
    /**
     * 修改班级信息
     * @param clazz 班级信息
     * @return 修改结果
     */
    @PutMapping("/clazzs")
    public Result update(@RequestBody Clazz clazz) {
        log.info("修改班级信息，班级ID：{}", clazz.getId());
        clazzService.update(clazz);
        return Result.success();
    }
}
