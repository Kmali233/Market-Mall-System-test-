package com.hmall.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hmall.search.domain.dto.ItemDTO;
import com.hmall.search.domain.po.Item;

import java.util.Collection;
import java.util.List;

public interface IItemService extends IService<Item> {
    List<ItemDTO> queryItemByIds(Collection<Long> ids);
}
