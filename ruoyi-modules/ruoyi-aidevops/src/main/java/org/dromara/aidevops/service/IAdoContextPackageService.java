package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.AdoContextPackage;
import org.dromara.aidevops.domain.bo.AdoContextPackageBo;

/**
 * 任务上下文包服务
 *
 * @author ruoyi
 */
public interface IAdoContextPackageService {

    Long buildPackage(AdoContextPackageBo bo);

    AdoContextPackage selectByTaskId(Long taskId);

    boolean invalidateByTaskId(Long taskId);

}
