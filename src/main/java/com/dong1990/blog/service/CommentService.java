package com.dong1990.blog.service;

import com.dong1990.blog.pojo.Comment;

import java.util.List;

public interface CommentService {

    // 获取博客列表下的评论列表信息
    List<Comment> listCommentByBlogId(Long blogId);

    // 保存评论信息
    Comment saveComment(Comment comment);
}
