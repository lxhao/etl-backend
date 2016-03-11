package com.mq.service;

public class MQ_Receiver {

	public Object receive(String destination) throws Exception {
		MQ_Service receiver = MQ_Service.getInstance();
		System.out.println("接下来要接收数据");
		return receiver.receive(destination);
	}

}