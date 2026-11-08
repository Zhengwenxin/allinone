package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.bo.AdoArtifactBo;
import org.dromara.aidevops.domain.vo.AdoArtifactVersionVo;
import org.dromara.aidevops.domain.vo.AdoArtifactVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 工件服务
 *
 * @author ruoyi
 */
public interface IAdoArtifactService {

    TableDataInfo<AdoArtifactVo> selectPageArtifactList(AdoArtifactBo bo, PageQuery pageQuery);

    AdoArtifactVo selectArtifactById(Long artifactId);

    AdoArtifactVo selectArtifactByKey(Long projectId, String artifactType, String artifactKey);

    Long createArtifact(AdoArtifactBo bo);

    boolean updateArtifact(AdoArtifactBo bo);

    boolean deleteArtifactByIds(List<Long> artifactIds);

    List<AdoArtifactVersionVo> selectVersions(Long artifactId);

}
