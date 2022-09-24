package com.crm.workbench.service.impl;

import com.crm.workbench.domain.TranRemark;
import com.crm.workbench.mapper.TranRemarkMapper;
import com.crm.workbench.service.TranRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class TranRemarkServiceImpl implements TranRemarkService {
    @Autowired
    private TranRemarkMapper tranRemarkMapper;

    @Override
    public List<TranRemark> queryTranRemarkForDetailByTranId(String tranId) {
        return tranRemarkMapper.selectTranRemarkForDetailByTranId(tranId);
    }
}
