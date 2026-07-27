package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.AiRequirement;
import org.dromara.system.domain.AiRequirementChange;
import org.dromara.system.domain.AiRequirementStory;
import org.dromara.system.domain.bo.AiRequirementBo;
import org.dromara.system.domain.bo.AiRequirementChangeBo;
import org.dromara.system.domain.bo.AiRequirementStoryBo;
import org.dromara.system.domain.vo.AiRequirementChangeVo;
import org.dromara.system.domain.vo.AiRequirementStoryVo;
import org.dromara.system.domain.vo.AiRequirementVo;
import org.dromara.system.mapper.AiRequirementChangeMapper;
import org.dromara.system.mapper.AiRequirementMapper;
import org.dromara.system.mapper.AiRequirementStoryMapper;
import org.dromara.system.service.IAiRequirementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AiRequirementServiceImpl implements IAiRequirementService {

    private final AiRequirementMapper baseMapper;
    private final AiRequirementStoryMapper storyMapper;
    private final AiRequirementChangeMapper changeMapper;

    @Override
    public TableDataInfo<AiRequirementVo> selectPageRequirementList(AiRequirementBo bo, PageQuery pageQuery) {
        Page<AiRequirementVo> page = baseMapper.selectPageRequirementList(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(page);
    }

    @Override
    public List<AiRequirementVo> selectRequirementList(AiRequirementBo bo) {
        return baseMapper.selectVoList(buildQueryWrapper(bo));
    }

    @Override
    public AiRequirementVo selectRequirementById(Long requirementId) {
        return baseMapper.selectVoById(requirementId);
    }

    @Override
    public boolean checkRequirementCodeUnique(AiRequirementBo bo) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<AiRequirement>()
            .eq(AiRequirement::getRequirementCode, bo.getRequirementCode())
            .ne(bo.getRequirementId() != null, AiRequirement::getRequirementId, bo.getRequirementId()));
        return !exist;
    }

    @Override
    public int insertRequirement(AiRequirementBo bo) {
        AiRequirement requirement = MapstructUtils.convert(bo, AiRequirement.class);
        return baseMapper.insert(requirement);
    }

    @Override
    public int updateRequirement(AiRequirementBo bo) {
        AiRequirement requirement = MapstructUtils.convert(bo, AiRequirement.class);
        return baseMapper.updateById(requirement);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteRequirementByIds(List<Long> requirementIds) {
        storyMapper.delete(new LambdaQueryWrapper<AiRequirementStory>().in(AiRequirementStory::getRequirementId, requirementIds));
        changeMapper.delete(new LambdaQueryWrapper<AiRequirementChange>().in(AiRequirementChange::getRequirementId, requirementIds));
        return baseMapper.deleteByIds(requirementIds);
    }

    @Override
    public List<AiRequirementStoryVo> selectStoryList(Long requirementId) {
        return storyMapper.selectVoList(new LambdaQueryWrapper<AiRequirementStory>()
            .eq(AiRequirementStory::getRequirementId, requirementId)
            .orderByDesc(AiRequirementStory::getCreateTime));
    }

    @Override
    public AiRequirementStoryVo selectStoryById(Long storyId) {
        return storyMapper.selectVoById(storyId);
    }

    @Override
    public int insertStory(AiRequirementStoryBo bo) {
        AiRequirementStory story = MapstructUtils.convert(bo, AiRequirementStory.class);
        return storyMapper.insert(story);
    }

    @Override
    public int updateStory(AiRequirementStoryBo bo) {
        AiRequirementStory story = MapstructUtils.convert(bo, AiRequirementStory.class);
        return storyMapper.updateById(story);
    }

    @Override
    public int deleteStoryByIds(List<Long> storyIds) {
        return storyMapper.deleteByIds(storyIds);
    }

    @Override
    public List<AiRequirementChangeVo> selectChangeList(Long requirementId) {
        return changeMapper.selectVoList(new LambdaQueryWrapper<AiRequirementChange>()
            .eq(AiRequirementChange::getRequirementId, requirementId)
            .orderByDesc(AiRequirementChange::getCreateTime));
    }

    @Override
    public AiRequirementChangeVo selectChangeById(Long changeId) {
        return changeMapper.selectVoById(changeId);
    }

    @Override
    public int insertChange(AiRequirementChangeBo bo) {
        AiRequirementChange change = MapstructUtils.convert(bo, AiRequirementChange.class);
        return changeMapper.insert(change);
    }

    @Override
    public int updateChange(AiRequirementChangeBo bo) {
        AiRequirementChange change = MapstructUtils.convert(bo, AiRequirementChange.class);
        return changeMapper.updateById(change);
    }

    @Override
    public int deleteChangeByIds(List<Long> changeIds) {
        return changeMapper.deleteByIds(changeIds);
    }

    private LambdaQueryWrapper<AiRequirement> buildQueryWrapper(AiRequirementBo bo) {
        Map<String, Object> params = bo.getParams();
        return new LambdaQueryWrapper<AiRequirement>()
            .like(StringUtils.isNotBlank(bo.getRequirementCode()), AiRequirement::getRequirementCode, bo.getRequirementCode())
            .like(StringUtils.isNotBlank(bo.getRequirementTitle()), AiRequirement::getRequirementTitle, bo.getRequirementTitle())
            .eq(StringUtils.isNotBlank(bo.getRequirementType()), AiRequirement::getRequirementType, bo.getRequirementType())
            .eq(StringUtils.isNotBlank(bo.getSource()), AiRequirement::getSource, bo.getSource())
            .eq(StringUtils.isNotBlank(bo.getPriority()), AiRequirement::getPriority, bo.getPriority())
            .eq(StringUtils.isNotBlank(bo.getStatus()), AiRequirement::getStatus, bo.getStatus())
            .eq(StringUtils.isNotBlank(bo.getRiskLevel()), AiRequirement::getRiskLevel, bo.getRiskLevel())
            .like(StringUtils.isNotBlank(bo.getOwnerName()), AiRequirement::getOwnerName, bo.getOwnerName())
            .like(StringUtils.isNotBlank(bo.getIterationName()), AiRequirement::getIterationName, bo.getIterationName())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                AiRequirement::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(AiRequirement::getCreateTime);
    }

}
