package org.dromara.aidevops.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 一致性巡检报告
 *
 * @author ruoyi
 */
@Data
public class ConsistencyReportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long projectId;

    private Date generatedAt;

    private Integer total;

    private Map<String, Integer> counts = new LinkedHashMap<>();

    private List<FindingVo> findings = new ArrayList<>();

    @Data
    public static class FindingVo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String type;

        private String severity;

        private Long artifactId;

        private String artifactKey;

        private String title;

        private String message;

        private String suggestion;

        private String evidence;

    }

}
