package org.dromara.aidevops.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AC 覆盖率报告
 *
 * @author ruoyi
 */
@Data
public class AcCoverageVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long specArtifactId;

    private Integer total;

    private Integer covered;

    private Integer uncovered;

    /**
     * 覆盖率，0-1
     */
    private Double coverageRate;

    private List<AcItem> details = new ArrayList<>();

    @Data
    public static class AcItem implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String acId;

        private boolean covered;

        private List<String> testCaseKeys = new ArrayList<>();

    }

}
