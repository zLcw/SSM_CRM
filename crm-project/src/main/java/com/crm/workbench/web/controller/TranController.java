package com.crm.workbench.web.controller;

import com.crm.commons.contants.Contants;
import com.crm.commons.domain.ReturnObject;
import com.crm.settings.domain.DicValue;
import com.crm.settings.domain.User;
import com.crm.settings.service.DicValueService;
import com.crm.settings.service.UserService;
import com.crm.workbench.domain.Tran;
import com.crm.workbench.domain.TranHistory;
import com.crm.workbench.domain.TranRemark;
import com.crm.workbench.service.CustomerService;
import com.crm.workbench.service.TranHistoryService;
import com.crm.workbench.service.TranRemarkService;
import com.crm.workbench.service.TranService;
import com.crm.workbench.vo.FunnelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Controller
public class TranController {

    @Autowired
    private DicValueService dicValueService;
    @Autowired
    private TranService tranService;
    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private TranRemarkService tranRemarkService;
    @Autowired
    private TranHistoryService tranHistoryService;

    @RequestMapping("/workbench/transaction/index.do")
    public String index(HttpServletRequest request){
        // 调用dicValueService查询交易类型
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_STAGE);
        List<DicValue> tyList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_TRANSACTION_TYPE);
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_SOURCE);
        request.setAttribute("stageList",stageList);
        request.setAttribute("tyList",tyList);
        request.setAttribute("sourceList",sourceList);
        // 查询出所有的交易
        List<Tran> tranList = tranService.queryAllTran();
        request.setAttribute("tranList",tranList);
        return "workbench/transaction/index";
    }

    @RequestMapping("/workbench/transaction/saveTran.do")
    public String saveTran(HttpServletRequest request){
        // 调用service层方法查询所有用户、交易阶段、类型、来源
        List<User> userList = userService.queryAllUsers();
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_STAGE);
        List<DicValue> tyList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_TRANSACTION_TYPE);
        List<DicValue> sourceList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_SOURCE);

        request.setAttribute("userList",userList);
        request.setAttribute("stageList",stageList);
        request.setAttribute("tyList",tyList);
        request.setAttribute("sourceList",sourceList);

        return "workbench/transaction/save";
    }

    @RequestMapping("/workbench/transaction/getPossibleByStage.do")
    public @ResponseBody Object getPossibleByStage(String stageValue){
        // 解析properties文件，获取可能性
        ResourceBundle bundle = ResourceBundle.getBundle("possible");
        String possible = bundle.getString(stageValue);
        // 返回响应信息
        return possible;
    }

    @RequestMapping("/workbench/transaction/queryCustomerNameByName.do")
    public @ResponseBody Object queryAllCustomerNameByName(String customerName){
        List<String> nameList = customerService.queryAllCustomerName(customerName);
        return nameList;
    }

    @RequestMapping("/workbench/traansaction/saveCreateTran.do")
    public @ResponseBody Object saveCreateTran(@RequestParam Map<String,Object> map, HttpSession session){
        // 封装参数
        map.put(Contants.SESSION_USER,session.getAttribute(Contants.SESSION_USER));
        ReturnObject returnObject = new ReturnObject();
        // 调用service层方法保存创建的交易
        try {
            tranService.saveCreateTran(map);
            // 不报异常则成功
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_SUCCESS);
        } catch (Exception e) {
            // 报错则失败
            returnObject.setCode(Contants.RETURN_OBJECT_CODE_FALSE);
            returnObject.setMessage("系统忙,请稍后重试...");
            e.printStackTrace();
        }
        return returnObject;
    }

    @RequestMapping("/workbench/transaction/detailTran.do")
    public String detailTran(String id,HttpServletRequest request){
        // 调用service层方法查询数据
        Tran tran = tranService.queryTranForDetailById(id);
        List<TranRemark> remarkList = tranRemarkService.queryTranRemarkForDetailByTranId(id);
        List<TranHistory> historyList = tranHistoryService.queryTranHistoryForDetailByTranId(id);
        // 根据交易所处阶段的名称查询可能性
        ResourceBundle bundle = ResourceBundle.getBundle("possible");
        String possible = bundle.getString(tran.getStage());
        tran.setPossible(possible);

        // 调用service层方法查询所有阶段
        List<DicValue> stageList = dicValueService.queryDicValueByTypeCode(Contants.DIC_VALUE_STAGE);

        // 保存到request中
        request.setAttribute("tran",tran);
        request.setAttribute("remarkList",remarkList);
        request.setAttribute("historyList",historyList);
        request.setAttribute("stageList",stageList);
        // 请求转发
        return "workbench/transaction/detail";
    }

    @RequestMapping("/workbench/chart/transaction/queryCountOfTranGroupByStage.do")
    public @ResponseBody Object queryCountOfTranGroupByStage(){
        // 调用service层查询数据
        List<FunnelVO> funnelVOList = tranService.queryCountOfTranGroupByStage();
        // 根据查询结果生成相应信息
        return funnelVOList;
    }
}
