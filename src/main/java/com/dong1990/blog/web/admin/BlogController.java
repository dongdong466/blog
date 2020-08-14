package com.dong1990.blog.web.admin;

import com.dong1990.blog.pojo.Blog;
import com.dong1990.blog.pojo.Tag;
import com.dong1990.blog.pojo.User;
import com.dong1990.blog.service.BlogService;
import com.dong1990.blog.service.TagService;
import com.dong1990.blog.service.TypeService;
import com.dong1990.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.regex.Pattern;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private TagService tagService;
    /*
    * 添加登录拦截器，凡是访问admin路径的都要接受拦截
    * */
    @RequestMapping("/blogs")
    public String blogs(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return LIST;
    }

    /*
    * 搜索的时候只需要渲染博客列表的局部区域，查询的时候才使用该方法
    * 返回一个admin/blogs页面下的 blogList片段
    * */
    @RequestMapping(value = "/blogs/search",method = RequestMethod.POST)
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";
    }

    // 初始化一个blog。防止页面报空指针
    @RequestMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }
    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }

    // 博客列表编辑进入博客编辑发布页面
    @RequestMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog",blog);
        return INPUT;
    }

    // 博客提交（发布或保存底稿）
    @RequestMapping(value = "/blogs",method = RequestMethod.POST)
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));

        // 博客发表中的新增的标签业务处理
        /*String ids;

        Pattern pattern = Pattern.compile("[0-9]*");
        String tagIds = blog.getTagIds();
        String[] split = tagIds.split(",");
        for (String id : split) {
            boolean matches = pattern.matcher(id).matches();
            if (!matches){
                Tag tag1 = new Tag();
                tag1.setName(id);
                Tag tag = tagService.saveTag(tag1);
                Long id1 = tag.getId();
                id = id1.toString();
            }
            // 重新拼接id
            ids += id+",";
        }*/

        blog.setTags(tagService.listTag(blog.getTagIds()));
        Blog b;
        if (blog.getId() == null) {
            b =  blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog.getId(), blog);
        }

        if (b == null ) {
            attributes.addFlashAttribute("message", "操作失败");
        } else {
            attributes.addFlashAttribute("message", "操作成功");
        }
        return REDIRECT_LIST;
    }

    @RequestMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "删除成功");
        return REDIRECT_LIST;
    }
}
