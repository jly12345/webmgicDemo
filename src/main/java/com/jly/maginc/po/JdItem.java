package com.jly.maginc.po;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "jd_item")
@Data
public class JdItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "spu", nullable = true)
    private Long spu;
    @Column(name = "sku", nullable = true)
    private Long sku;
    @Column(name = "title", nullable = true, length = 100)
    private String title;
    @Column(name = "price", nullable = true)
    private Double price;
    @Column(name = "pic", nullable = true, length = 200)
    private String pic;
    @Column(name = "url", nullable = true, length = 200)
    private String url;
    @Column(name = "created", nullable = true)
    private Date created;
    @Column(name = "updated", nullable = true)
    private Date updated;

}
