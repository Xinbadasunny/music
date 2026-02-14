package com.vocalcoach.client.dto.cmd;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class SaveProgressCmd implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "课程ID不能为空")
    private String courseId;

    @NotBlank(message = "练习ID不能为空")
    private String exerciseId;

    @NotNull(message = "分数不能为空")
    private Integer score;
}
