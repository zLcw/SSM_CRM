package com.crm.workbench.web.controller;

import com.crm.commons.contants.Contants;
import com.crm.commons.domain.ReturnObject;
import com.crm.commons.util.DateUtil;
import com.crm.commons.util.UUIDUtils;
import com.crm.settings.domain.DicValue;
import com.crm.settings.domain.User;
import com.crm.settings.service.DicValueService;
import com.crm.settings.service.UserService;
import com.crm.workbench.domain.Activity;
import com.crm.workbench.domain.Clue;
import com.crm.workbench.domain.ClueActivityRelation;
import com.crm.workbench.domain.ClueRemark;
import com.crm.workbench.service.ActivityService;
import com.crm.workbench.service.ClueActivityRelationService;
import com.crm.workbench.service.ClueRemarkService;
import com.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {

    @Autowired
    private UserService userService;
    @Autowired
    private DicValueService dicValueService;
    @Autowired
    private ClueService clueService;
    @Autowired
    private ClueRemarkService clueRemarkService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @RequestMapping("/workbench/clue/index.do")
    public String index(HttpServletRequest request){
        // 调用service层查询动态数据
        List<User> userList = userService.queryAllUsers();
        List<DicValue> appellationList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_APPELLATION);
        List<DicValue> clueStatesList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_CLUE_STATES);
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_SOURCE);
        List<Clue> clueList = clueService.queryAllClues();
        // 把查询到的数据保存到作用域中
        request.setAttribute("userList",userList);
        request.setAttribute("appellationList",appellationList);
        request.setAttribute("clueStatesList",clueStatesList);
        request.setAttribute("sourceList",sourceList);
        request.setAttribute("clueList",clueList);
        // 请求转发
        return "workbench/clue/index";
    }


    @RequestMapping("/workbench/clue/saveCreateClue.do")
    public @ResponseBody Object saveCreateClue(Clue clue, HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        // 封装参数
        clue.setId(UUIDUtils.getUUID());
        clue.setCreateTime(DateUtil.formateDateTime(new Date()));
        User user = (User)session.getAttribute(Contants.SESSION_USER);
        clue.setCreateBy(user.getId());
        // 调用service层，保存创建的线索
        try {
            int ret = clueService.saveCreateClue(clue);
            if (ret>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            returnObject.setMessage("系统忙，请稍后重试...");
            e.printStackTrace();
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/clueDetail.do")
    public String clueDetail(String id,HttpServletRequest request){
        // 调用service层方法查询数据
        Clue clue = clueService.queryClueForDetailById(id);
        List<ClueRemark> clueRemarkList = clueRemarkService.queryClueRemarkForDetailByClueId(id);
        List<Activity> activityList = activityService.queryActivityForDetailByClueId(id);
        // 把数据保存到作用域中
        request.setAttribute("clue",clue);
        request.setAttribute("clueRemarkList",clueRemarkList);
        request.setAttribute("activityList",activityList);
        //请求转发
        return "workbench/clue/detail";
    }

    @RequestMapping("/workbench/clue/queryActivityForDetailByNameAndClueId.do")
    public @ResponseBody Object queryActivityForDetailByNameAndClueId(String activityName,String clueId){
        // 封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        // 调用service获取查询结果
        List<Activity> activityList = activityService.queryActivityForDetailByNameAndClueId(map);
        // 根据查询结果形成响应信息
        return activityList;
    }

    @RequestMapping("/workbench/clue/saveClueActivityBund.do")
    public @ResponseBody Object saveClueActivityBund(String[] activityId,String clueId){
        // 封装参数
        ClueActivityRelation clueActivityRelation = null;
        List<ClueActivityRelation> activityRelationList = new ArrayList<>();
        for (String id:activityId){
            clueActivityRelation = new ClueActivityRelation();
            clueActivityRelation.setId(UUIDUtils.getUUID());
            clueActivityRelation.setActivityId(id);
            clueActivityRelation.setClueId(clueId);

            activityRelationList.add(clueActivityRelation);
        }

        ReturnObject returnObject = new ReturnObject();
        // 调用service层方法，批量保存线索和市场活动的关联信息
        try {
            int retBund = clueActivityRelationService.saveCreateClueActivityRelationByList(activityRelationList);
            if (retBund>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
                // 添加成功之后，调用activityService查询市场活动
                List<Activity> activityList = activityService.queryActivityForDetailByIds(activityId);
                returnObject.setRetDate(activityList);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            returnObject.setMessage("系统忙，请稍后重试...");
            e.printStackTrace();
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/saveDeleteBund.do")
    public @ResponseBody Object saveDeleteBund(ClueActivityRelation clueActivityRelation){
        ReturnObject returnObject = new ReturnObject();
        try {
            int retDel = clueActivityRelationService.delelteClueActivityRelationByClueIdActivityId(clueActivityRelation);
            if (retDel>0){
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
                returnObject.setMessage("系统忙，请稍后重试...");
            }
        } catch (Exception e) {
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            returnObject.setMessage("系统忙，请稍后重试...");
            e.printStackTrace();
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/goConvert.do")
    public String goConvert(String clueId,HttpServletRequest request){
        // 调用service层方法查询线索的明细信息
        Clue clue = clueService.queryClueForDetailById(clueId);
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_STAGE);
        request.setAttribute("clue",clue);
        request.setAttribute("stageList",stageList);
        return "workbench/clue/convert";
    }

    @RequestMapping("/workbench/clue/queryActivityForConvertByNameClueId.do")
    public @ResponseBody Object queryActivityForConvertByNameClueId(String activityName,String clueId){
        // 封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        // 调用service层，模糊查询跟线索有关联的市场活动
        List<Activity> activityList = activityService.queryActivityForConvertByNameClueId(map);
        // 根据查询结果生成相应信息
        return activityList;
    }

    @RequestMapping("/workbench/clue/saveClueConvert.do")
    public @ResponseBody Object saveClueConvert(String clueId,String money,String name,String expectedDate, String stage,
                                                String activityId,String isCreateTran,HttpSession session){
        // 封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("clueId",clueId);
        map.put("money",money);
        map.put("name",name);
        map.put("expectedDate",expectedDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("isCreateTran",isCreateTran);
        map.put(Contants.SESSION_USER,session.getAttribute(Contants.SESSION_USER));
        ReturnObject returnObject = new ReturnObject();
        try {
            // 调用service层处理业务
            clueService.saveConvertClue(map);
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            returnObject.setMessage("系统忙，请稍后重试...");
        }
        return returnObject;
    }
}
