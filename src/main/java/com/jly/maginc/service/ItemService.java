package com.jly.maginc.service;

import com.jly.maginc.po.JdItem;

import java.util.List;

public interface ItemService {

    /**
     * save
     * @param item
     */
    void save(JdItem item);

    /**
     * find
     * @param item
     * @return
     */
    List<JdItem> findAll(JdItem item);
}
