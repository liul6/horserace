package com.horserace.mapper;

import com.horserace.domain.Users;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsersMapperTest {
    @Autowired
    UsersMapper usersMapper;

    @Test
    public void testInserUser() {
        Users users = new Users();
        users.setId(1112);
        users.setName("Joey Liu");
        users.setSalary(20000L);

        usersMapper.insert(users);

        List<Users> usersList = usersMapper.findAll();
        assert usersList.size() == 2;
    }
}
