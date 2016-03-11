package com.mq.service;

import java.io.Serializable;

public class MQ_Sender {


	public void sender(Serializable object, String destination) throws Exception {
			MQ_Service sender = MQ_Service.getInstance();
			System.out.println("接下来要发送数据");
			sender.send(object, destination);
			System.out.println("数据已经发送完毕！");
	}

}