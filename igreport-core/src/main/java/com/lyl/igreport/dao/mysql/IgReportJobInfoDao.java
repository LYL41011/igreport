package com.lyl.igreport.dao.mysql;

import com.lyl.igreport.xxljob.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by liuyanling on 2020/2/6
 */
@Mapper
public interface IgReportJobInfoDao {

	public List<XxlJobInfo> pageList(@Param("offset") int offset,
                                     @Param("pagesize") int pagesize,
                                     @Param("jobGroup") int jobGroup,
                                     @Param("triggerStatus") int triggerStatus,
                                     @Param("jobDesc") String jobDesc,
                                     @Param("executorHandler") String executorHandler,
                                     @Param("author") String author);
	public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("jobGroup") int jobGroup,
                             @Param("triggerStatus") int triggerStatus,
                             @Param("jobDesc") String jobDesc,
                             @Param("executorHandler") String executorHandler,
                             @Param("author") String author);

	public int save(XxlJobInfo info);

	public XxlJobInfo loadById(@Param("id") int id);

	public int update(XxlJobInfo xxlJobInfo);

	public int delete(@Param("id") long id);


	public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize);

	public int scheduleUpdate(XxlJobInfo xxlJobInfo);


	/**
	 * 统计最近7天任务数
	 * @return
	 */
	public List<HashMap> sevenDayTaskCount();

	public int findAllCount();
	public int findRunningCount();

}
