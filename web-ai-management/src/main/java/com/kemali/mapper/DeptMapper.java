package com.kemali.mapper;

import com.kemali.pojo.Dept;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeptMapper {

    // 查询所有部门
    @Select("select id, name, create_time, update_time from tlias.dept order by update_time desc")
    List<Dept> findAll();


    @Delete("delete from tlias.dept where id = #{id}")
    void deleteById(Integer id);


    @Insert("insert into tlias.dept(name, create_time, update_time) values(#{name},#{createTime},#{updateTime})")
    void insert(Dept dept);

    @Select("select id, name, create_time, update_time from tlias.dept where id = #{id}")
    Dept getById(Integer id);

    @Update("update tlias.dept set name = #{name},update_time = #{updateTime} where id = #{id}")
    void update(Dept dept);
}
