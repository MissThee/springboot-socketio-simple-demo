package com.github.missthee.socketio.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;

//相应实体类
@Data
@Component
@NoArgsConstructor
public class AckModel<T> implements Serializable {
    private AckModel(Boolean result, T data, String msg) {
        this.result = result;
        try {
            data = data == null ? (T) new HashMap() : data;
        } catch (Exception ignored) {
        }
        this.data = data;
        this.msg = StringUtils.isEmpty(msg) ? "" : msg;
    }

    public static <T> AckModel<T> res(Boolean result, T data, String msg) {
        return new AckModel<>(result, data, msg);
    }

    public static <T> AckModel<T> res(Boolean result, T data) {
        return new AckModel<>(result, data, "");
    }

    public static <T> AckModel<T> res(Boolean result, String msg) {
        return new AckModel<>(result, null, "");
    }

    public static <T> AckModel<T> res(Boolean result) {
        return new AckModel<>(result, null, "");
    }

    public static <T> AckModel<T> success(T data, String msg) {
        return new AckModel<>(true, data, msg);
    }

    public static <T> AckModel<T> success(T data) {
        return new AckModel<>(true, data, "");
    }

    public static <T> AckModel<T> success(String msg) {
        return new AckModel<>(true, null, msg);
    }

    public static <T> AckModel<T> success() {
        return new AckModel<>(true, null, "");
    }

    public static <T> AckModel<T> failure(T data, String msg) {
        return new AckModel<>(false, data, msg);
    }

    public static <T> AckModel<T> failure(T data) {
        return new AckModel<>(false, data, "");
    }

    public static <T> AckModel<T> failure(String msg) {
        return new AckModel<>(false, null, msg);
    }

    public static <T> AckModel<T> failure() {
        return new AckModel<>(false, null, "");
    }

    private Boolean result;
    private T data;
    private String msg;
}