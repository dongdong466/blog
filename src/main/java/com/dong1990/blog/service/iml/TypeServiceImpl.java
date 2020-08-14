package com.dong1990.blog.service.iml;

import com.dong1990.blog.NotFoundException;
import com.dong1990.blog.dao.TypeRepository;
import com.dong1990.blog.pojo.Type;
import com.dong1990.blog.service.TypeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeRepository typeRepository;


    // 新增放在事务里
    @Transactional
    @Override
    public Type saveType(Type type) {
        return typeRepository.save(type);
    }

    @Transactional
    @Override
    public Type getType(Long id) {
        Optional<Type> byId = typeRepository.findById(id);
        return byId.isPresent() ? byId.get() : null;
    }

    @Override
    public Type getTypeByName(String name) {
        return typeRepository.findByName(name);
    }

    @Transactional
    @Override
    public Page<Type> listType(Pageable pageable) {
        return typeRepository.findAll(pageable);
    }

    @Override
    public List<Type> listType() {
        return typeRepository.findAll();
    }

    @Override
    public List<Type> listTypeTop(Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"blogs.size");
        // 先构造一个Pageable对象，拿第一页，指定size
        Pageable pageable = PageRequest.of(0,size,sort);
        // 没有通过数量来获取列表数据的方法，所以需要在dao定义
        return typeRepository.findTop(pageable);
    }

    @Transactional
    @Override
    public Type updateType(Long id, Type type) {
        Type type1 = getType(id);
        if(type1 == null){
            throw new NotFoundException("不存在该类型");
        }
        // spring里面提供的一个方法，type里的值复制到type中
        BeanUtils.copyProperties(type,type1);
        // type1当中已经存在id，保存会自动更新
        return typeRepository.save(type1);
    }

    @Transactional
    @Override
    public void deleteType(Long id) {
        typeRepository.deleteById(id);
    }
}
