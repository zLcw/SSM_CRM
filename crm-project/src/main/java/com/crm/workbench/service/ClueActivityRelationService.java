package com.crm.workbench.service;

import com.crm.workbench.domain.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationService {

    int saveCreateClueActivityRelationByList(List<ClueActivityRelation> list);

    int delelteClueActivityRelationByClueIdActivityId(ClueActivityRelation clueActivityRelation);
}
