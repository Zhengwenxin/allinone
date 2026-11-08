package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.vo.TraceabilityVo;

/**
 * 工件追溯服务
 *
 * @author ruoyi
 */
public interface IAdoTraceabilityService {

    /**
     * 查询工件上下游关系
     *
     * @param artifactId 工件ID
     * @param direction  upstream/downstream/both
     * @param depth      深度，1-5
     * @return 追溯视图
     */
    TraceabilityVo trace(Long artifactId, String direction, int depth);

}
