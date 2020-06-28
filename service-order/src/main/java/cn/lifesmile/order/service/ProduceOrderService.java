package cn.lifesmile.order.service;



public interface ProduceOrderService {

     /**
      * @Description: 下单接口
      * @author xub
      */
     int save(int userId, int produceId, int total);
}
