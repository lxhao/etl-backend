package cn.jdworks.etl.backend.biz;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;

import cn.jdworks.etl.backend.bean.TimerTask;
import cn.jdworks.etl.backend.module.TimerTaskModule;
import cn.jdworks.etl.protocol.TaskAssigment;

public class TimerScheduler {
	protected TimerTaskModule timerTaskModule = new TimerTaskModule();

	public TimerScheduler() { // 构造方法，对象new出来就开始监控定时任务
		Timer timer = new Timer();
		long intevalPeriod = 1 * 1000;// 从当前时间开始，每间隔100秒查询一次数据库
		timer.scheduleAtFixedRate(new Task(), new Date(), intevalPeriod);
	}

	// 从数据库取到任务列表
	public List<TimerTask> getTasks() {
		List<TimerTask> list = timerTaskModule.getAllTasks();
		return list;
	}

	class Task extends java.util.TimerTask {
		@Override
		public void run() {
			System.out.println("before accessing database.");
			List<TimerTask> list = timerTaskModule.getAllTasks();
			System.out.println("after accessing database.");
			long time = 0;
			Iterator<TimerTask> item = list.iterator();
			while (item.hasNext()) {
				TimerTask timerTask = item.next();
				time = timerTask.getExeTime().getTime() - new Date().getTime();
				if (true) {
					try {
						ExecutorManager executorManager = ExecutorManager.getInstance();
						executorManager.send(
								new TaskAssigment(timerTask.getId(), timerTask.getScript(), timerTask.getScriptArgs()));
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		}

	}
}
