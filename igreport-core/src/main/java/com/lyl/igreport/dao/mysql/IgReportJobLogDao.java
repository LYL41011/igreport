package com.lyl.igreport.dao.mysql;

import com.lyl.igreport.xxljob.core.model.XxlJobLog;
import com.lyl.igreport.xxljob.core.model.XxlJobLogReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by liuyanling on 2020/2/6
 */
@Mapper
public interface IgReportJobLogDao {

	// exist jobId not use jobGroup, not exist use jobGroup
	public List<XxlJobLog> pageList(@Param("offset") int offset,
                                    @Param("pagesize") int pagesize,
                                    @Param("jobGroup") int jobGroup,
                                    @Param("jobId") int jobId,
                                    @Param("triggerTimeStart") String triggerTimeStart,
                                    @Param("triggerTimeEnd") String triggerTimeEnd,
                                    @Param("logStatus") int logStatus,
									@Param("authorName") String authorName);
	public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("jobGroup") int jobGroup,
                             @Param("jobId") int jobId,
                             @Param("triggerTimeStart") String triggerTimeStart,
                             @Param("triggerTimeEnd") String triggerTimeEnd,
                             @Param("logStatus") int logStatus,
							 @Param("authorName") String authorName);

	public XxlJobLog load(@Param("id") long id);

	public long save(XxlJobLog xxlJobLog);

	public int updateTriggerInfo(XxlJobLog xxlJobLog);

	public int updateHandleInfo(XxlJobLog xxlJobLog);

	/**
	 * 任务执行错误日志
	 * @param xxlJobLog
	 * @return
	 */
	public int updateHandleMsg(XxlJobLog xxlJobLog);

	public int delete(@Param("jobId") int jobId);

	public List<XxlJobLogReport> findSevenDayLogReport();

	public List<Long> findClearLogIds(@Param("jobGroup") int jobGroup,
                                      @Param("jobId") int jobId,
                                      @Param("clearBeforeTime") Date clearBeforeTime,
                                      @Param("clearBeforeNum") int clearBeforeNum,
                                      @Param("pagesize") int pagesize);
	public int clearLog(@Param("logIds") List<Long> logIds);

	public List<Long> findFailJobLogIds(@Param("pagesize") int pagesize);

	public int updateAlarmStatus(@Param("logId") long logId,
                                 @Param("oldAlarmStatus") int oldAlarmStatus,
                                 @Param("newAlarmStatus") int newAlarmStatus);

	/**
	 * 最近一周任务耗时top10
	 * @return
	 */
	public String queryConsumeTimeTop10();
	/**
	 * 最近一周任务耗时分布
	 * @return
	 */
	public String queryConsumeTimeDistribute();

}
