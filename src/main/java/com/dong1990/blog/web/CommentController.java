package com.dong1990.blog.web;

import com.dong1990.blog.pojo.Comment;
import com.dong1990.blog.pojo.User;
import com.dong1990.blog.service.BlogService;
import com.dong1990.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    // 评论者的默认静态图像
    @Value("${comment.avatar}")
    private String avatar;

    @RequestMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        // blog页面下的片段
        return "blog :: commentList";
    }

    // 点击发布提交评论信息
    @RequestMapping(value="/comments",method = RequestMethod.POST)
    public String post(Comment comment, HttpSession session) {
        Long blogId = comment.getBlog().getId();
        comment.setBlog(blogService.getBlog(blogId));
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // 不等于空的话就是管理员
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        } else {
            // 普通访客，默认头像
            comment.setAvatar(avatar);
        }
        commentService.saveComment(comment);
        return "redirect:/comments/" + blogId;
    }
}
