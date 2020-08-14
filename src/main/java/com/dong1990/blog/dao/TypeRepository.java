package com.dong1990.blog.dao;

import com.dong1990.blog.pojo.Type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeRepository extends JpaRepository<Type,Long> {

    Type findByName(String name);


    /*
    * 一个分类中有很多博客，按照博客数量的大小对分类进行获取最多的，type中有blogs属性
    * 根据分页对象里的一些参数来查找需要的条数
    * @Query 自定义查询 按分页的方式去查询
    * */
    @Query("select t from Type t")
    List<Type> findTop(Pageable pageable);
}
