package com.dong1990.blog.service;

import com.dong1990.blog.pojo.Blog;
import com.dong1990.blog.vo.BlogQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface BlogService {

    // 根据id查询博客
    Blog getBlog(Long id);

    // 专门用于前端markdown转html的方法
    Blog getAndConvert(Long id);

    // 查询一组数据，参数（分页，带标题和分类的查询）
    Page<Blog> listBlog(Pageable pageable, BlogQuery blog);

    // 查询出来一组数据，用于首页博客内容展示
    Page<Blog> listBlog(Pageable pageable);

    // 根据tag标签来查询一组分页的数据
    Page<Blog> listBlog(Long tagId,Pageable pageable);

    // 首页搜索查询
    Page<Blog> listBlog(String query,Pageable pageable);

    // 推荐的博客列表
    List<Blog> listRecommendBlogTop(Integer size);

    // 归档查询，最终按照年返回的map集合
    Map<String,List<Blog>> archiveBlog();

    Long countBlog();

    // 新增
    Blog saveBlog(Blog blog);

    // 修改
    Blog updateBlog(Long id,Blog blog);

    // 删除
    void deleteBlog(Long id);

}
