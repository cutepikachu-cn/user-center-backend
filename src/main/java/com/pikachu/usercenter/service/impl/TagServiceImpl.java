package com.pikachu.usercenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pikachu.usercenter.mapper.TagMapper;
import com.pikachu.usercenter.model.entity.Tag;
import com.pikachu.usercenter.service.TagService;
import org.springframework.stereotype.Service;

/**
 * @author 28944
 * @description 针对表【tag(标签表)】的数据库操作Service实现
 * @createDate 2023-12-16 19:56:51
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
        implements TagService {

}




