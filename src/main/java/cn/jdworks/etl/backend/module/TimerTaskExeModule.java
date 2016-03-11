package cn.jdworks.etl.backend.module;

import java.util.Date;
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
import cn.jdworks.etl.backend.bean.TimerTaskExe;

@IocBean
@At("/timertaskexe")
@Ok("json")
@Fail("http:500")
public class TimerTaskExeModule {

	@Inject
	protected Dao dao;

	@At("/?")
	@GET // 查看指定Id的数据项
	public TimerTaskExe getTaskExe(int Id) {
		TimerTaskExe t = dao.fetch(TimerTaskExe.class, Id);
		return t;
	}

	@At("/")
	@GET // 查看整表数据
	public List<TimerTaskExe> getTaskExes() {
		List<TimerTaskExe> list = dao.query(TimerTaskExe.class, null);
		return list;
	}

	@At // 计数
	public int count() {
		return dao.count(TimerTaskExe.class);
	}

	@At("/?")
	@DELETE // 删除
	public boolean deleteTaskExe(int Id) {
		if (dao.fetch(TimerTaskExe.class, Id) == null) {
			return false;
		}
		dao.delete(TimerTaskExe.class, Id);
		return true;
	}

	@At("/u")
	@POST // 更新
	public boolean updateTaskExe(int Id, @Param("..") TimerTaskExe taskExe) {

		if (dao.fetch(TimerTask.class, taskExe.getId()) == null) {
			return false;// 如果要更新的任务Id不存在，不進行更新
		}

		if (dao.fetch(TimerTaskExe.class, taskExe.getId_TimerTask()) == null) {
			return false;// 根据ID查找任务是否存在数据库中再确定是否更新
		}

		dao.updateIgnoreNull(taskExe);
		return true;
	}

	@At("/i")
	@POST // 插入
	public boolean insertTaskExe(int Id, @Param("..") TimerTaskExe taskExe) {

		if (dao.fetch(TimerTask.class, taskExe.getId_TimerTask()) == null) {
			return false;// 如果不存在这个任务ID,不插入数据
		} else {
			taskExe.setExeTime((new Date()));
			dao.insert(taskExe);
			return true;
		}
	}
}
