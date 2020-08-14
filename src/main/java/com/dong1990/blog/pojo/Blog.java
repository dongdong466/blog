package com.dong1990.blog.pojo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "t_blog")
public class Blog {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;// 博客id
    private String title;// 博客标题
    // 大字段类型，只有第一次初始化的时候才会有效
    // lazy，只有get使用的时候才回家再content
    @Basic(fetch = FetchType.LAZY)
    @Lob
    private String content;// 博客内容
    private String firstPicture;// 首图
    private String flag;// 标记，原创、转载
    private Integer views;// 浏览次数
    private boolean appreciation;// 赞赏是否开启
    private boolean shareStatement;// 分享转载声明是否开启
    private boolean commentabled;// 评论
    private boolean published;// 是否发布，或保存草稿
    private boolean recommend;// 是否推荐
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;// 创建时间
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;// 更新时间
    private String description;// 博客描述

    //属于一个分类，关系维护端
    @ManyToOne
    private Type type;

    // 设置级联关系，级联操作
    // 新增博客的时候，如果需要新增标签的话就连同标签一起新增到数据库,当前实体类进行保存操作时，同时保存其关联的实体。
    @ManyToMany(cascade = {CascadeType.PERSIST})
    private List<Tag> tags = new ArrayList<Tag>();

    // 在blog这一端属于多对一的关系，属于关系的维护方
    @ManyToOne
    private User user;

    // 一对多关系,这次轮到blog为被维护方
    @OneToMany(mappedBy = "blog")
    private List<Comment> comments = new ArrayList<Comment>();

    // 该注释表明该字段不被序列化，也就是不会入库，不和数据库一一映射
    @Transient
    private String tagIds;

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }

    // 博客列表点编辑进入编辑发布页面，需要将原有博客数据初始化，多个标签需要单独处理下
    public void init() {
        this.tagIds = tagsToIds(this.getTags());
    }

    // 最终获取到的值是 1,2,3 这样子的
    private String tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            StringBuffer ids = new StringBuffer();
            boolean flag = false;
            for (Tag tag : tags) {
                if (flag) {
                    ids.append(",");
                } else {
                    flag = true;
                }
                ids.append(tag.getId());
            }
            return ids.toString();
        } else {
            return tagIds;
        }
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Blog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFirstPicture() {
        return firstPicture;
    }

    public void setFirstPicture(String firstPicture) {
        this.firstPicture = firstPicture;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public boolean isAppreciation() {
        return appreciation;
    }

    public void setAppreciation(boolean appreciation) {
        this.appreciation = appreciation;
    }

    public boolean isShareStatement() {
        return shareStatement;
    }

    public void setShareStatement(boolean shareStatement) {
        this.shareStatement = shareStatement;
    }

    public boolean isCommentabled() {
        return commentabled;
    }

    public void setCommentabled(boolean commentabled) {
        this.commentabled = commentabled;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public boolean isRecommend() {
        return recommend;
    }

    public void setRecommend(boolean recommend) {
        this.recommend = recommend;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", firstPicture='" + firstPicture + '\'' +
                ", flag='" + flag + '\'' +
                ", views=" + views +
                ", appreciation=" + appreciation +
                ", shareStatement=" + shareStatement +
                ", commentabled=" + commentabled +
                ", published=" + published +
                ", recommend=" + recommend +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", description=" + description +
                '}';
    }
}
