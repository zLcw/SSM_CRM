package com.crm.workbench.service;

import com.crm.workbench.domain.ActivityRemark;

import java.util.List;

/**
 * @author 33212
 */
public interface ActivityRemarkService {

    /**
     * 根据市场活动id查询市场活动详细
     * @param activityId
     * @return
     */
    List<ActivityRemark> queryActivityRemarkForDrtailByActivityById(String activityId);

    /**
     * 添加市场活动备注
     * @param remark
     * @return
     */
    int saveCreateActivityRemark(ActivityRemark remark);

    /**
     * 根据id删除市场活动备注
     * @param id
     * @return
     */
    int deleteActivityRemarkById(String id);

    /**
     * 保存修改市场活动备注
     * @param remark
     * @return
     */
    int saveEditActivityRemark(ActivityRemark remark);
}
