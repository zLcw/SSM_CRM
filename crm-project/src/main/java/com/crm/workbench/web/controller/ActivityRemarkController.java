package com.crm.workbench.web.controller;

import com.crm.commons.contants.Contants;
import com.crm.commons.domain.ReturnObject;
import com.crm.commons.util.DateUtil;
import com.crm.commons.util.UUIDUtils;
import com.crm.settings.domain.User;
import com.crm.workbench.domain.ActivityRemark;
import com.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * @author 33212
 */
@Controller
public class ActivityRemarkController {

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    public @ResponseBody Object saveCreateActivityRemark(ActivityRemark remark, HttpSession session){
        ReturnObject retObj = new ReturnObject();
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        // 封装参数
        remark.setId(UUIDUtils.getUUID());
        remark.setCreateTime(DateUtil.formateDateTime(new Date()));
        remark.setCreateBy(user.getId());
        remark.setEditFlag(Contants.REMARK_EDIT_NO);

        // 调用service层方法，保存创建的市场活动
        try {
            int ret = activityRemarkService.saveCreateActivityRemark(remark);
            if (ret > 0){
                retObj.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                retObj.setRetDate(remark);
            }else {
                retObj.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                retObj.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            retObj.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            retObj.setMessage("系统忙，请稍后重试...");
            e.printStackTrace();
        }

        return retObj;
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    public @ResponseBody Object deleteActivityRemarkById(String id){
        ReturnObject retObj = new ReturnObject();
        // 调用service层，删除备注
        try {
            int ret = activityRemarkService.deleteActivityRemarkById(id);
            if (ret>0){
                retObj.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                retObj.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                retObj.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            retObj.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            retObj.setMessage("系统忙，请稍后重试...");
        }

        return retObj;
    }

    @RequestMapping("/workbench/activity/saveEditActivityRemark.do")
    public @ResponseBody Object saveEditActivityRemark(ActivityRemark remark,HttpSession session){
        ReturnObject object = new ReturnObject();
        // 封装参数
        User user = (User) session.getAttribute(Contants.SESSION_USER);
        remark.setEditTime(DateUtil.formateDateTime(new Date()));
        remark.setEditBy(user.getId());
        remark.setEditFlag(Contants.REMARK_EDIT_YES);
        // 调用service
        try {
            int ret = activityRemarkService.saveEditActivityRemark(remark);
            if (ret>0){
                object.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                object.setRetDate(remark);
            }else {
                object.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                object.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            object.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            object.setMessage("系统忙，请稍后重试...");
        }
        return object;
    }
}
