package com.honhe.common.rocketmq.result;

/**
 * 数据返回包装类
 */
public class ResultData<T> {

    private String code;
    private boolean isSuccess;
    private String message;
    private T data;

    public ResultData(boolean isSuccess, String code, String message, T data) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultData(boolean isSuccess, String message, T data) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.data = data;
    }

    public ResultData(boolean isSuccess, T data) {
        this.isSuccess = isSuccess;
        this.data = data;
    }
    /*public ResultData(boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }*/

    public static ResultData success() {
        return new ResultData<>(true, "0000", "操作成功！", null);
    }

    public static ResultData error(String code, String message) {
        return new ResultData<>(false, code, message, null);
    }

    public String getCode() {
        return code;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}