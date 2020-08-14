package com.dong1990.blog.web;

import com.dong1990.blog.NotFoundException;
import com.dong1990.blog.service.BlogService;
import com.dong1990.blog.service.TagService;
import com.dong1990.blog.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class IndexController {

    //@RequestMapping(value = "index",method = RequestMethod.GET)
    //public String index(){
        /*//int i = 9/0;
        String blog = null;
        if(blog == null){
            throw new NotFoundException("博客资源找不到");
        }*/
        //return "index";
    //}

    // 测试日志，定义两个参数，最终返回的是一个“index”的字符串，查看方法执行的先后顺序
    /*@RequestMapping(value = "/{id}/{name}",method = RequestMethod.GET)
    public String index(@PathVariable Integer id, @PathVariable String name){
        System.out.println("-------index------------");
        return "index";
    }*/

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;



    @RequestMapping("/")
    public String index(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        Model model) {
        model.addAttribute("page",blogService.listBlog(pageable));
        model.addAttribute("types", typeService.listTypeTop(6));
        model.addAttribute("tags", tagService.listTagTop(10));
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(8));
        return "index";
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         @RequestParam String query, Model model) {
        model.addAttribute("page", blogService.listBlog("%"+query+"%", pageable));
        model.addAttribute("query", query);
        return "search";
    }


    // 博客详情页面
    @RequestMapping("/blog/{id}")
    public String blog(@PathVariable Long id,Model model) {
        // 将获取的博客内容进行html渲染，以markdown转换成html模式展示在页面上
        //model.addAttribute("blog", blogService.getBlog(id));
        // 专门用于前端验证渲染的方法
        model.addAttribute("blog", blogService.getAndConvert(id));
        return "blog";
    }

    @RequestMapping("/footer/newblog")
    public String newblogs(Model model) {
        model.addAttribute("newblogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }

    @RequestMapping(value = "blog",method = RequestMethod.GET)
    public String blog(){

        return "blog";
    }
}
