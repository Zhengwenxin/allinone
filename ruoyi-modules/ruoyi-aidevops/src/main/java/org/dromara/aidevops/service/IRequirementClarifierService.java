package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.bo.RequirementClarifyBo;
import org.dromara.aidevops.domain.vo.RequirementClarifyVo;

/**
 * 需求澄清 Agent
 *
 * @author ruoyi
 */
public interface IRequirementClarifierService {

    /**
     * 把原始需求细化为结构化 Spec 草稿
     *
     * @param bo 请求
     * @return 澄清结果
     */
    RequirementClarifyVo clarify(RequirementClarifyBo bo);

}
