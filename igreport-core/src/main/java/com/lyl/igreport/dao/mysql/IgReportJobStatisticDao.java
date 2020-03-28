package com.lyl.igreport.dao.mysql;

import com.lyl.igreport.xxljob.core.model.XxlJobLogReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by liuyanling on 2020/2/6
 */
@Mapper
public interface IgReportJobStatisticDao {

	public int save(XxlJobLogReport xxlJobLogReport);

	public int saveOnDuplicateKey(List<XxlJobLogReport> xxlJobLogReport);

	public int update(XxlJobLogReport xxlJobLogReport);

	public List<XxlJobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
                                                @Param("triggerDayTo") Date triggerDayTo);

	public XxlJobLogReport queryLogReportTotal();

}
