package com.crm.workbench.vo;

/**
 * 专门为了显示漏斗图而设计的vo实体类
 * @Author: zhou
 * @Version: 1.0
 */
public class FunnelVO {

    private String name;
    private Integer value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
