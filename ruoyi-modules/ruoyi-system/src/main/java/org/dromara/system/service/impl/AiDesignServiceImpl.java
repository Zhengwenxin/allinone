package org.dromara.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.AiDesignPage;
import org.dromara.system.domain.AiDesignProject;
import org.dromara.system.domain.AiDesignReview;
import org.dromara.system.domain.bo.AiDesignPageBo;
import org.dromara.system.domain.bo.AiDesignProjectBo;
import org.dromara.system.domain.bo.AiDesignReviewBo;
import org.dromara.system.domain.vo.AiDesignPageVo;
import org.dromara.system.domain.vo.AiDesignProjectVo;
import org.dromara.system.domain.vo.AiDesignReviewVo;
import org.dromara.system.mapper.AiDesignPageMapper;
import org.dromara.system.mapper.AiDesignProjectMapper;
import org.dromara.system.mapper.AiDesignReviewMapper;
import org.dromara.system.service.IAiDesignService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class AiDesignServiceImpl implements IAiDesignService {

    private final AiDesignProjectMapper baseMapper;
    private final AiDesignPageMapper pageMapper;
    private final AiDesignReviewMapper reviewMapper;

    @Override
    public TableDataInfo<AiDesignProjectVo> selectPageDesignList(AiDesignProjectBo bo, PageQuery pageQuery) {
        Page<AiDesignProjectVo> page = baseMapper.selectPageDesignList(pageQuery.build(), buildQueryWrapper(bo));
        return TableDataInfo.build(page);
    }

    @Override
    public List<AiDesignProjectVo> selectDesignList(AiDesignProjectBo bo) {
        return baseMapper.selectVoList(buildQueryWrapper(bo));
    }

    @Override
    public AiDesignProjectVo selectDesignById(Long designId) {
        return baseMapper.selectVoById(designId);
    }

    @Override
    public boolean checkDesignCodeUnique(AiDesignProjectBo bo) {
        boolean exist = baseMapper.exists(new LambdaQueryWrapper<AiDesignProject>()
            .eq(AiDesignProject::getDesignCode, bo.getDesignCode())
            .ne(bo.getDesignId() != null, AiDesignProject::getDesignId, bo.getDesignId()));
        return !exist;
    }

    @Override
    public int insertDesign(AiDesignProjectBo bo) {
        AiDesignProject design = MapstructUtils.convert(bo, AiDesignProject.class);
        fillDesignMetrics(design);
        return baseMapper.insert(design);
    }

    @Override
    public int updateDesign(AiDesignProjectBo bo) {
        AiDesignProject design = MapstructUtils.convert(bo, AiDesignProject.class);
        fillDesignMetrics(design);
        return baseMapper.updateById(design);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDesignByIds(List<Long> designIds) {
        pageMapper.delete(new LambdaQueryWrapper<AiDesignPage>().in(AiDesignPage::getDesignId, designIds));
        reviewMapper.delete(new LambdaQueryWrapper<AiDesignReview>().in(AiDesignReview::getDesignId, designIds));
        return baseMapper.deleteByIds(designIds);
    }

    @Override
    public List<AiDesignPageVo> selectPageList(Long designId) {
        return pageMapper.selectVoList(new LambdaQueryWrapper<AiDesignPage>()
            .eq(AiDesignPage::getDesignId, designId)
            .orderByAsc(AiDesignPage::getSortOrder)
            .orderByDesc(AiDesignPage::getCreateTime));
    }

    @Override
    public AiDesignPageVo selectPageById(Long pageId) {
        return pageMapper.selectVoById(pageId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertPage(AiDesignPageBo bo) {
        assertDesignExists(bo.getDesignId());
        AiDesignPage page = MapstructUtils.convert(bo, AiDesignPage.class);
        int rows = pageMapper.insert(page);
        refreshDesignMetrics(bo.getDesignId());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updatePage(AiDesignPageBo bo) {
        AiDesignPage oldPage = pageMapper.selectById(bo.getPageId());
        if (oldPage == null) {
            throw new ServiceException("原型页面不存在");
        }
        assertDesignExists(bo.getDesignId());
        AiDesignPage page = MapstructUtils.convert(bo, AiDesignPage.class);
        int rows = pageMapper.updateById(page);
        refreshDesignMetrics(oldPage.getDesignId());
        if (!Objects.equals(oldPage.getDesignId(), bo.getDesignId())) {
            refreshDesignMetrics(bo.getDesignId());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deletePageByIds(List<Long> pageIds) {
        List<AiDesignPage> pages = pageMapper.selectList(new LambdaQueryWrapper<AiDesignPage>().in(AiDesignPage::getPageId, pageIds));
        int rows = pageMapper.deleteByIds(pageIds);
        pages.stream().map(AiDesignPage::getDesignId).distinct().forEach(this::refreshDesignMetrics);
        return rows;
    }

    @Override
    public List<AiDesignReviewVo> selectReviewList(Long designId) {
        return reviewMapper.selectVoList(new LambdaQueryWrapper<AiDesignReview>()
            .eq(AiDesignReview::getDesignId, designId)
            .orderByDesc(AiDesignReview::getCreateTime));
    }

    @Override
    public AiDesignReviewVo selectReviewById(Long reviewId) {
        return reviewMapper.selectVoById(reviewId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertReview(AiDesignReviewBo bo) {
        assertDesignExists(bo.getDesignId());
        AiDesignReview review = MapstructUtils.convert(bo, AiDesignReview.class);
        int rows = reviewMapper.insert(review);
        refreshReviewStatus(bo.getDesignId());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateReview(AiDesignReviewBo bo) {
        AiDesignReview oldReview = reviewMapper.selectById(bo.getReviewId());
        if (oldReview == null) {
            throw new ServiceException("设计评审不存在");
        }
        assertDesignExists(bo.getDesignId());
        AiDesignReview review = MapstructUtils.convert(bo, AiDesignReview.class);
        int rows = reviewMapper.updateById(review);
        refreshReviewStatus(oldReview.getDesignId());
        if (!Objects.equals(oldReview.getDesignId(), bo.getDesignId())) {
            refreshReviewStatus(bo.getDesignId());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteReviewByIds(List<Long> reviewIds) {
        List<AiDesignReview> reviews = reviewMapper.selectList(new LambdaQueryWrapper<AiDesignReview>().in(AiDesignReview::getReviewId, reviewIds));
        int rows = reviewMapper.deleteByIds(reviewIds);
        reviews.stream().map(AiDesignReview::getDesignId).distinct().forEach(this::refreshReviewStatus);
        return rows;
    }

    private LambdaQueryWrapper<AiDesignProject> buildQueryWrapper(AiDesignProjectBo bo) {
        Map<String, Object> params = bo.getParams();
        return new LambdaQueryWrapper<AiDesignProject>()
            .like(StringUtils.isNotBlank(bo.getDesignCode()), AiDesignProject::getDesignCode, bo.getDesignCode())
            .like(StringUtils.isNotBlank(bo.getDesignName()), AiDesignProject::getDesignName, bo.getDesignName())
            .like(StringUtils.isNotBlank(bo.getRequirementTitle()), AiDesignProject::getRequirementTitle, bo.getRequirementTitle())
            .eq(StringUtils.isNotBlank(bo.getDesignType()), AiDesignProject::getDesignType, bo.getDesignType())
            .eq(StringUtils.isNotBlank(bo.getDesignTool()), AiDesignProject::getDesignTool, bo.getDesignTool())
            .eq(StringUtils.isNotBlank(bo.getStatus()), AiDesignProject::getStatus, bo.getStatus())
            .eq(StringUtils.isNotBlank(bo.getReviewStatus()), AiDesignProject::getReviewStatus, bo.getReviewStatus())
            .like(StringUtils.isNotBlank(bo.getOwnerName()), AiDesignProject::getOwnerName, bo.getOwnerName())
            .like(StringUtils.isNotBlank(bo.getVersionNo()), AiDesignProject::getVersionNo, bo.getVersionNo())
            .between(params.get("beginTime") != null && params.get("endTime") != null,
                AiDesignProject::getCreateTime, params.get("beginTime"), params.get("endTime"))
            .orderByDesc(AiDesignProject::getCreateTime);
    }

    private void fillDesignMetrics(AiDesignProject design) {
        if (design.getPageCount() == null) {
            design.setPageCount(0);
        }
        if (design.getInteractionCount() == null) {
            design.setInteractionCount(0);
        }
        if (StringUtils.isBlank(design.getReviewStatus())) {
            design.setReviewStatus("pending");
        }
    }

    private void refreshDesignMetrics(Long designId) {
        if (designId == null) {
            return;
        }
        Long pageCount = pageMapper.selectCount(new LambdaQueryWrapper<AiDesignPage>().eq(AiDesignPage::getDesignId, designId));
        Long interactionCount = pageMapper.selectCount(new LambdaQueryWrapper<AiDesignPage>()
            .eq(AiDesignPage::getDesignId, designId)
            .isNotNull(AiDesignPage::getInteractionDesc)
            .ne(AiDesignPage::getInteractionDesc, ""));
        AiDesignProject design = new AiDesignProject();
        design.setDesignId(designId);
        design.setPageCount(pageCount.intValue());
        design.setInteractionCount(interactionCount.intValue());
        baseMapper.updateById(design);
    }

    private void refreshReviewStatus(Long designId) {
        if (designId == null) {
            return;
        }
        long totalCount = reviewMapper.selectCount(new LambdaQueryWrapper<AiDesignReview>()
            .eq(AiDesignReview::getDesignId, designId));
        long openCount = reviewMapper.selectCount(new LambdaQueryWrapper<AiDesignReview>()
            .eq(AiDesignReview::getDesignId, designId)
            .ne(AiDesignReview::getStatus, "closed"));
        AiDesignProject design = new AiDesignProject();
        design.setDesignId(designId);
        design.setReviewStatus(totalCount == 0 ? "pending" : openCount > 0 ? "reviewing" : "passed");
        baseMapper.updateById(design);
    }

    private void assertDesignExists(Long designId) {
        if (designId == null || baseMapper.selectById(designId) == null) {
            throw new ServiceException("设计项目不存在");
        }
    }

}
