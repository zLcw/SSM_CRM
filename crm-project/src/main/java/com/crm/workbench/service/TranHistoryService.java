package com.crm.workbench.service;

import com.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranHistoryService {

    List<TranHistory> queryTranHistoryForDetailByTranId(String tranId);
}
