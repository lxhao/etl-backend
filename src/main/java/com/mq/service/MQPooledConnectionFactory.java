package com.mq.service;  
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;


/** 
 * 链接工厂管理类 
 * 自己工厂定义成了单例模式，连接池是静态块进行初始化，具体实现自己看着办 
 */  
public class MQPooledConnectionFactory {  
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(MQPooledConnectionFactory.class);

    private static ActiveMQConnectionFactory connectionFactory;  
    public static ActiveMQConnectionFactory getMyActiveMQConnectionFactory() {  
        if (null == connectionFactory) {  
            connectionFactory = new ActiveMQConnectionFactory("system","manage", "tcp://127.0.0.1:61616");  
			connectionFactory.setTrustAllPackages(true);
			LOG.info("connectionFactory was created succeed");
        }  
        return connectionFactory;  
    }  
    private static PooledConnectionFactory pooledConnectionFactory;  
    static {  
        try {  
            // 需要创建一个链接工厂然后设置到连接池中  
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();  
            activeMQConnectionFactory.setUserName("system");  
            activeMQConnectionFactory.setPassword("manage");  
            activeMQConnectionFactory.setBrokerURL("tcp://127.0.0.1:61616");  
			activeMQConnectionFactory.setTrustAllPackages(true);
            // 如果将消息工厂作为属性设置则会有类型不匹配的错误，虽然Spring配置文件中是这么配置的，这里必须在初始化的时候设置进去  
            pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    /** 
     * 获得链接池工厂 
     */  
    public static PooledConnectionFactory getPooledConnectionFactory() {  
        return pooledConnectionFactory;  
    }  
    /** 
     * 对象回收销毁时停止链接 
     */  
    @Override  
    protected void finalize() throws Throwable {  
        pooledConnectionFactory.stop();  
        super.finalize();  
    }  
}  