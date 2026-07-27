package org.dromara.system.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.domain.bo.AiDesignPageBo;
import org.dromara.system.domain.bo.AiDesignProjectBo;
import org.dromara.system.domain.bo.AiDesignReviewBo;
import org.dromara.system.domain.vo.AiDesignPageVo;
import org.dromara.system.domain.vo.AiDesignProjectVo;
import org.dromara.system.domain.vo.AiDesignReviewVo;

import java.util.List;

public interface IAiDesignService {

    TableDataInfo<AiDesignProjectVo> selectPageDesignList(AiDesignProjectBo bo, PageQuery pageQuery);

    List<AiDesignProjectVo> selectDesignList(AiDesignProjectBo bo);

    AiDesignProjectVo selectDesignById(Long designId);

    boolean checkDesignCodeUnique(AiDesignProjectBo bo);

    int insertDesign(AiDesignProjectBo bo);

    int updateDesign(AiDesignProjectBo bo);

    int deleteDesignByIds(List<Long> designIds);

    List<AiDesignPageVo> selectPageList(Long designId);

    AiDesignPageVo selectPageById(Long pageId);

    int insertPage(AiDesignPageBo bo);

    int updatePage(AiDesignPageBo bo);

    int deletePageByIds(List<Long> pageIds);

    List<AiDesignReviewVo> selectReviewList(Long designId);

    AiDesignReviewVo selectReviewById(Long reviewId);

    int insertReview(AiDesignReviewBo bo);

    int updateReview(AiDesignReviewBo bo);

    int deleteReviewByIds(List<Long> reviewIds);

}
