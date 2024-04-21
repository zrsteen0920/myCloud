package com.zrs.controller;

import cn.hutool.core.bean.BeanUtil;
import com.zrs.entities.Pay;
import com.zrs.entities.PayDTO;
import com.zrs.resp.ResultData;
import com.zrs.service.PayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@Tag(name = "支付微服务",description = "支付crud")
public class PayController {
    @Resource
    private PayService payService;
    @PostMapping(value = "/pay/add")
    @Operation(summary = "新增",description = "新增支付流水方法,json串做参数")
    private ResultData<String> addPay(@RequestBody Pay pay){
        log.debug(pay.toString());
        int add = payService.add(pay);
        return ResultData.success("成功了"+add+"条");
    }


    @DeleteMapping(value = "/pay/delete/{id}")
    private ResultData deletePay(@PathVariable("id") Integer id){
        log.debug(id.toString());
        int add = payService.delete(id);
        return ResultData.success( "成功了"+add+"条");
    }


    @PutMapping(value = "/pay/update")
    private ResultData updatePay(PayDTO payDTO){
        log.debug(payDTO.toString());
        Pay pay = new Pay();
        BeanUtil.copyProperties(payDTO,pay);

        int add = payService.update(pay);
        return ResultData.success( "成功了"+add+"条");
    }

    @GetMapping(value = "/pay/get/{id}")
    private ResultData getPayById(@PathVariable("id") Integer id) throws Exception {
        if(id == -4) throw new RuntimeException("id不能为负数");
        log.debug(id.toString());
        Pay pay = payService.getById(id);
        PayDTO payDto = new PayDTO();
        BeanUtil.copyProperties(pay,payDto);
        return ResultData.success( payDto);
    }



    @GetMapping(value = "/pay/getPayAll")
    private ResultData getPayAll(){
        List<Pay> payList = payService.getAll();
        List<PayDTO> payDtoList = new ArrayList<>();
        for (int i = 0; i < payList.size(); i++) {
            Pay  pay =payList.get(i);
            PayDTO payDto = new PayDTO();
            BeanUtil.copyProperties(pay,payDto);
            payDtoList.add(payDto);
        }
        return ResultData.success( payDtoList);
    }


    @Value("${server.port}")
    private String port;
    @GetMapping(value = "/pay/get/info")
    private ResultData getInfoByConsul(@Value("${atguifu.info}") String atguiguInfo)
    {
        return ResultData.success("atguiguInfo: "+atguiguInfo+"\t"+"port: "+port);
    }




}
