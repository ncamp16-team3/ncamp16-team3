package com.izikgram.user.repository;

import com.izikgram.user.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Insert("insert into iz_member" +
            "(name, member_id, password, phone_num, birth_date, email, gender, payday, start_time, end_time, lunch_time, loc_mod, ind_cd, edu_lv)" +
            "values (" +
            "#{name}, #{member_id}, #{password}, #{phone_num}, #{birth_date}, #{email}, #{gender}," +
            "#{payday}, #{start_time}, #{end_time}, #{lunch_time}, #{loc_mod}, #{ind_cd}, #{edu_lv})")
    void insertUser(User user);

    @Select("select * from iz_member where member_id=#{member_id} and password=#{password}")
    User login(@Param("member_id") String member_id, @Param("password") String password);

    @Select("select * from iz_member where member_id=#{member_id}")
    User findUserById(@Param("member_id") String member_id);

    @Select("SELECT NOW()")
    String getCurrentTime();

    @Select("select * from iz_member where member_id=#{member_id}")
    User getUserInfo(String member_id);

    @Delete("delete from iz_member where member_id=#{member_id}")
    void deleteUser(String member_id);

    @Update("UPDATE iz_member SET password = #{password} WHERE member_id = #{member_id}")
    int updateUserPw(@Param("password") String password,@Param("member_id") String member_id);

}
