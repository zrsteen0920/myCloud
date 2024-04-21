package com.zrs.controller;

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
//    public static final String PaymentSrv_URL = "http://localhost:8001";//先写死，硬编码
    public static final String PaymentSrv_URL = "http://cloud-payment-service";
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 一般情况下，通过浏览器的地址栏输入url，发送的只能是get请求
     * 我们底层调用的是post方法，模拟消费者发送get请求，客户端消费者
     * 参数可以不添加@RequestBody
     * @param payDTO
     * @return
     */
    @GetMapping("/consumer/pay/add")
    public ResultData addOrder(PayDTO payDTO){
        return restTemplate.postForObject(PaymentSrv_URL + "/pay/add",payDTO,ResultData.class);
    }
    // 删除+修改操作作为家庭作业，O(∩_∩)O。。。。。。。
    @GetMapping("/consumer/pay/get/{id}")
    public ResultData getPayInfo(@PathVariable("id") Integer id){
        return restTemplate.getForObject(PaymentSrv_URL + "/pay/get/"+id, ResultData.class, id);
    }



    @GetMapping("/consumer/delete/{id}")
    public ResultData deletePay(@PathVariable("id") Integer id){
        Map<String, Object> uriVariables = new HashMap<>();
        uriVariables.put("id",id);

        // 替换URL中的占位符{id}
        String deleteUrl = String.format(PaymentSrv_URL + "/pay/delete/{id}", id);

        HttpEntity<?> requestEntity = new ResponseEntity<>( HttpStatusCode.valueOf(200));
        // 发送DELETE请求
        ResponseEntity<ResultData> exchange = restTemplate.exchange(
                deleteUrl,
                HttpMethod.DELETE,
                null, // 如果DELETE请求不需要请求体，则传递null
                ResultData.class // 期望返回的响应体类型
        );

        return exchange.getBody();
    }
    @GetMapping("/consumer/get/info")
    public ResultData getInfo(){
        String url = PaymentSrv_URL + "/pay/get/info";
        return  restTemplate.getForObject(url, ResultData.class);
    }

    @Resource
    private DiscoveryClient discoveryClient;
    @GetMapping("/consumer/discovery")
    public String discovery()
    {
        List<String> services = discoveryClient.getServices();
        for (String element : services) {
            System.out.println(element);
        }

        System.out.println("===================================");

        List<ServiceInstance> instances = discoveryClient.getInstances("cloud-payment-service");
        for (ServiceInstance element : instances) {
            System.out.println(element.getServiceId()+"\t"+element.getHost()+"\t"+element.getPort()+"\t"+element.getUri());
        }

        return instances.get(0).getServiceId()+":"+instances.get(0).getPort();
    }
}
 
 