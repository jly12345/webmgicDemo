package com.jly.maginc.service.impl;

import com.jly.maginc.dao.ItemDao;
import com.jly.maginc.po.JdItem;
import com.jly.maginc.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    @Autowired
    private ItemDao itemDao;

    @Override
    public void save(JdItem item) {
        itemDao.save(item);
    }

    @Override
    public List<JdItem> findAll(JdItem item) {
        Example<JdItem> ex = Example.of(item);
        return itemDao.findAll(ex);
    }
}
