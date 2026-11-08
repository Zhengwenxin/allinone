package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.vo.ChangeImpactVo;

/**
 * 变更传播服务
 *
 * @author ruoyi
 */
public interface IAdoChangePropagationService {

    /**
     * 分析某工件版本变化对下游的影响
     *
     * @param artifactId    上游工件ID
     * @param targetVersion 目标版本，不传则使用当前版本
     * @return 影响报告
     */
    ChangeImpactVo analyze(Long artifactId, Integer targetVersion);

}
