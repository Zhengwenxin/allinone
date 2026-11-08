package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.vo.AcCoverageVo;
import org.dromara.aidevops.domain.vo.SpecValidationVo;

/**
 * 结构化 Spec 服务
 *
 * @author ruoyi
 */
public interface ISpecService {

    /**
     * 获取 Spec JSON Schema
     */
    Object schema();

    /**
     * 校验 Spec
     *
     * @param specJson Spec JSON
     */
    SpecValidationVo validate(String specJson);

    /**
     * 计算 AC 覆盖率
     *
     * @param specArtifactId Spec 工件ID
     */
    AcCoverageVo acCoverage(Long specArtifactId);

}
