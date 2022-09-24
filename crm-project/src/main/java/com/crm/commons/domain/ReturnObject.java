package com.crm.commons.domain;


public class ReturnObject {
    private String code;    // 处理成功获取失败的标记：1--成功，0--失败
    private String message; // 提示信息
    private Object retDate; // 其他数据

    public Object getRetDate() {
        return retDate;
    }

    public void setRetDate(Object retDate) {
        this.retDate = retDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ReturnObject{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", retDate=" + retDate +
                '}';
    }
}
