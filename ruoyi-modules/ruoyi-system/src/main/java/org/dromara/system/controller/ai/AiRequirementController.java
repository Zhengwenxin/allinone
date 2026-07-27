package org.dromara.system.controller.ai;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.bo.AiRequirementBo;
import org.dromara.system.domain.bo.AiRequirementChangeBo;
import org.dromara.system.domain.bo.AiRequirementStoryBo;
import org.dromara.system.domain.vo.AiRequirementChangeVo;
import org.dromara.system.domain.vo.AiRequirementStoryVo;
import org.dromara.system.domain.vo.AiRequirementVo;
import org.dromara.system.service.IAiRequirementService;
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
@RequestMapping("/ai/requirement")
public class AiRequirementController extends BaseController {

    private final IAiRequirementService requirementService;

    @SaCheckPermission("ai:requirement:list")
    @GetMapping("/list")
    public TableDataInfo<AiRequirementVo> list(AiRequirementBo bo, PageQuery pageQuery) {
        return requirementService.selectPageRequirementList(bo, pageQuery);
    }

    @Log(title = "需求管理", businessType = BusinessType.EXPORT)
    @SaCheckPermission("ai:requirement:export")
    @PostMapping("/export")
    public void export(AiRequirementBo bo, HttpServletResponse response) {
        List<AiRequirementVo> list = requirementService.selectRequirementList(bo);
        ExcelUtil.exportExcel(list, "需求数据", AiRequirementVo.class, response);
    }

    @SaCheckPermission("ai:requirement:query")
    @GetMapping("/{requirementId}")
    public R<AiRequirementVo> getInfo(@PathVariable Long requirementId) {
        return R.ok(requirementService.selectRequirementById(requirementId));
    }

    @SaCheckPermission("ai:requirement:add")
    @Log(title = "需求管理", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Void> add(@Validated @RequestBody AiRequirementBo bo) {
        if (!requirementService.checkRequirementCodeUnique(bo)) {
            return R.fail("新增需求'" + bo.getRequirementTitle() + "'失败，需求编号已存在");
        }
        return toAjax(requirementService.insertRequirement(bo));
    }

    @SaCheckPermission("ai:requirement:edit")
    @Log(title = "需求管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated @RequestBody AiRequirementBo bo) {
        if (!requirementService.checkRequirementCodeUnique(bo)) {
            return R.fail("修改需求'" + bo.getRequirementTitle() + "'失败，需求编号已存在");
        }
        return toAjax(requirementService.updateRequirement(bo));
    }

    @SaCheckPermission("ai:requirement:remove")
    @Log(title = "需求管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{requirementIds}")
    public R<Void> remove(@PathVariable Long[] requirementIds) {
        return toAjax(requirementService.deleteRequirementByIds(Arrays.asList(requirementIds)));
    }

    @SaCheckPermission("ai:requirement:query")
    @GetMapping("/{requirementId}/story/list")
    public R<List<AiRequirementStoryVo>> storyList(@PathVariable Long requirementId) {
        return R.ok(requirementService.selectStoryList(requirementId));
    }

    @SaCheckPermission("ai:requirement:query")
    @GetMapping("/story/{storyId}")
    public R<AiRequirementStoryVo> getStory(@PathVariable Long storyId) {
        return R.ok(requirementService.selectStoryById(storyId));
    }

    @SaCheckPermission("ai:requirement:edit")
    @Log(title = "用户故事", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/story")
    public R<Void> addStory(@Validated @RequestBody AiRequirementStoryBo bo) {
        return toAjax(requirementService.insertStory(bo));
    }

    @SaCheckPermission("ai:requirement:edit")
    @Log(title = "用户故事", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/story")
    public R<Void> editStory(@Validated @RequestBody AiRequirementStoryBo bo) {
        return toAjax(requirementService.updateStory(bo));
    }

    @SaCheckPermission("ai:requirement:edit")
    @Log(title = "用户故事", businessType = BusinessType.DELETE)
    @DeleteMapping("/story/{storyIds}")
    public R<Void> removeStory(@PathVariable Long[] storyIds) {
        return toAjax(requirementService.deleteStoryByIds(Arrays.asList(storyIds)));
    }

    @SaCheckPermission("ai:requirement:query")
    @GetMapping("/{requirementId}/change/list")
    public R<List<AiRequirementChangeVo>> changeList(@PathVariable Long requirementId) {
        return R.ok(requirementService.selectChangeList(requirementId));
    }

    @SaCheckPermission("ai:requirement:query")
    @GetMapping("/change/{changeId}")
    public R<AiRequirementChangeVo> getChange(@PathVariable Long changeId) {
        return R.ok(requirementService.selectChangeById(changeId));
    }

    @SaCheckPermission("ai:requirement:edit")
    @Log(title = "需求变更", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping("/change")
    public R<Void> addChange(@Validated @RequestBody AiRequirementChangeBo bo) {
        return toAjax(requirementService.insertChange(bo));
    }

    @SaCheckPermission("ai:requirement:edit")
    @Log(title = "需求变更", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping("/change")
    public R<Void> editChange(@Validated @RequestBody AiRequirementChangeBo bo) {
        return toAjax(requirementService.updateChange(bo));
    }

    @SaCheckPermission("ai:requirement:edit")
    @Log(title = "需求变更", businessType = BusinessType.DELETE)
    @DeleteMapping("/change/{changeIds}")
    public R<Void> removeChange(@PathVariable Long[] changeIds) {
        return toAjax(requirementService.deleteChangeByIds(Arrays.asList(changeIds)));
    }

}
