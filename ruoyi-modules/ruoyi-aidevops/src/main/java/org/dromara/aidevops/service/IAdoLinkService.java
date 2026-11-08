package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.AdoLink;
import org.dromara.aidevops.domain.bo.AdoLinkBo;

import java.util.List;

/**
 * 工件关系服务
 *
 * @author ruoyi
 */
public interface IAdoLinkService {

    Long createLink(AdoLinkBo bo);

    /**
     * 下游确认已消费上游最新版本
     *
     * @param linkId 关系ID
     * @return 是否成功
     */
    boolean acknowledge(Long linkId);

    boolean deleteLinkByIds(List<Long> linkIds);

    List<AdoLink> selectLinks(Long artifactId, String direction);

}
