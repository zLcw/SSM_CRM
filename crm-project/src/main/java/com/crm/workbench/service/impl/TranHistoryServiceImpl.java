package com.crm.workbench.service.impl;

import com.crm.workbench.domain.TranHistory;
import com.crm.workbench.mapper.TranHistoryMapper;
import com.crm.workbench.service.TranHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class TranHistoryServiceImpl implements TranHistoryService {
    @Autowired
    private TranHistoryMapper tranHistoryMapper;

    @Override
    public List<TranHistory> queryTranHistoryForDetailByTranId(String tranId) {
        return tranHistoryMapper.selectTranHistoryForDetailByTranId(tranId);
    }
}
