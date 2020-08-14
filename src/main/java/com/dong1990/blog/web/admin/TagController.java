package com.dong1990.blog.web.admin;

import com.dong1990.blog.pojo.Tag;
import com.dong1990.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TagController {

    @Autowired
    private TagService tagService;

    /*
     * 使用Pageable 分页
     * Pageable 是spring Data库中定义的一个接口，该接口是所有分页相关信息的一个抽象，
     * 通过该接口，我们可以得到和分页相关所有信息（例如pageNumber、pageSize等）。
     * Pageable定义了很多方法，但其核心的信息只有两个：一是分页的信息（page、size），二是排序的信息
     * 在springmvc的请求中只需要在方法的参数中直接定义一个pageable类型的参数，
     * 当Spring发现这个参数时，Spring会自动的根据request的参数来组装该pageable对象
     * Spring data提供了@PageableDefault帮助我们个性化的设置pageable的默认配置
     * */
    @RequestMapping(value = "/tags",method = RequestMethod.GET)
    public String types(@PageableDefault(size = 5,sort = {"id"},direction = Sort.Direction.DESC) Pageable pageable, Model model){
        // model 页面展示的模型，将分页查询的数据放到模型里，这样前端的页面就可以拿到pageable数据
        model.addAttribute("page",tagService.listTag(pageable));
        return "admin/tags";
    }

    @RequestMapping("/tags/input")
    public String input(Model model) {
        model.addAttribute("tag", new Tag());
        return "admin/tags-input";
    }

    // 方法参数中校验的type必须要和BindingResult挨在一起，不然校验没有效果
    @RequestMapping(value = "/tags",method = RequestMethod.POST)
    public String post(@Valid Tag tag, BindingResult result, RedirectAttributes attributes){

        // 再次验证，防止出现同样的分类名称
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            // 如果类型已经存在，我们需要给前端返回一个错误
            result.rejectValue("name","nameError","不能添加重复的标签");
        }
        // type为空保存的话会给出一个错误提示，实际上会在后台抛出一个异常
        // 如果希望后台的异常信息能够传到前端页面上去的话，我们需要加一个@Valid校验，
        // 还需要result来接收一个校验后的结果
        // 如果校验没通过，result有错误的话，返回指定页面
        if (result.hasErrors()) {
            return "admin/tags-input";
        }
        Tag t = tagService.saveTag(tag);
        // 给前端以提示，使用了redirect，所以需要Redirect Attributes
        if (t == null ) {
            attributes.addFlashAttribute("message", "新增失败");
        } else {
            attributes.addFlashAttribute("message", "新增成功");
        }
        return "redirect:/admin/tags";
    }

    @RequestMapping("/tags/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        model.addAttribute("tag", tagService.getTag(id));
        return "admin/tags-input";
    }

    // 点击分类编辑跳转之后需要重新保存的方法
    // 多加一个参数id，根据这个id进行修改保存
    // 通过 @PathVariable 可以将 URL 中占位符参数绑定到控制器处理方法的入参中：URL 中的 {xxx} 占位符可以通过@PathVariable(“xxx“) 绑定到操作方法的入参中。
    @RequestMapping("/tags/{id}")
    public String editPost(@Valid Tag tag, BindingResult result, @PathVariable Long id, RedirectAttributes attributes) {
        Tag tag1 = tagService.getTagByName(tag.getName());
        if (tag1 != null) {
            result.rejectValue("name","nameError","不能添加重复的标签");
        }
        if (result.hasErrors()) {
            return "admin/tags-input";
        }
        Tag t = tagService.updateTag(id,tag);
        if (t == null ) {
            attributes.addFlashAttribute("message", "更新失败");
        } else {
            attributes.addFlashAttribute("message", "更新成功");
        }
        return "redirect:/admin/tags";
    }

    @RequestMapping("/tags/{id}/delete")
    public String delete(@PathVariable Long id,RedirectAttributes attributes) {
        tagService.deleteTag(id);
        attributes.addFlashAttribute("message", "删除成功");
        return "redirect:/admin/tags";
    }

}
