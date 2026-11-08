package org.dromara.aidevops.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.aidevops.domain.AdoArtifact;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 工件视图对象
 *
 * @author ruoyi
 */
@Data
@AutoMapper(target = AdoArtifact.class)
public class AdoArtifactVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long artifactId;

    private Long projectId;

    private String artifactType;

    private String artifactKey;

    private String title;

    private String summary;

    private String status;

    private Long ownerId;

    private String ownerName;

    private Integer currentVersion;

    private String sourceSystem;

    private String sourceRef;

    private String payload;

    private Date createTime;

    private Date updateTime;

    private String remark;

}
