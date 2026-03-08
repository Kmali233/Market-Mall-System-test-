package com.kemali;

import com.kemali.mapper.EmpMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebAiManagementApplicationTests {

    @Autowired
    private EmpMapper empMapper;

//    @Test
//    public void testList(){
//        List<Emp> empList = empMapper.list();
//        empList.forEach(System.out::println);
//    }

}
