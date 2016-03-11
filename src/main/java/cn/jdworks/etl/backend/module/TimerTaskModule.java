package cn.jdworks.etl.backend.module;

import java.util.List;

import org.nutz.dao.Dao;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.DELETE;
import org.nutz.mvc.annotation.Fail;
import org.nutz.mvc.annotation.GET;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.POST;
import org.nutz.mvc.annotation.Param;

import cn.jdworks.etl.backend.bean.TimerTask;
import cn.jdworks.etl.backend.biz.TimerScheduler;

@IocBean
@At("/timertask")
@Ok("json")
@Fail("http:500")
public class TimerTaskModule {

	@Inject
	protected Dao dao;


	@At("/")
	@GET
	public List<TimerTask> getAllTasks() {
		List<TimerTask> list = dao.query(TimerTask.class, null);
		return list;
	}

	@At("/?")
	@GET
	public TimerTask getTask(int Id) {
		TimerTask t = dao.fetch(TimerTask.class, Id);
		return t;
	}

	@At("/u")
	@POST
	public boolean updateTask(int Id, @Param("..") TimerTask task) {
		if (dao.fetch(TimerTask.class, task.getId()) == null) {
			return false;// 根据ID查找任务是否存在数据库中再确定是否更新
		}
		dao.updateIgnoreNull(task);
		return true;
	}

	@At("/i")
	@POST
	public boolean insertTask(int Id, @Param("..") TimerTask task) {
		dao.insert(task);
		if (dao.fetch(TimerTask.class, task.getId()) == null) {
			return false;
		} else {
			return true;
		}
	}

	@At("/?")
	@DELETE
	public boolean deleteTask(int Id) {
		if (dao.fetch(TimerTask.class, Id) == null) {
			return false;
		}
		dao.delete(TimerTask.class, Id);
		return true;
	}

	// default to @At("/timertask/count")
	@At
	public int count() {
		TimerScheduler t = new TimerScheduler();
		return dao.count(TimerTask.class);
	}
}
