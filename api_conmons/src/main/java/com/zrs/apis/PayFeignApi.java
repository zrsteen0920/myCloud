package com.zrs.apis;

import com.zrs.entities.PayDTO;
import com.zrs.resp.ResultData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("cloud-payment-service")
public interface PayFeignApi {
    @PostMapping(value = "/pay/add")
    public ResultData<String> addPay(@RequestBody PayDTO pay);
    @GetMapping(value = "/pay/get/{id}")
    public  ResultData getPayById(@PathVariable("id") Integer id);

    @GetMapping(value = "/pay/get/info")
    public ResultData getInfoByConsul();


    /**
     * Resilience4j CircuitBreaker 的例子
     * @param id
     * @return
     */
    @GetMapping(value = "/pay/circuit/{id}")
    public String myCircuit(@PathVariable("id") Integer id);
}
