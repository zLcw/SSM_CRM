package com.crm.workbench.service.impl;

import com.crm.settings.domain.DicValue;
import com.crm.settings.mapper.DicValueMapper;
import com.crm.settings.service.DicValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class DicValueServiceImpl implements DicValueService {

    @Autowired
    private DicValueMapper dicValueMapper;

    @Override
    public List<DicValue> queryDicValueByTypeCode(String typeCode) {
        return dicValueMapper.selectDicValueByTypeCode(typeCode);
    }
}
