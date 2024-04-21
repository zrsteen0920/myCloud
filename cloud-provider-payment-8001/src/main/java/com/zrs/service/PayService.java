package com.zrs.service;

import com.zrs.entities.Pay;

import java.util.List;

/**
 * @auther zzyy
 * @create 2023-11-03 18:40
 */
public interface PayService
{
    public int add(Pay pay);
    public int delete(Integer id);
    public int update(Pay pay);
    
    public Pay   getById(Integer id);
    public List<Pay> getAll();
}
