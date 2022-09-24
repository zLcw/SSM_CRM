package com.crm.workbench.service.impl;

import com.crm.commons.contants.Contants;
import com.crm.commons.util.DateUtil;
import com.crm.commons.util.UUIDUtils;
import com.crm.settings.domain.User;
import com.crm.workbench.domain.Customer;
import com.crm.workbench.domain.Tran;
import com.crm.workbench.domain.TranHistory;
import com.crm.workbench.mapper.CustomerMapper;
import com.crm.workbench.mapper.TranHistoryMapper;
import com.crm.workbench.mapper.TranMapper;
import com.crm.workbench.service.TranService;
import com.crm.workbench.vo.FunnelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class TranServiceImpl implements TranService {

    @Autowired
    private TranMapper tranMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public List<Tran> queryAllTran() {
        return tranMapper.selectAllTran();
    }

    @Override
    public void saveCreateTran(Map<String, Object> map) {
        String customerName = (String) map.get("customerName");
        User user = (User) map.get(Contants.SESSION_USER);
        // 根据name精确查询客户
        Customer customer = customerMapper.selectCustomeByName(customerName);
        // 如果客户不存在，则新建客户
        if (customer==null){
            customer = new Customer();
            customer.setOwner(user.getId());
            customer.setName(customerName);
            customer.setId(UUIDUtils.getUUID());
            customer.setCreateTime(DateUtil.formateDateTime(new Date()));
            customer.setCreateBy(user.getId());
            customerMapper.insertCustomer(customer);
        }
        // 保存创建交易
        Tran tran = new Tran();
        tran.setStage((String) map.get("stage"));
        tran.setOwner((String) map.get("owner"));
        tran.setNextContactTime((String) map.get("nextContactTime"));
        tran.setName((String) map.get("name"));
        tran.setMoney((String) map.get("money"));
        tran.setExpectedDate((String) map.get("expectedDate"));
        tran.setCreateTime(DateUtil.formateDateTime(new Date()));
        tran.setCustomerId(customer.getId());
        tran.setId(UUIDUtils.getUUID());
        tran.setCreateBy(user.getId());
        tran.setContactSummary((String) map.get("contactSummary"));
        tran.setContactsId((String) map.get("contactsId"));
        tran.setActivityId((String)map.get("activityId"));
        tran.setDescription((String) map.get("description"));
        tran.setSource((String) map.get("source"));
        tran.setType((String) map.get("type"));
        tranMapper.insertTran(tran);

        // 保存交易历史
        TranHistory tranHistory = new TranHistory();
        tranHistory.setCreateBy(user.getId());
        tranHistory.setCreateTime(DateUtil.formateDateTime(new Date()));
        tranHistory.setExpectedDate(tran.getExpectedDate());
        tranHistory.setId(UUIDUtils.getUUID());
        tranHistory.setMoney(tran.getMoney());
        tranHistory.setStage(tran.getStage());
        tranHistory.setTranId(tran.getId());
        tranHistoryMapper.insertTranHistory(tranHistory);
    }

    @Override
    public Tran queryTranForDetailById(String id) {
        return tranMapper.selectTranForDetailById(id);
    }

    @Override
    public List<FunnelVO> queryCountOfTranGroupByStage() {
        return tranMapper.selectCountOfTranGroupByStage();
    }
}
