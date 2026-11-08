package org.dromara.aidevops.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 变更影响分析视图
 *
 * @author ruoyi
 */
@Data
public class ChangeImpactVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long artifactId;

    private String artifactKey;

    private Integer currentVersion;

    private Integer targetVersion;

    private Integer affectedCount;

    private Date generatedAt;

    private List<ImpactItemVo> impacts = new ArrayList<>();

    @Data
    public static class ImpactItemVo implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long linkId;

        private String relation;

        private String impactLevel;

        private Long downstreamArtifactId;

        private String artifactType;

        private String artifactKey;

        private String title;

        private Integer basedOnVersion;

        private Integer currentVersion;

        private String reason;

    }

}
