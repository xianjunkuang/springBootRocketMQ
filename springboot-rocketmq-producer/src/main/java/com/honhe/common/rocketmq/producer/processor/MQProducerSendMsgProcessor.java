package com.honhe.common.rocketmq.producer.processor;

import com.alibaba.fastjson.JSON;
import com.honhe.common.rocketmq.TopicEnum;
import com.honhe.common.rocketmq.constants.RocketMQErrorEnum;
import com.honhe.common.rocketmq.exception.AppException;
import com.honhe.common.rocketmq.msgbean.base.BaseMQMessageVO;
import com.honhe.common.rocketmq.producer.MQProducerCondition;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
/**
 * 生产者发送消息处理器
 * .<br/>
 * 
 * Copyright: Copyright (c) 2017  zteits
 * 
 * @ClassName: RocketMQProducerSendMsgProcessor
 * @Description: 
 * @version: v1.0.0
 * @author: zhaowg
 * @date: 2018年2月28日 上午11:12:05
 * Modification History:
 * Date             Author          Version            Description
 *---------------------------------------------------------*
 * 2018年2月28日      zhaowg           v1.0.0               创建
 */
@Conditional(MQProducerCondition.class)
@Component
public class MQProducerSendMsgProcessor {
	private static final Logger logger = LoggerFactory.getLogger(MQProducerSendMsgProcessor.class);
	@Autowired
	private DefaultMQProducer defaultMQProducer;
	
	/**
	 * 发送消息,仅发送一次，不关心是否发送成功
	 * @param topic	主题
	 * @param tag 消息标签，只支持设置一个Tag（服务端消息过滤使用）
	 * @param keys 消息关键词，多个Key用MessageConst.KEY_SEPARATOR隔开（查询消息使用）
	 * @param msg	消息
	 * 2018年2月28日 zhaowg
	 */
	public void sendOneway(TopicEnum topic,String tag,String keys,BaseMQMessageVO msg){
		this.sendOneway(topic, tag, keys, JSON.toJSONString(msg));
	}
	
	/**
	 * 发送消息,仅发送一次，不关心是否发送成功
	 * @param topic	主题
	 * @param tag 消息标签，只支持设置一个Tag（服务端消息过滤使用）
	 * @param keys 消息关键词，多个Key用MessageConst.KEY_SEPARATOR隔开（查询消息使用）
	 * @param msg	消息
	 * 2018年2月28日 zhaowg
	 */
	public void sendOneway(TopicEnum topic,String tag,String keys,String msg){
		logger.info("发送信息到消息队列(只发送一次，不关心是否成功)。topic:{},tag:{},keys:{},msg:{}",topic==null?"":topic.getCode()+"["+topic.getMsg()+"]",
				tag==null?"":tag,keys,msg);
		try {
			validateSendMsg(topic, tag, msg);
			Message sendMsg = new Message(topic.getCode(),tag==null?null:tag,StringUtils.isEmpty(keys)?null:keys,msg.getBytes());
			//默认3秒超时
			defaultMQProducer.sendOneway(sendMsg);
		} catch (MQClientException | RemotingException | InterruptedException e) {
			logger.error("消息发送失败",e);
		}
	}
	
	/**
	 * 同步发送消息
	 * @param topic	主题
	 * @param tag 消息标签，只支持设置一个Tag（服务端消息过滤使用）
	 * @param msg	消息
	 * @return
	 * 2018年2月28日 zhaowg
	 */
	public MQSendResult send(TopicEnum topic,String tag,String msg){
		return this.send(topic, tag, null, msg);
	}
	
	/**
	 * 同步发送消息
	 * @param topic	主题
	 * @param tag 消息标签，只支持设置一个Tag（服务端消息过滤使用）
	 * @param keys 消息关键词，多个Key用MessageConst.KEY_SEPARATOR隔开（查询消息使用）
	 * @param msg	消息
	 * @return
	 * 2018年2月28日 zhaowg
	 */
	public MQSendResult send(TopicEnum topic,String tag,String keys,String msg){
		logger.info("发送信息到消息队列。topic:{},tag:{},keys:{},msg:{}",topic==null?"":topic.getCode()+"["+topic.getMsg()+"]",
				tag==null?"":tag,keys,msg);
		MQSendResult mqSendResult = null;
		try {
			validateSendMsg(topic, tag, msg);
			SendResult sendResult = null;
			Message sendMsg = new Message(topic.getCode(),tag==null?null:tag,StringUtils.isEmpty(keys)?null:keys,msg.getBytes());
			//默认3秒超时
			sendResult = defaultMQProducer.send(sendMsg);
			mqSendResult = new MQSendResult(sendResult);
		}catch(AppException e){
			logger.error(e.getErrMsg());
			mqSendResult = new MQSendResult(e.getErrMsg(), null);
		} catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
			logger.error("消息发送失败",e);
			mqSendResult = new MQSendResult("消息发送失败", e);
		}
		logger.info("发送消息到消息队列的响应信息为："+mqSendResult.toString());
		return mqSendResult==null?new MQSendResult():mqSendResult;
	}
	/**
	 * 校验参数
	 * @param topic
	 * @param tag
	 * @param msg
	 * 2018年3月2日 zhaowg
	 */
	private void validateSendMsg(TopicEnum topic, String tag, String msg) {
		if(topic==null){
			throw new AppException(RocketMQErrorEnum.PARAMM_NULL,"topic为空",false);
		}
		if(tag == null){
			throw new AppException(RocketMQErrorEnum.PARAMM_NULL,"tag为空",false);
		}
		if(msg==null){
			throw new AppException(RocketMQErrorEnum.PARAMM_NULL,"msg为空",false);
		}
	}
	
}
