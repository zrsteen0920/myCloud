springcloud个人学习加强
##### 服务注册中心consiu

支持服务的的发现和注册和分布式注册中心,eureka 不支持分布式注册中心,配置信息注册conslu服务器,直接从conslu获取,
A高可用  保证系统可以用性
C一致性
P分区容错性
consul    Go CP
eureka     java AP
zookeeper java   CP

consul

```
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-consul-discovery</artifactId>
        </dependency>
```



```
    ####Spring Cloud Consul for Service Discovery
    cloud:
      consul:
        host: localhost
        port: 8500
        discovery:
          service-name: ${spring.application.name}
```


bootstrap.yml优先级比application.yml优先级高 且不会被覆盖

##### 负载均衡 loadbalaner

客户端负载均衡和服务器端负载均衡有什么区别?

Nginx是服务器负载均衡，客户端所有请求都会交给nginx，然后由nginx实现转发请求，即负载均衡是由服务端实现的。

loadbalancer本地负载均衡，在调用微服务接口时候，会在注册中心上获取注册信息服务列表之后缓存到JVM本地，从而在本地实现RPC远程服务调用技术。



加在消费者的pom

*<!--loadbalancer-->
*<**dependency**>
  <**groupId**>org.springframework.cloud</**groupId**>
  <**artifactId**>spring-cloud-starter-loadbalancer</**artifactId**>
</**dependency**>

默认使用轮询 

使用随机



##### OpenFeign负载均衡   集成了loadbalaner(重点)

**OpenFeign默认超时时间->       60S,超时后报错**

```
    openfeign:
      client:
        config:
          # 全局超时配置
          default:
            #连接超时时间
            connectTimeout: 3000
            #读取超时时间
            readTimeout: 3000
          #指定服务
          cloud-payment-service:
            #连接超时时间
            connectTimeout: 11000
            #读取超时时间
            readTimeout: 11000
```

**如果超时了怎么解决->进行重试**

重试机制默认是关闭的

```
package com.zrs.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @auther zzyy
 * @create 2023-11-10 11:09
 */
@Configuration
public class FeignConfig
{
    @Bean
    public Retryer myRetryer()
    {
        //return Retryer.NEVER_RETRY; //Feign默认配置是不走重试策略的

        //最大请求次数为3(1+2)，初始间隔时间为100ms，重试间最大间隔时间为1s
        return new Retryer.Default(100,1,3);
    }
}
```





**OpenFeign中http client     生产务必使用**

如果不做特殊配置，OpenFeign默认使用JDK自带的HttpURLConnection发送HTTP请求，

由于默认HttpURLConnection没有连接池、性能和效率比较低，如果采用默认，性能上不是最牛B的，所以加到最大。

```
<!-- httpclient5-->
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.3</version>
</dependency>
<!-- feign-hc5-->
<dependency>
    <groupId>io.github.openfeign</groupId>
    <artifactId>feign-hc5</artifactId>
    <version>13.1</version>
</dependency>
```

```
#  Apache HttpClient5 配置开启
spring:
  cloud:
    openfeign:
      httpclient:
        hc5:
          enabled: true
```



成功



**对请求和响应进行gzip压缩**

```
      compression:
        request:
          enabled: true
          min-request-size: 2048 #最小触发压缩的大小
          mime-types: text/xml,application/xml,application/json #触发压缩数据类型
        response:
          enabled: true
```



##### 断路器resilience4j





```
        <!--resilience4j-circuitbreaker-->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
        </dependency>
        <!-- 由于断路保护等需要AOP实现，所以必须导入AOP包 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-aop</artifactId>
        </dependency>

```

```
#次数
# feign日志以什么级别监控哪个接口
logging:
  level:
    com:
      atguigu:
        cloud:
          apis:
            PayFeignApi: debug
# Resilience4j CircuitBreaker 按照次数：COUNT_BASED 的例子
#  6次访问中当执行方法的失败率达到50%时CircuitBreaker将进入开启OPEN状态(保险丝跳闸断电)拒绝所有请求。
#  等待5秒后，CircuitBreaker 将自动从开启OPEN状态过渡到半开HALF_OPEN状态，允许一些请求通过以测试服务是否恢复正常。
#  如还是异常CircuitBreaker 将重新进入开启OPEN状态；如正常将进入关闭CLOSE闭合状态恢复正常处理请求。
resilience4j:
  circuitbreaker:
    configs:
      default:
        failureRateThreshold: 50 #设置50%的调用失败时打开断路器，超过失败请求百分⽐CircuitBreaker变为OPEN状态。
        slidingWindowType: COUNT_BASED # 滑动窗口的类型
        slidingWindowSize: 6 #滑动窗⼝的⼤⼩配置COUNT_BASED表示6个请求，配置TIME_BASED表示6秒
        minimumNumberOfCalls: 6 #断路器计算失败率或慢调用率之前所需的最小样本(每个滑动窗口周期)。如果minimumNumberOfCalls为10，则必须最少记录10个样本，然后才能计算失败率。如果只记录了9次调用，即使所有9次调用都失败，断路器也不会开启。
        automaticTransitionFromOpenToHalfOpenEnabled: true # 是否启用自动从开启状态过渡到半开状态，默认值为true。如果启用，CircuitBreaker将自动从开启状态过渡到半开状态，并允许一些请求通过以测试服务是否恢复正常
        waitDurationInOpenState: 5s #从OPEN到HALF_OPEN状态需要等待的时间
        permittedNumberOfCallsInHalfOpenState: 2 #半开状态允许的最大请求数，默认值为10。在半开状态下，CircuitBreaker将允许最多permittedNumberOfCallsInHalfOpenState个请求通过，如果其中有任何一个请求失败，CircuitBreaker将重新进入开启状态。
        recordExceptions:
        - java.lang.Exception
    instances:
      cloud-payment-service:
        baseConfig: default
```

```
#超时

# Resilience4j CircuitBreaker 按照时间：TIME_BASED 的例子
resilience4j:
  timelimiter:
    configs:
      default:
        timeout-duration: 10s #神坑的位置，timelimiter 默认限制远程1s，超于1s就超时异常，配置了降级，就走降级逻辑
  circuitbreaker:
    configs:
      default:
        failureRateThreshold: 50 #设置50%的调用失败时打开断路器，超过失败请求百分⽐CircuitBreaker变为OPEN状态。
        slowCallDurationThreshold: 2s #慢调用时间阈值，高于这个阈值的视为慢调用并增加慢调用比例。
        slowCallRateThreshold: 30 #慢调用百分比峰值，断路器把调用时间⼤于slowCallDurationThreshold，视为慢调用，当慢调用比例高于阈值，断路器打开，并开启服务降级
        slidingWindowType: TIME_BASED # 滑动窗口的类型
        slidingWindowSize: 2 #滑动窗口的大小配置，配置TIME_BASED表示2秒
        minimumNumberOfCalls: 2 #断路器计算失败率或慢调用率之前所需的最小样本(每个滑动窗口周期)。
        permittedNumberOfCallsInHalfOpenState: 2 #半开状态允许的最大请求数，默认值为10。
        waitDurationInOpenState: 5s #从OPEN到HALF_OPEN状态需要等待的时间
        recordExceptions:
          - java.lang.Exception
    instances:
      cloud-payment-service:
        baseConfig: default

```




##### 隔离BulkHead

##### 
