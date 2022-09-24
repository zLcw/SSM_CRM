package com.crm.workbench.service.impl;

import com.crm.commons.contants.Contants;
import com.crm.commons.util.DateUtil;
import com.crm.commons.util.UUIDUtils;
import com.crm.settings.domain.User;
import com.crm.workbench.domain.*;
import com.crm.workbench.mapper.*;
import com.crm.workbench.service.ClueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class ClueServiceImpl implements ClueService {

    @Autowired
    private ClueMapper clueMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private ContactsMapper contactsMapper;
    @Autowired
    private ClueRemarkMapper clueRemarkMapper;
    @Autowired
    private CustomerRemarkMapper customerRemarkMapper;
    @Autowired
    private ContactsRemarkMapper contactsRemarkMapper;
    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;
    @Autowired
    private ContactsActivityRelationMapper carMapper;
    @Autowired
    private TranMapper tranMapper;
    @Autowired
    private TranRemarkMapper tranRemarkMapper;

    @Override
    public List<Clue> queryAllClues() {
        return clueMapper.selectAllClues();
    }

    @Override
    public int saveCreateClue(Clue clue) {
        return clueMapper.insertClue(clue);
    }

    @Override
    public Clue queryClueForDetailById(String id) {
        return clueMapper.selectClueForDetailById(id);
    }

    @Override
    public void saveConvertClue(Map<String, Object> map) {
        // 获取参数
        String isCreateTran = (String) map.get("isCreateTran");
        String clueId = (String) map.get("clueId");
        User user = (User) map.get(Contants.SESSION_USER);
        // 根据id查询线索信息
        Clue clue = clueMapper.selectClueById(clueId);

        // 把线索中有关公司的信息转换到客户表中，并调用mapper层执行SQL语句
        Customer customer = new Customer();
        customer.setAddress(clue.getAddress());
        customer.setContactSummary(clue.getContactSummary());
        customer.setCreateBy(user.getId());
        customer.setCreateTime(DateUtil.formateDateTime(new Date()));
        customer.setDescription(clue.getDescription());
        customer.setId(UUIDUtils.getUUID());
        customer.setName(clue.getCompany());
        customer.setNextContactTime(clue.getNextContactTime());
        customer.setOwner(user.getId());
        customer.setPhone(clue.getPhone());
        customer.setWebsite(clue.getWebsite());
        customerMapper.insertCustomer(customer);

        // 把线索中有关个人信息转换到联系人表中
        Contacts contacts = new Contacts();
        contacts.setAddress(clue.getAddress());
        contacts.setAppellation(clue.getAppellation());
        contacts.setCreateBy(user.getId());
        contacts.setCreateTime(DateUtil.formateDateTime(new Date()));
        contacts.setContactSummary(clue.getContactSummary());
        contacts.setCustomerId(customer.getId());
        contacts.setDescription(clue.getDescription());
        contacts.setEmail(clue.getEmail());
        contacts.setFullname(clue.getFullname());
        contacts.setId(UUIDUtils.getUUID());
        contacts.setJob(clue.getJob());
        contacts.setMphone(clue.getMphone());
        contacts.setNextContactTime(clue.getNextContactTime());
        contacts.setOwner(user.getId());
        contacts.setSource(clue.getSource());
        contactsMapper.insertContans(contacts);

        // 如果该备注下有内容，把线索中的备注转换到客户表备注中
        // 如果该备注下有内容，把线索中的备注转换到联系人备注表中
        // 根据clueid查询线索下所有的备注
        List<ClueRemark> clueRemarkList = clueRemarkMapper.selectClueRemarkByClueId(clueId);
        if (clueRemarkList.size()>0&&clueRemarkList!=null){
            // 遍历clueRemarkList，疯转客户备注
            CustomerRemark cur = null;
            ContactsRemark ctr = null;
            List<CustomerRemark> curList = new ArrayList<>();
            List<ContactsRemark> ctrList = new ArrayList<>();
            for (ClueRemark cr:clueRemarkList){
                cur = new CustomerRemark();
                cur.setCreateBy(cr.getCreateBy());
                cur.setCreateTime(cr.getCreateTime());
                cur.setCustomerId(customer.getId());
                cur.setEditBy(cr.getEditBy());
                cur.setEditFlag(cr.getEditFlag());
                cur.setEditTime(cr.getEditTime());
                cur.setId(UUIDUtils.getUUID());
                cur.setNoteContent(cr.getNoteContent());
                curList.add(cur);

                ctr = new ContactsRemark();
                ctr.setCreateBy(cr.getCreateBy());
                ctr.setCreateTime(cr.getCreateTime());
                ctr.setContactsId(customer.getId());
                ctr.setEditBy(cr.getEditBy());
                ctr.setEditTime(cr.getEditTime());
                ctr.setEditFlag(cr.getEditFlag());
                ctr.setId(UUIDUtils.getUUID());
                ctr.setNoteContent(cr.getNoteContent());
                ctrList.add(ctr);
            }
            customerRemarkMapper.insertCustomerRemarkByList(curList);
            contactsRemarkMapper.insertContactsRemarkByList(ctrList);
        }

        // 根据clueId查询该线索和市场活动的关联关系
        List<ClueActivityRelation> carList = clueActivityRelationMapper.selectClueActivityRelationByClueId(clueId);
        // 把该线索和市场活动的关联关系转换到联系人和市场活动的关联关系表中
        if (carList!=null&&carList.size()>0){
            ContactsActivityRelation c = null;
            List<ContactsActivityRelation> cList = new ArrayList<>();
            for (ClueActivityRelation car:carList){
                c = new ContactsActivityRelation();
                c.setActivityId(car.getActivityId());
                c.setContactsId(contacts.getId());
                c.setId(UUIDUtils.getUUID());
                cList.add(c);
            }
            carMapper.insertContactsActivityRelationByList(cList);
        }
        // 如果需要交易，则往交易表中添加一条记录
        // 把该线索下的备注，转换到交易表的备注中一份
        if ("true".equals(isCreateTran)){
            Tran tran = new Tran();
            tran.setActivityId((String) map.get("activityId"));
            tran.setContactsId(contacts.getId());
            tran.setCreateBy(user.getId());
            tran.setCreateTime(DateUtil.formateDateTime(new Date()));
            tran.setCustomerId(customer.getId());
            tran.setExpectedDate((String) map.get("expectedDate"));
            tran.setId(UUIDUtils.getUUID());
            tran.setMoney((String) map.get("money"));
            tran.setName((String)map.get("name"));
            tran.setOwner(user.getId());
            tran.setStage((String)map.get("stage"));
            tranMapper.insertTran(tran);

            // 把该线索下的备注，转换到交易表的备注中一份
            if (clueRemarkList.size()>0&&clueRemarkList!=null){
                TranRemark tr = null;
                List<TranRemark> trList = new ArrayList<>();
                for (ClueRemark c:clueRemarkList){
                    tr = new TranRemark();
                    tr.setCreateBy(c.getCreateBy());
                    tr.setCreateTime(c.getCreateTime());
                    tr.setEditBy(c.getEditBy());
                    tr.setEditTime(c.getEditTime());
                    tr.setEditFlag(c.getEditFlag());
                    tr.setId(UUIDUtils.getUUID());
                    tr.setNoteContent(c.getNoteContent());
                    tr.setTranId(tran.getId());
                    trList.add(tr);
                }
                tranRemarkMapper.insertTranRemarkList(trList);
            }
        }
        // 根据clueId删除该线索下的所有备注
        clueRemarkMapper.deleteClueRemarkByClueId(clueId);
        // 删除线索和市场活动的关联关系
        clueActivityRelationMapper.deleteClueActivityRelationByClueId(clueId);
        // 删除该线索
        clueMapper.deleteClueById(clueId);
    }


}
