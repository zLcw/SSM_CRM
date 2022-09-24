package com.crm.workbench.service;

import com.crm.workbench.domain.Tran;
import com.crm.workbench.vo.FunnelVO;

import java.util.List;
import java.util.Map;

public interface TranService {

    List<Tran> queryAllTran();

    void saveCreateTran(Map<String,Object> map);

    Tran queryTranForDetailById(String id);

    List<FunnelVO> queryCountOfTranGroupByStage();
}
