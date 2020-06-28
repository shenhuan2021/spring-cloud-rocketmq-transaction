package cn.lifesmile.order.mqservice;

import cn.lifesmile.order.service.ProduceOrderService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TransactionListenerImpl implements TransactionListener {

    private static final Map<String,Boolean> tranId=new ConcurrentHashMap<>();

    private ProduceOrderService produceOrderService;



    public TransactionListenerImpl(ProduceOrderService produceOrderService) {
        this.produceOrderService = produceOrderService;
    }

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        log.info("=========本地事务开始执行=============");
        String message = new String(msg.getBody());
        JSONObject jsonObject = JSONObject.parseObject(message);
        Integer productId = jsonObject.getInteger("productId");
        Integer total = jsonObject.getInteger("total");
        int userId = Integer.parseInt(arg.toString());
        //模拟执行本地事务begin=======
        /**
         * 本地事务执行会有三种可能
         * 1、commit 成功
         * 2、Rollback 失败
         * 3、网络等原因服务宕机收不到返回结果
         */
        int result = produceOrderService.save(userId, productId, total);
        //模拟执行本地事务end========
        //TODO 实际开发是根据本地事务执行结果自动返回
        //1、二次确认消息，然后消费者可以消费
        if (result == 0) {
            //记得已处理的事务ID，实际生产中，可用MySQL表记录
            tranId.put(msg.getKeys(),true);
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        //2、回滚消息，Broker端会删除半消息
        if (result == 1) {
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        //3、Broker端会进行回查消息
        return LocalTransactionState.UNKNOW;
    }

    /**
     * 只有上面接口返回 LocalTransactionState.UNKNOW 才会调用查接口被调用
     *
     * @param msg 消息
     * @return
     */
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        log.info("==========回查接口=========");
        log.info("回查Msg,key:[{}],id:[{}]", msg.getKeys(), msg.getMsgId());

        //TODO 检查是否已经处理成功，但是没有发送COMMIT_MESSAGE
        /**
         * 因为有种情况就是：上面本地事务执行成功了，但是return LocalTransactionState.COMMIT_MESSAG的时候
         * 服务挂了，那么最终 Brock还未收到消息的二次确定，还是个半消息 ，所以当重新启动的时候还是回调这个回调接口。
         * 如果不先查询上面本地事务的执行情况 直接在执行本地事务，那么就相当于成功执行了两次本地事务了。
         */
        String keys = msg.getKeys();
        if (tranId.get(keys) ==null ){
            //return LocalTransactionState.COMMIT_MESSAGE;
        }

        //根据实际业务返回，我这里直接返回COMMIT_MESSAGE
        //若需要丢弃则返回 ROLLBACK_MESSAGE
        return LocalTransactionState.COMMIT_MESSAGE;
    }
}
