package com.dong1990.blog.dao;

import com.dong1990.blog.pojo.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    // List<Comment> findByBlogId(Long blogId, Sort sort);

    // 根据blogId来查询评论列表信息，更新评论创建时间倒序排序
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);

}
