package com.zrs.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @auther zzyy
 * @create 2023-11-03 18:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayDTO implements Serializable
{
    private Integer id;
    //支付流水号
    private String payNo;
    //订单流水号
    private String orderNo;
    //用户账号ID
    private Integer userId;
    //交易金额
    private BigDecimal amount;
}
