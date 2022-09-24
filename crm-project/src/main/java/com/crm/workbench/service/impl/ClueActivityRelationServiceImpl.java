package com.crm.workbench.service.impl;

import com.crm.workbench.domain.ClueActivityRelation;
import com.crm.workbench.mapper.ClueActivityRelationMapper;
import com.crm.workbench.service.ClueActivityRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class ClueActivityRelationServiceImpl implements ClueActivityRelationService {

    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;

    @Override
    public int saveCreateClueActivityRelationByList(List<ClueActivityRelation> list) {
        return clueActivityRelationMapper.insertClueActivityRelationByList(list);
    }

    @Override
    public int delelteClueActivityRelationByClueIdActivityId(ClueActivityRelation clueActivityRelation) {
        return clueActivityRelationMapper.deleteClueActivityRelationByClueIActivityId(clueActivityRelation);
    }
}
