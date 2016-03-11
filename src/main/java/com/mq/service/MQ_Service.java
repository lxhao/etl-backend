package com.mq.service;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * 数据发送类，用于发送数据 如果获得链接，请查看被注释的代码
 */
public class MQ_Service {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MQ_Service.class);

	private MQ_Service() {
	}

	// 发送对象每次创建一个，用以区别我们使用的对象
	public static MQ_Service getInstance() {
		return new MQ_Service();
	}

	// 发送消息
	@SuppressWarnings("static-access")
	public void send(Serializable object, String queueName) throws Exception {
		// ActiveMQConnectionFactory connectionFactory = null;
		Connection connection = null;
		Session session = null;
		Queue queue = null;
		MessageProducer producer = null;
		ObjectMessage message = null;
		// ====================================
		try {

			// 链接直接从链接池工厂进行获得
			connection = MQPooledConnectionFactory.getPooledConnectionFactory().createConnection();
			LOG.info("get activeMQ connection from MQPooledConnectionFactory");

			session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
			queue = session.createQueue(queueName);
			producer = session.createProducer(queue);
			// 链接开始，如果我们使用的是连接池，那么即使你不开始，也是没有问题的
			connection.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message = session.createObjectMessage(object);
		producer.send(message);
		message.clearBody();
		message.clearProperties();
		// 通过打印会话的内存地址和链接的客户端编号就可以知道我们使用的是不是同一个会话和链接
		// 无论使用的自己的工厂还是连接池的，都要将会话关闭
		// 如果不关闭，在使用连接池的时可以看到效果，发送两次时只能发送一次，造成堵塞
		session.close();
		// 使用自己的工厂和连接池的区别是，运行后自己工厂链接调用关闭程序结束
		// 而调用连接池链接进行关闭实际上没有关闭，因为连接池要维护这个链接
		connection.close();
		message = null;
	}

	// 接收消息
	@SuppressWarnings("static-access")
	public Object receive(String queueName) throws Exception {
		Connection connection = null;
		Session session = null;
		Queue queue = null;
		MessageConsumer producer = null;
		ObjectMessage message = null;
		try {

			connection = MQPooledConnectionFactory.getPooledConnectionFactory().createConnection();
			LOG.info("ExecutorManager connected ActiveMQ.");

			session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
			queue = session.createQueue(queueName);
			producer = session.createConsumer(queue);
			connection.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message = (ObjectMessage) producer.receive();
		if (message == null) {
			LOG.info("队列" + queueName + "中没有消息可以接收！");
			return null;
		}
//		message.clearBody();
//		message.clearProperties();
		session.close();
		connection.close();
//		message = null;
		return message.getObject();
	}
}