package com.ambow.springboot_adoption.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//接收查询的结果 传递总条数 和当前页的集合数
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageBean<T> {

    private Long total;//总条数
    private List<T> items;//当前页的数据集合

}
