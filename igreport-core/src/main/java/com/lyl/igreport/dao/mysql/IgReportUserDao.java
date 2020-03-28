package com.lyl.igreport.dao.mysql;

import com.lyl.igreport.xxljob.core.model.XxlJobUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by liuyanling on 2020/2/6
 */
@Mapper
public interface IgReportUserDao {

	public List<XxlJobUser> pageList(@Param("offset") int offset,
                                     @Param("pagesize") int pagesize,
                                     @Param("username") String username,
                                     @Param("role") int role);
	public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("username") String username,
                             @Param("role") int role);

	public XxlJobUser loadByUserName(@Param("username") String username);

	public List<String> getAllUserName();
	public int userCount();
	public int save(XxlJobUser xxlJobUser);

	public int update(XxlJobUser xxlJobUser);
	
	public int delete(@Param("userName") String userName);

}
