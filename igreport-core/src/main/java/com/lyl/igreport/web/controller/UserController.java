package com.lyl.igreport.web.controller;

import com.alibaba.fastjson.JSONObject;
import com.lyl.igreport.xxljob.controller.annotation.PermissionLimit;
import com.lyl.igreport.xxljob.core.model.XxlJobGroup;
import com.lyl.igreport.xxljob.core.model.XxlJobUser;
import com.lyl.igreport.xxljob.core.util.I18nUtil;
import com.lyl.igreport.dao.mysql.XxlJobGroupDao;
import com.lyl.igreport.dao.mysql.IgReportUserDao;
import com.lyl.igreport.xxljob.service.LoginService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xuxueli 2019-05-04 16:39:50
 */
@Controller
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private IgReportUserDao igReportUserDao;
    @Resource
    private XxlJobGroupDao xxlJobGroupDao;

    @RequestMapping
    @PermissionLimit(adminuser = true)
    public String index(Model model) {

        // 执行器列表
        List<XxlJobGroup> groupList = xxlJobGroupDao.findAll();
        model.addAttribute("groupList", groupList);
        return "user/user.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public Map<String, Object> pageList(@RequestBody JSONObject jobQuery) {


        int length = Integer.parseInt(jobQuery.getInteger("pageSize").toString());
        int start = (Integer.parseInt(jobQuery.getInteger("pageIndex").toString()) - 1) * length;
        String username = jobQuery.getString("userName");

        // page list
        List<XxlJobUser> list = igReportUserDao.pageList(start, length, username, -1);
        int list_count = igReportUserDao.pageListCount(start, length, username, -1);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", list_count);		// 总记录数
        maps.put("recordsFiltered", list_count);	// 过滤后的总记录数
        maps.put("data", list);  					// 分页列表
        return maps;
    }

    @RequestMapping("/add")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> add(@RequestBody XxlJobUser xxlJobUser) {

        //默认是普通用户 默认执行器
        xxlJobUser.setPermission("1");
        xxlJobUser.setRole(0);

        // valid username
        if (!StringUtils.hasText(xxlJobUser.getUsername())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_username") );
        }
        xxlJobUser.setUsername(xxlJobUser.getUsername().trim());
        if (!(xxlJobUser.getUsername().length()>=4 && xxlJobUser.getUsername().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // valid password
        if (!StringUtils.hasText(xxlJobUser.getPassword())) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_please_input")+I18nUtil.getString("user_password") );
        }
        xxlJobUser.setPassword(xxlJobUser.getPassword().trim());
        if (!(xxlJobUser.getPassword().length()>=4 && xxlJobUser.getPassword().length()<=20)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("system_lengh_limit")+"[4-20]" );
        }
        // md5 password
        xxlJobUser.setPassword(DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().getBytes()));

        // check repeat
        XxlJobUser existUser = igReportUserDao.loadByUserName(xxlJobUser.getUsername());
        if (existUser != null) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, I18nUtil.getString("user_username_repeat") );
        }

        // write
        igReportUserDao.save(xxlJobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/update")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> update(@RequestBody JSONObject jsonObject) {
        String updateUser = jsonObject.getString("updateUser").trim();
        String currentUser = jsonObject.getString("currentUser").trim();
        String password = jsonObject.getString("password").trim();
        if(updateUser.equals(currentUser)){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }

        password=DigestUtils.md5DigestAsHex(password.getBytes());

        XxlJobUser xxlJobUser = new XxlJobUser();
        xxlJobUser.setPassword(password);
        xxlJobUser.setUsername(updateUser);
        xxlJobUser.setPermission("1");
        xxlJobUser.setRole(0);
        // write
        igReportUserDao.update(xxlJobUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/remove")
    @ResponseBody
    @PermissionLimit(adminuser = true)
    public ReturnT<String> remove(@RequestBody JSONObject jsonObject) {

        String deleteUser = jsonObject.getString("deleteUser").trim();
        String currentUser = jsonObject.getString("currentUser").trim();
        if(deleteUser.equals(currentUser)){
            return new ReturnT<String>(ReturnT.FAIL.getCode(), I18nUtil.getString("user_update_loginuser_limit"));
        }
        igReportUserDao.delete(deleteUser);
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/updatePwd")
    @ResponseBody
    public ReturnT<String> updatePwd(@RequestBody XxlJobUser xxlJobUser){
        // md5 password
        String md5Password = DigestUtils.md5DigestAsHex(xxlJobUser.getPassword().trim().getBytes());
        xxlJobUser.setPermission("1");
        xxlJobUser.setRole(0);
        // do write
        xxlJobUser.setPassword(md5Password);
        igReportUserDao.update(xxlJobUser);

        return ReturnT.SUCCESS;
    }

}
