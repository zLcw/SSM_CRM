package com.crm.workbench.service;

import com.crm.workbench.domain.ClueRemark;

import java.util.List;

/**
 * @author zw
 */
public interface ClueRemarkService {
    /**
     * 根据clueid查该线索下所有的备注
     * @param clueId
     * @return
     */
    List<ClueRemark> queryClueRemarkForDetailByClueId(String clueId);
}
