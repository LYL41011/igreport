package com.lyl.igreport.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.util.DateTimeUtil;
import com.lyl.igreport.xxljob.controller.annotation.PermissionLimit;
import com.lyl.igreport.xxljob.service.LoginService;
import com.lyl.igreport.service.JobService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.util.DateUtil;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by liuyanling on 2020/2/1
 */
@RestController
public class IndexController {

	@Resource
	private JobService jobService;
	@Resource
	private LoginService loginService;


	@PostMapping(value = "/api/chartJobSum")
	public ReturnT<Map<String, Object>> chartJobSum() {

		ReturnT<Map<String, Object>> dashboardMap = jobService.dashboardInfo();

		return dashboardMap;
	}

    @PostMapping(value = "/api/chartJobInfo")
	public ReturnT<Map<String, Object>> chartJobInfo() {
		Date zero = new Date();
		try {
			zero = DateTimeUtil.formatDateString(DateTimeUtil.getDayZero(new Date()),"yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date startDate = DateUtil.addDays(zero,-7);
		Date endDate = DateUtil.addDays(zero,1);

		ReturnT<Map<String, Object>> chartInfo = jobService.chartInfo(startDate, endDate);
		return chartInfo;
    }
	
	@RequestMapping("/toLogin")
	@PermissionLimit(limit=false) //PermissionInterceptor类默认是会拦截的 要走登录接口 如果这个设置为false就不拦截
	public String toLogin(HttpServletRequest request, HttpServletResponse response) {
		//TODO 必须login 不然无法访问其他接口
		if (loginService.ifLogin(request, response) != null) {
			return "redirect:/";
		}
		return "login";
	}
	
	@RequestMapping(value="/api/login", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject user){
		//boolean ifRem = (ifRemember!=null && ifRemember.trim().length()>0 && "on".equals(ifRemember))?true:false;
		boolean ifRem = true;
		return loginService.login(request, response, user.get("username").toString(), user.get("password").toString(), ifRem);
	}
	
	@RequestMapping(value="logout", method=RequestMethod.POST)
	@ResponseBody
	@PermissionLimit(limit=false)
	public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response){
		return loginService.logout(request, response);
	}


	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
}
