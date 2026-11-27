package com.ruoyi.common.core.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.constant.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "响应体")
@Data
public class R<T> implements Serializable {

    @Schema(description = "状态码")
    private int code;

    @Schema(description = "提示消息")
    private String message;

    @Schema(description = "数据")
    private T data;

    @Schema(description = "总条数")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;

    public static R<Void> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return ok(data, null);
    }

    public static <T> R<T> ok(T data, Long total) {
        return ok(data, total, "操作成功");
    }

    public static <T> R<T> ok(T data, Long total, String message) {
        R<T> r = new R<>();
        r.setCode(HttpStatus.SUCCESS);
        r.setMessage(message);
        r.setData(data);
        r.setTotal(total);
        return r;
    }

    @SuppressWarnings("unused")
    public static R<Void> fail() {
        return fail("操作失败");
    }

    public static R<Void> fail(String message) {
        R<Void> r = new R<>();
        r.setCode(HttpStatus.ERROR);
        r.setMessage(message);
        return r;
    }

    public static <T> R<T> fail(T data, String message) {
        R<T> r = new R<>();
        r.setCode(HttpStatus.ERROR);
        r.setMessage(message);
        r.setData(data);
        return r;
    }

}
