package org.dromara.aidevops.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.aidevops.domain.AdoArtifactVersion;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 工件版本视图对象
 *
 * @author ruoyi
 */
@Data
@AutoMapper(target = AdoArtifactVersion.class)
public class AdoArtifactVersionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long versionId;

    private Long artifactId;

    private Integer versionNo;

    private String snapshot;

    private String diff;

    private String changeSummary;

    private String authorType;

    private Long authorId;

    private String authorName;

    private Long approvedBy;

    private Date approvedAt;

    private Date createTime;

}
