package com.kemali.mapper;

import com.kemali.pojo.Clazz;
import com.kemali.pojo.ClazzQueryParam;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ClazzMapper {

    @Delete("delete from tlias.clazz where id = #{id}")
    void ClazzDelete(Integer id);

    @Insert("insert into tlias.clazz(name, room, begin_date, end_date, master_id, subject, create_time, update_time) " +
            "values(#{name}, #{room}, #{beginDate}, #{endDate}, #{masterId}, #{subject}, #{createTime}, #{updateTime})")
    void ClassInsert(Clazz clazz);

    @Select("select c.*, e.name as masterName, (case when now() < c.begin_date then '未开班' when now() > c.end_date then '已结课' else '在读' end) as status from tlias.clazz c left join tlias.emp e on c.master_id = e.id")
    List<Clazz> getAllclass();
    
    // 条件分页查询班级列表
    List<Clazz> page(ClazzQueryParam clazzQueryParam);
    
    // 查询条件匹配的总记录数
    Long count(ClazzQueryParam clazzQueryParam);
    
    // 根据ID查询班级信息
    Clazz getClazzById(Integer id);
    
    // 修改班级信息
    void update(Clazz clazz);
}
