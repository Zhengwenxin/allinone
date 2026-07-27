package org.dromara.system.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.AiDesignPageBo;
import org.dromara.system.domain.bo.AiDesignProjectBo;
import org.dromara.system.domain.bo.AiDesignReviewBo;
import org.dromara.system.domain.vo.AiDesignPageVo;
import org.dromara.system.domain.vo.AiDesignProjectVo;
import org.dromara.system.domain.vo.AiDesignReviewVo;
import org.dromara.system.service.IAiDesignService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/design")
public class AiDesignController extends BaseController {

    private final IAiDesignService designService;

    @SaCheckPermission("ai:design:list")
    @GetMapping("/list")
    public TableDataInfo<AiDesignProjectVo> list(AiDesignProjectBo bo, PageQuery pageQuery) {
        return designService.selectPageDesignList(bo, pageQuery);
    }

    @Log(title = "产品原型与设计协同", businessType = BusinessType.EXPORT)
    @SaCheckPermission("ai:design:export")
    @PostMapping("/export")
    public void export(AiDesignProjectBo bo, HttpServletResponse response) {
        List<AiDesignProjectVo> list = designService.selectDesignList(bo);
        ExcelUtil.exportExcel(list, "设计协同数据", AiDesignProjectVo.class, response);
    }

    @SaCheckPermission("ai:design:query")
    @GetMapping("/{designId}")
    public R<AiDesignProjectVo> getInfo(@PathVariable Long designId) {
        return R.ok(designService.selectDesignById(designId));
    }

    @SaCheckPermission("ai:design:add")
    @Log(title = "产品原型与设计协同", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated(AddGroup.class) @RequestBody AiDesignProjectBo bo) {
        if (!designService.checkDesignCodeUnique(bo)) {
            return R.fail("新增设计'" + bo.getDesignName() + "'失败，设计编号已存在");
        }
        return toAjax(designService.insertDesign(bo));
    }

    @SaCheckPermission("ai:design:edit")
    @Log(title = "产品原型与设计协同", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody AiDesignProjectBo bo) {
        if (!designService.checkDesignCodeUnique(bo)) {
            return R.fail("修改设计'" + bo.getDesignName() + "'失败，设计编号已存在");
        }
        return toAjax(designService.updateDesign(bo));
    }

    @SaCheckPermission("ai:design:remove")
    @Log(title = "产品原型与设计协同", businessType = BusinessType.DELETE)
    @DeleteMapping("/{designIds}")
    public R<Void> remove(@PathVariable Long[] designIds) {
        return toAjax(designService.deleteDesignByIds(Arrays.asList(designIds)));
    }

    @SaCheckPermission("ai:design:query")
    @GetMapping("/{designId}/page/list")
    public R<List<AiDesignPageVo>> pageList(@PathVariable Long designId) {
        return R.ok(designService.selectPageList(designId));
    }

    @SaCheckPermission("ai:design:query")
    @GetMapping("/page/{pageId}")
    public R<AiDesignPageVo> getPage(@PathVariable Long pageId) {
        return R.ok(designService.selectPageById(pageId));
    }

    @SaCheckPermission("ai:design:edit")
    @Log(title = "原型页面", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/page")
    public R<Void> addPage(@Validated(AddGroup.class) @RequestBody AiDesignPageBo bo) {
        return toAjax(designService.insertPage(bo));
    }

    @SaCheckPermission("ai:design:edit")
    @Log(title = "原型页面", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/page")
    public R<Void> editPage(@Validated(EditGroup.class) @RequestBody AiDesignPageBo bo) {
        return toAjax(designService.updatePage(bo));
    }

    @SaCheckPermission("ai:design:edit")
    @Log(title = "原型页面", businessType = BusinessType.DELETE)
    @DeleteMapping("/page/{pageIds}")
    public R<Void> removePage(@PathVariable Long[] pageIds) {
        return toAjax(designService.deletePageByIds(Arrays.asList(pageIds)));
    }

    @SaCheckPermission("ai:design:query")
    @GetMapping("/{designId}/review/list")
    public R<List<AiDesignReviewVo>> reviewList(@PathVariable Long designId) {
        return R.ok(designService.selectReviewList(designId));
    }

    @SaCheckPermission("ai:design:query")
    @GetMapping("/review/{reviewId}")
    public R<AiDesignReviewVo> getReview(@PathVariable Long reviewId) {
        return R.ok(designService.selectReviewById(reviewId));
    }

    @SaCheckPermission("ai:design:edit")
    @Log(title = "设计评审", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/review")
    public R<Void> addReview(@Validated(AddGroup.class) @RequestBody AiDesignReviewBo bo) {
        return toAjax(designService.insertReview(bo));
    }

    @SaCheckPermission("ai:design:edit")
    @Log(title = "设计评审", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/review")
    public R<Void> editReview(@Validated(EditGroup.class) @RequestBody AiDesignReviewBo bo) {
        return toAjax(designService.updateReview(bo));
    }

    @SaCheckPermission("ai:design:edit")
    @Log(title = "设计评审", businessType = BusinessType.DELETE)
    @DeleteMapping("/review/{reviewIds}")
    public R<Void> removeReview(@PathVariable Long[] reviewIds) {
        return toAjax(designService.deleteReviewByIds(Arrays.asList(reviewIds)));
    }

}
