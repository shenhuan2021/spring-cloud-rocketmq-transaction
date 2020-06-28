# <center>RocketMQ实现分布式事务</center>

## 项目总体技术选型

```
SpringCloud(Finchley.RELEASE) + SpringBoot2.0.4 + Maven3.5.4 + RocketMQ4.3 +MySQL + lombok(插件)
```


## 测试流程

页面输入：

```
http://localhost:9001/api/v1/order/save?userId=1&productId=1&total=4	
```

订单微服务执行情况（订单服务事务执行成功）

商品微服务执行情况（商品服务事务执行成功）

当然你也可以通过修改参数来模拟分布式事务出现的各种情况。
<br>




该项目是基于雨点的名字修改的（https://www.cnblogs.com/qdhxhz/p/11191399.html）