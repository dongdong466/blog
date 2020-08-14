package com.dong1990.blog.service.iml;

import com.dong1990.blog.NotFoundException;
import com.dong1990.blog.dao.BlogRepository;
import com.dong1990.blog.pojo.Blog;
import com.dong1990.blog.pojo.Type;
import com.dong1990.blog.service.BlogService;
import com.dong1990.blog.util.MarkdownUtils;
import com.dong1990.blog.util.MyBeanUtils;
import com.dong1990.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Blog getBlog(Long id) {
        Optional<Blog> byId = blogRepository.findById(id);
        return byId.isPresent() ? byId.get() : null;
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = getBlog(id);
        if (blog == null) {
            throw new NotFoundException("该博客不存在");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog,b);
        // 仅仅展示前端，不修改数据库
        String content = b.getContent();
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));

        // 博客详情页面views刷新更新一次
        blogRepository.updateViews(id);
        return b;
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        /*
        * Root<Blog> root 代表了要查询的对象是blog，把blog对象映射成一个root，从root中可以获取一些表的字段和属性名称
        * CriteriaQuery<?> query 查询的一个条件容器，条件可以放在这里，设置具体某一个条件的表达式
        * CriteriaBuilder criteriaBuilder 设置具体某一个条件的表达式，比如说条件相等，或者模糊查询
        * */
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // 新建list集合，放置一些组合条件 Predicate 阐明，断言的意思
                List<Predicate> predicates = new ArrayList<>();
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(criteriaBuilder.like(root.<String>get("title"), "%"+blog.getTitle()+"%"));
                }
                // 第二个条件查询分类的id值
                if (blog.getTypeId() != null) {
                    predicates.add(criteriaBuilder.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }
                // 第三个条件是否是推荐
                if (blog.isRecommend()) {
                    predicates.add(criteriaBuilder.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                // 开始执行查询，按条件查询，将list转换成一个数组
                query.where(predicates.toArray(new Predicate[predicates.size()]));
                // 自动完成组合的条件去完成自动拼接的动态查询的sql语句
                // Specification这种规范在spring boot中已经有封装，直接使用就可以
                return null; // 这个不用管了
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findPublished(pageable);
    }


    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                // 关联表的查询
                Join join = root.join("tags");
                return cb.equal(join.get("id"),tagId);
            }
        },pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "updateTime");
        Pageable pageable = PageRequest.of(0, size, sort);
        return blogRepository.findTop(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String, List<Blog>> map = new HashMap<>();
        for (String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        map = (HashMap<String, List<Blog>>) sortMapBykeyDesc(map) ;//key降序
        return map;
    }
    // 倒序 方法处理
    public  Map<String, List<Blog>> sortMapBykeyDesc(Map<String, List<Blog>> oriMap) {
        Map<String, List<Blog>> sortedMap = new LinkedHashMap<String, List<Blog>>();
        try {
            if (oriMap != null && !oriMap.isEmpty()) {
                List<Map.Entry<String, List<Blog>>> entryList = new ArrayList<Map.Entry<String, List<Blog>>>(oriMap.entrySet());
                Collections.sort(entryList,
                        new Comparator<Map.Entry<String, List<Blog>>>() {
                            public int compare(Map.Entry<String, List<Blog>> entry1,
                                               Map.Entry<String, List<Blog>> entry2) {
                                int value1 = 0, value2 = 0;
                                try {
                                    value1 = Integer.parseInt(entry1.getKey());
                                    value2 = Integer.parseInt(entry2.getKey());
                                } catch (NumberFormatException e) {
                                    value1 = 0;
                                    value2 = 0;
                                }
                                return value2 - value1;
                            }
                        });
                Iterator<Map.Entry<String, List<Blog>>> iter = entryList.iterator();
                Map.Entry<String, List<Blog>> tmpEntry = null;
                while (iter.hasNext()) {
                    tmpEntry = iter.next();
                    sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
                }
            }
        } catch (Exception e) {
        }
        return sortedMap;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = getBlog(id);
        if (b == null) {
            throw new NotFoundException("该博客不存在");
        }
        // 更新编辑博客的话不需要重新更新该博客数据的createtime和views
        // b 根据传过来的id获取到的blog对象
        // blog是页面带过来的blog对象
        // 把blog对象中的createtime和views空的，
        // MyBeanUtils.getNullPropertyNames 过滤掉属性值为空的属性，返回没有属性值的属性组成的数组
        // 最后就是把blog对象有值的属性到b的里面来，b里面的createtime和views是不变的
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogRepository.save(b);// 这里就是更新的动作
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }
}
