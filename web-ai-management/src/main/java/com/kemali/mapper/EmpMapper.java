package com.kemali.mapper;

import com.kemali.pojo.Emp;
import com.kemali.pojo.EmpQueryParam;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface EmpMapper {

    /*
    //查询总记录数

    @Select("select count(*)from tlias.emp e left join tlias.dept d on e.dept_id = d.id")
    public Long count();


    //查询所有的员工及其对应的部门名称

    @Select("select e.*, d.name as deptName from tlias.emp e left join tlias.dept d on e.dept_id = d.id")
    public List<Emp> list(Integer start,Integer pageSize);
    */

//    @Select("select e.*, d.name as deptName from tlias.emp e left join tlias.dept d on e.dept_id = d.id")

    public List<Emp> list(EmpQueryParam empQueryParam);

    // 新增员工数据
    @Options(useGeneratedKeys = true, keyProperty = "id")   // 主键返回
    @Insert("insert into emp(username, name, gender, phone, job, salary, image, entry_date, dept_id, create_time, update_time) " +
            "values (#{username},#{name},#{gender},#{phone},#{job},#{salary},#{image},#{entryDate},#{deptId},#{createTime},#{updateTime})")
    void insert(Emp emp);

    // 查询所有员工
    @Select("select * from tlias.emp")
    List<Emp> findAll();

    // 批量删除员工
    void deleteByIds(List<Integer> ids);
    
    // 根据ID查询员工完整信息
    Emp findById(Integer id);
    
    // 修改员工信息
    void update(Emp emp);

    /**
     * 统计各个职位的员工人数
     */
    @MapKey("pos")
    List<Map<String,Object>> countEmpJobData();

    /**
     * 统计员工性别信息
     */
    @MapKey("name")
    List<Map> countEmpGenderData();
    
    /**
     * 统计指定部门的员工数量
     */
    Integer countByDeptId(Integer deptId);


    /**
     * 根据用户名和密码查询员工信息
     */
    @Select("select * from tlias.emp where username = #{username} and password = #{password}")
    Emp getUsernameAndPassword(Emp emp);
}