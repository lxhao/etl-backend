package cn.jdworks.etl.backend.biz;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import com.mq.service.MQ_Service;

import cn.jdworks.etl.protocol.ExecutorHeartbeat;
import cn.jdworks.etl.protocol.TaskAssigment;

public class ExecutorManager {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExecutorManager.class);
	private String receiveDestination;
	private String sendDestination;

	private List<ExecutorHeartbeat> heartbeatsList = Collections.synchronizedList(new LinkedList<ExecutorHeartbeat>());
	// 同步链表用来保存Executor的心跳包

	public abstract class MergeSort {
	    
	    public static void sort(int[] numbers){
	        sort(numbers, 0, numbers.length);
	    }

	    public static void sort(int[] numbers,int pos,int end){
	        if ((end - pos) > 1) {
	            int offset = (end + pos) / 2;
	            sort(numbers, pos, offset);
	            sort(numbers, offset, end);
	            merge(numbers, pos, offset, end);
	        }
	    }
	    
	    public static void merge(int[] numbers,int pos,int offset,int end){
	        int[] array1 = new int[offset - pos];
	        int[] array2 = new int[end - offset];
	        System.arraycopy(numbers, pos, array1, 0, array1.length);
	        System.arraycopy(numbers, offset, array2, 0, array2.length);
	        for (int i = pos,j=0,k=0; i < end ; i++) {
	            if (j == array1.length) {
	                System.arraycopy(array2, k, numbers, i, array2.length - k);
	                break;
	            }
	            if (k == array2.length) {
	                System.arraycopy(array1, j, numbers, i, array1.length - j);
	                break;
	            }
	            if (array1[j] <= array2[k]) {
	                numbers[i] = array1[j++];
	            } else {
	                numbers[i] = array2[k++];
	            }
	        }
	    }
	    
	}
	
	public void send(TaskAssigment taskAssigment) throws Exception { // 发送任务

	}

	public void receive() throws Exception { // 接收心跳包
		MQ_Service mq_Service = MQ_Service.getInstance();
		LOG.info("receiving tasks...");
		ExecutorHeartbeat heartbeat = (ExecutorHeartbeat) mq_Service.receive(this.getReceiveDestination());
		heartbeat.printInfo();
		int index = heartbeatsList.indexOf(heartbeat);
		if (index == -1) {
			heartbeatsList.add(heartbeat);
		} else {
			heartbeatsList.set(index, heartbeat);
		}
	}

	public void start() {
		Timer timer = new Timer();
		long intevalPeriod = 1 * 1000;// 从当前时间开始，每间隔1秒接收次任务
		LOG.info("start to receive heartbeat package");
		timer.schedule(new Task(), 0, intevalPeriod);// 开始接收心跳包
	}

	class Task extends java.util.TimerTask {
		@Override
		public void run() {
			try {
				receive();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static class LazyHolder {
		private static final ExecutorManager executorManager = new ExecutorManager();
	}

	private ExecutorManager() {

	}

	public static final ExecutorManager getInstance() {
		return LazyHolder.executorManager;
	}// 单例

	public String getReceiveDestination() {
		return receiveDestination;
	}

	public void setReceiveDestination(String receiveDestination) {
		this.receiveDestination = receiveDestination;
	}

	public String getSendDestination() {
		return sendDestination;
	}

	public void setSendDestination(String sendDestination) {
		this.sendDestination = sendDestination;
	}

	public static void main(String args[]) {
		ExecutorManager executorManager = ExecutorManager.getInstance();
		executorManager.setReceiveDestination("ETL.HEARTBEAT");
		executorManager.start();
	}

}