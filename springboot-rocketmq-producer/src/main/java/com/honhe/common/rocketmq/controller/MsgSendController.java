package com.honhe.common.rocketmq.controller;

import com.honhe.common.rocketmq.TagConstants;
import com.honhe.common.rocketmq.TopicEnum;
import com.honhe.common.rocketmq.producer.processor.MQProducerSendMsgProcessor;
import com.honhe.common.rocketmq.producer.processor.MQSendResult;
import com.honhe.common.rocketmq.result.ResultData;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class MsgSendController {

    private static final Logger logger = LoggerFactory.getLogger(MsgSendController.class);

    /**使用RocketMq的生产者*/
    @Autowired
    private DefaultMQProducer defaultMQProducer;
    /**使用自己封装的生产者*/
    @Autowired
    private MQProducerSendMsgProcessor producerSendMsgProcessor;

    /**
     * 发送消息
     *
     * @throws InterruptedException
     * @throws MQBrokerException
     * @throws RemotingException
     * @throws MQClientException
     */
    @RequestMapping("send1")
    public ResultData send(@RequestParam(required = false) String msg) throws MQClientException, RemotingException, MQBrokerException, InterruptedException{
        if(StringUtils.isEmpty(msg)){
            msg = "demo msg test";
        }
        logger.info("开始发送消息："+msg);
        Message sendMsg = new Message(TopicEnum.DemoTopic.getCode(), TagConstants.DemoTopic.DemoTag,msg.getBytes());
        //默认3秒超时
        SendResult sendResult = defaultMQProducer.send(sendMsg);
        logger.info("消息发送响应信息："+sendResult.toString());
        return new ResultData(true,"消息发送响应信息："+sendResult.toString(),null);
    }
    /**
     * 使用自己封装的生产者发送消息
     *
     */
    @RequestMapping("send2")
    public ResultData sendByMyProduct(@RequestParam(required = false) String msg){
        if(StringUtils.isEmpty(msg)) {
             msg = "demo msg test2";
        }
        logger.info("开始发送消息："+msg);
        MQSendResult mqSendResult = producerSendMsgProcessor.send(TopicEnum.DemoTopic, TagConstants.DemoTopic.DemoTag, msg);
        logger.info("消息发送响应信息："+mqSendResult.toString());
        return new ResultData(true,"消息发送响应信息："+mqSendResult.toString(),null);
    }
    /**
     * 使用自己封装的生产者发送消息
     *
     */
    @RequestMapping("send3")
    public ResultData sendMsg3(@RequestParam(required = false) String msg,@RequestParam("tag") String tag){
        if(StringUtils.isEmpty(msg)) {
             msg = "demo msg test3";
        }
        logger.info("开始发送消息："+msg);
        MQSendResult mqSendResult = producerSendMsgProcessor.send(TopicEnum.DemoNewTopic, tag, msg);
        logger.info("消息发送响应信息："+mqSendResult.toString());
        return new ResultData(true,"消息发送响应信息："+mqSendResult.toString(),null);
    }
}
