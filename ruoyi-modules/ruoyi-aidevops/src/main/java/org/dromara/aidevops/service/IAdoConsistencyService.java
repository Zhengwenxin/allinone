package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.vo.ConsistencyReportVo;

/**
 * 一致性巡检服务
 *
 * @author ruoyi
 */
public interface IAdoConsistencyService {

    /**
     * 巡检项目内的工件图一致性
     *
     * @param projectId 项目ID
     * @return 巡检报告
     */
    ConsistencyReportVo check(Long projectId);

}
