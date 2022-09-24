package com.crm.workbench.service;

import com.crm.workbench.domain.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    /**
     * 添加市场活动
     * @param activity 添加数据
     * @return
     */
    int saveCreateActivity(Activity activity);

    /**
     * 分页查询市场活动
     * @param map 查询条件
     * @return
     */
    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    /**
     * 按条件查询市场活动的总条数
     * @param map
     * @return
     */
    int queryCountOfActivityByCondition(Map<String,Object> map);

    /**
     * 根据ids批量删除市场活动
     * @param ids
     * @return
     */
    int deleteActivityByIds(String[] ids);

    /**
     * 根据id查询市场活动
     * @param id
     * @return
     */
    Activity queryActivityById(String id);

    /**
     * 保存修改市场活动
     * @param activity
     * @return
     */
    int saveEditActivity(Activity activity);

    /**
     * 查询所有的市场活动
     * @return
     */
    List<Activity> queryAllActivitys();

    /**
     * 批量保存市场活动
     * @param activityList
     * @return
     */
    int saveCreateActivityByList(List<Activity> activityList);

    /**
     * 根据id查询市场活动详情
     * @param id
     * @return
     */
    Activity queryActivityForDetailById(String id);

    /**
     * 根据clueId查询线索相关联的市场活动的明细信息
     * @param clueId
     * @return
     */
    List<Activity> queryActivityForDetailByClueId(String clueId);
    /**
     * 根据名称模糊查询市场活动，并且排除已经关联的市场活动
     * @param map
     * @return
     */
    List<Activity> queryActivityForDetailByNameAndClueId(Map<String,Object> map);

    List<Activity> queryActivityForDetailByIds(String[] ids);

    List<Activity> queryActivityForConvertByNameClueId(Map<String, Object> map);

}
