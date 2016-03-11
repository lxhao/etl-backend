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

import cn.jdworks.etl.backend.bean.TriggerTask;

@IocBean
@At("/triggertask")
@Ok("json")
@Fail("http:500")
public class TriggerTaskModule {

	@Inject
	protected Dao dao;

	@Inject

	@At("/")
	@GET
	public List<TriggerTask> getAllTasks() {
		List<TriggerTask> list = dao.query(TriggerTask.class, null);
		return list;
	}

	@At("/?")
	@GET
	public TriggerTask getTask(int Id) {
		TriggerTask t = dao.fetch(TriggerTask.class, Id);
		return t;
	}

	@At("/u")
	@POST
	public boolean updateTask(int Id, @Param("..") TriggerTask task) {
		if (dao.fetch(TriggerTask.class, task.getId()) == null) {
			return false;// 根据ID查找任务是否存在数据库中再确定是否更新
		}
		dao.updateIgnoreNull(task);
		return true;
	}

	@At("/i")
	@POST // 插入
	public boolean insertTask(int Id, @Param("..") TriggerTask task) {
		dao.insert(task);
		if (dao.fetch(TriggerTask.class, task.getId()) == null) {
			return false;
		} else {
			return true;
		}
	}

	@At("/?")
	@DELETE
	public boolean deleteTask(int Id) {
		if (dao.fetch(TriggerTask.class, Id) == null) {
			return false;
		}
		dao.delete(TriggerTask.class, Id);
		return true;
	}

	// default to @At("/TriggerTask/count")
	@At
	public int count() {
		return dao.count(TriggerTask.class);
	}
}
