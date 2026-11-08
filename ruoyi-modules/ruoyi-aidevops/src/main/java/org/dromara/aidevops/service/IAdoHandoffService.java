package org.dromara.aidevops.service;

import org.dromara.aidevops.domain.AdoHandoff;
import org.dromara.aidevops.domain.bo.AdoHandoffBo;

import java.util.List;

/**
 * 交接契约服务
 *
 * @author ruoyi
 */
public interface IAdoHandoffService {

    Long createHandoff(AdoHandoffBo bo);

    AdoHandoff selectById(Long handoffId);

    List<AdoHandoff> selectList(Long projectId, String handoffType, String status);

    boolean signOff(Long handoffId, String status);

}
