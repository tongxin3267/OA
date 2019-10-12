package com.ldz.biz.job;

import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldz.biz.model.BizExceptionConfig;
import com.ldz.biz.service.BizExceptionService;
import com.ldz.util.bean.ApiResponse;
import com.ldz.util.redis.RedisTemplateUtil;

/*
 * 主动推送数据至合作方接口
 */
// 在成功执行了job类的execute方法后,更新JobDetail中JobDataMap的数据
@PersistJobDataAfterExecution
// 等待上一次任务执行完成，才会继续执行新的任务
@DisallowConcurrentExecution
public class ExceptionDataJob implements Job {
	
	Logger errorLog = LoggerFactory.getLogger("error_info");
	protected Logger accessLog = LoggerFactory.getLogger("access_info");
	@Autowired
	private RedisTemplateUtil redisUtil;
	@Autowired
	private BizExceptionService exceptionService;
	//订单最大等待时长
	int maxMinute = 20;
	
	ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			ApiResponse<List<BizExceptionConfig>> exps = exceptionService.getAllConfig();
			if (exps.getCode() == ApiResponse.SUCCESS && exps.getResult().size() > 0){
				List<BizExceptionConfig> configs = exps.getResult();
				for (int i=0; i<configs.size(); i++){
					BizExceptionConfig config = configs.get(i);
					if (config.getDays() != null){
						exceptionService.expJobSave(config);
					}
				}
			}
		} catch (Exception e) {
			errorLog.error("异常数据统计异常", e);
			JobExecutionException e2 = new JobExecutionException(e);
			// 当任务执行失败时，立即停止所有相关这个任务的触发器
			e2.setRefireImmediately(true);
		}
	}

}