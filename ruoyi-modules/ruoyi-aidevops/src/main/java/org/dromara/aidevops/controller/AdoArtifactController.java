package org.dromara.aidevops.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.domain.bo.AdoArtifactBo;
import org.dromara.aidevops.domain.vo.AdoArtifactVersionVo;
import org.dromara.aidevops.domain.vo.AdoArtifactVo;
import org.dromara.aidevops.service.IAdoArtifactService;
import org.dromara.common.core.domain.R;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 工件管理
 *
 * @author ruoyi
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-devops/artifact")
public class AdoArtifactController extends BaseController {

    private final IAdoArtifactService artifactService;

    @SaCheckPermission("ai:devops:artifact:list")
    @GetMapping("/list")
    public TableDataInfo<AdoArtifactVo> list(AdoArtifactBo bo, PageQuery pageQuery) {
        return artifactService.selectPageArtifactList(bo, pageQuery);
    }

    @SaCheckPermission("ai:devops:artifact:query")
    @GetMapping("/{artifactId}")
    public R<AdoArtifactVo> getInfo(@PathVariable Long artifactId) {
        return R.ok(artifactService.selectArtifactById(artifactId));
    }

    @SaCheckPermission("ai:devops:artifact:query")
    @GetMapping("/key")
    public R<AdoArtifactVo> getByKey(@RequestParam Long projectId,
                                     @RequestParam String artifactType,
                                     @RequestParam String artifactKey) {
        return R.ok(artifactService.selectArtifactByKey(projectId, artifactType, artifactKey));
    }

    @SaCheckPermission("ai:devops:artifact:add")
    @Log(title = "AI-DevOps工件", businessType = BusinessType.INSERT)
    @RepeatSubmit
    @PostMapping
    public R<Long> add(@Validated @RequestBody AdoArtifactBo bo) {
        return R.ok(artifactService.createArtifact(bo));
    }

    @SaCheckPermission("ai:devops:artifact:edit")
    @Log(title = "AI-DevOps工件", businessType = BusinessType.UPDATE)
    @RepeatSubmit
    @PutMapping
    public R<Void> edit(@Validated @RequestBody AdoArtifactBo bo) {
        return toAjax(artifactService.updateArtifact(bo));
    }

    @SaCheckPermission("ai:devops:artifact:remove")
    @Log(title = "AI-DevOps工件", businessType = BusinessType.DELETE)
    @DeleteMapping("/{artifactIds}")
    public R<Void> remove(@PathVariable Long[] artifactIds) {
        return toAjax(artifactService.deleteArtifactByIds(Arrays.asList(artifactIds)));
    }

    @SaCheckPermission("ai:devops:artifact:query")
    @GetMapping("/{artifactId}/versions")
    public R<List<AdoArtifactVersionVo>> versions(@PathVariable Long artifactId) {
        return R.ok(artifactService.selectVersions(artifactId));
    }

}
