package com.crm.workbench.service;

import com.crm.workbench.domain.TranRemark;

import java.util.List;

/**
 * @Author: zhou
 * @Version: 1.0
 */
public interface TranRemarkService {

    List<TranRemark> queryTranRemarkForDetailByTranId(String tranId);
}
