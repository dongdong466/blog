package com.dong1990.blog.service;

import com.dong1990.blog.pojo.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TypeService {

    // 新增分类
    Type saveType(Type type);

    // 根据id获取分类
    Type getType(Long id);

    // 获取分类名称
    Type getTypeByName(String name);

    // 分页查询，返回page
    Page<Type> listType(Pageable pageable);

    // 博客列表当中的分类下拉菜单渲染方法，博客控制器处调用此方法
    List<Type> listType();

    // 传递大小来定义数据的数据列表的数量获取
    List<Type> listTypeTop(Integer size);

    // 修改分类，根据id查询到对象，再修改对象信息保存到数据库
    Type updateType(Long id,Type type);

    // 删除分类
    void deleteType(Long id);
}
