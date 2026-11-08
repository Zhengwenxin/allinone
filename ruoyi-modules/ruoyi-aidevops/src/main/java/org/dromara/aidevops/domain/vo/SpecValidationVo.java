package org.dromara.aidevops.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Spec 校验结果
 *
 * @author ruoyi
 */
@Data
public class SpecValidationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private boolean valid;

    private List<ErrorItem> errors = new ArrayList<>();

    @Data
    public static class ErrorItem implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String path;

        private String message;

    }

}
