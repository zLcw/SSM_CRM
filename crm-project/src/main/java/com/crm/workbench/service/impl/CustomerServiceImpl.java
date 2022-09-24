package com.crm.workbench.service.impl;

import com.crm.workbench.mapper.CustomerMapper;
import com.crm.workbench.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: zhou
 * @Version: 1.0
 */
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public List<String> queryAllCustomerName(String name) {
        return customerMapper.selectAllCustomerName(name);
    }
}
