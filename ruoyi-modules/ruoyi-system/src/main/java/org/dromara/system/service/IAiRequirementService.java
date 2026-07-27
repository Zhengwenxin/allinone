package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.AiRequirementBo;
import org.dromara.system.domain.bo.AiRequirementChangeBo;
import org.dromara.system.domain.bo.AiRequirementStoryBo;
import org.dromara.system.domain.vo.AiRequirementChangeVo;
import org.dromara.system.domain.vo.AiRequirementStoryVo;
import org.dromara.system.domain.vo.AiRequirementVo;

import java.util.List;

public interface IAiRequirementService {

    TableDataInfo<AiRequirementVo> selectPageRequirementList(AiRequirementBo bo, PageQuery pageQuery);

    List<AiRequirementVo> selectRequirementList(AiRequirementBo bo);

    AiRequirementVo selectRequirementById(Long requirementId);

    boolean checkRequirementCodeUnique(AiRequirementBo bo);

    int insertRequirement(AiRequirementBo bo);

    int updateRequirement(AiRequirementBo bo);

    int deleteRequirementByIds(List<Long> requirementIds);

    List<AiRequirementStoryVo> selectStoryList(Long requirementId);

    AiRequirementStoryVo selectStoryById(Long storyId);

    int insertStory(AiRequirementStoryBo bo);

    int updateStory(AiRequirementStoryBo bo);

    int deleteStoryByIds(List<Long> storyIds);

    List<AiRequirementChangeVo> selectChangeList(Long requirementId);

    AiRequirementChangeVo selectChangeById(Long changeId);

    int insertChange(AiRequirementChangeBo bo);

    int updateChange(AiRequirementChangeBo bo);

    int deleteChangeByIds(List<Long> changeIds);

}
