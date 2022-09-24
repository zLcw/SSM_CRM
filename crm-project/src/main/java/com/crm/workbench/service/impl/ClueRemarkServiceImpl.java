package com.crm.workbench.service.impl;

import com.crm.workbench.domain.ClueRemark;
import com.crm.workbench.mapper.ClueRemarkMapper;
import com.crm.workbench.service.ClueRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class ClueRemarkServiceImpl implements ClueRemarkService {

    @Autowired
    private ClueRemarkMapper clueRemarkMapper;
    @Override
    public List<ClueRemark> queryClueRemarkForDetailByClueId(String clueId) {
        return clueRemarkMapper.selectClueRemarkForDetailByClueId(clueId);
    }
}
