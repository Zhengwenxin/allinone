package org.dromara.aidevops.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 工件追溯视图
 *
 * @author ruoyi
 */
@Data
public class TraceabilityVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private TraceNodeVo root;

    private List<TraceNodeVo> nodes = new ArrayList<>();

    private List<TraceEdgeVo> edges = new ArrayList<>();

    @Data
    public static class TraceNodeVo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long artifactId;

        private String artifactType;

        private String artifactKey;

        private String title;

        private String status;

        private Integer currentVersion;

        private String ownerName;

        private Integer level;

    }

    @Data
    public static class TraceEdgeVo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long linkId;

        private Long fromArtifactId;

        private Integer fromVersion;

        private Long toArtifactId;

        private Integer toVersion;

        private String relation;

        private BigDecimal confidence;

        private String origin;

        private String status;

    }

}
