package com.zrs.controller;

import com.zrs.apis.PayFeignApi;
import com.zrs.entities.PayDTO;
import com.zrs.resp.ResultData;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther zzyy
 * @create 2023-11-04 16:00
 */
@RestController
public class OrderController{

    @Resource
    private PayFeignApi payFeignApi;

    @PostMapping(value = "/feign/pay/add")
    public ResultData addOrder(@RequestBody PayDTO pay){
        return payFeignApi.addPay(pay);
    }
    @GetMapping(value = "/feign/pay/get/{id}")
    public ResultData getPayById(@PathVariable("id") Integer id){
        return payFeignApi.getPayById(id);
    }

    @GetMapping(value = "/feign/pay/get/info")
    public ResultData getInfoByConsul(){
        try {

            return payFeignApi.getInfoByConsul();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
 
 