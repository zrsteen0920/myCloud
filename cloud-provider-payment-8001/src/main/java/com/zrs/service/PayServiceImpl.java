package com.zrs.service;

import com.zrs.entities.Pay;
import com.zrs.mapper.PayMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther zzyy
 * @create 2023-11-03 18:44
 */
@Service
public class PayServiceImpl implements PayService{
    @Resource
    PayMapper payMapper;
    @Override
    public int add(Pay pay){
        return payMapper.insertSelective(pay);
    }
    @Override
    public int delete(Integer id){
        return payMapper.deleteByPrimaryKey(id);
    }
    @Override
    public int update(Pay pay){
        return payMapper.updateByPrimaryKeySelective(pay);
    }
    @Override
    public Pay getById(Integer id){
        return payMapper.selectByPrimaryKey(id);
    }
    @Override
    public List<Pay> getAll(){
        return payMapper.selectAll();
    }
}
 