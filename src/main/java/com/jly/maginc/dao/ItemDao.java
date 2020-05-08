package com.jly.maginc.dao;

import com.jly.maginc.po.JdItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDao extends JpaRepository<JdItem,Long> {

}
