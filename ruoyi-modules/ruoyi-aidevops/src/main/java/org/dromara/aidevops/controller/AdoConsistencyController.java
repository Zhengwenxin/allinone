package org.dromara.aidevops.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import lombok.RequiredArgsConstructor;
import org.dromara.aidevops.domain.vo.ConsistencyReportVo;
import org.dromara.aidevops.service.IAdoConsistencyService;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 一致性巡检
 *
 * @author ruoyi
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/ai-devops/consistency")
public class AdoConsistencyController extends BaseController {

    private final IAdoConsistencyService consistencyService;

    @SaCheckPermission("ai:devops:consistency:check")
    @GetMapping("/{projectId}")
    public R<ConsistencyReportVo> check(@PathVariable Long projectId) {
        return R.ok(consistencyService.check(projectId));
    }

}
