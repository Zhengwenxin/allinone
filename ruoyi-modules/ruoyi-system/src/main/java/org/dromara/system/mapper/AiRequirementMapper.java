package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.mybatis.annotation.DataColumn;
import org.dromara.common.mybatis.annotation.DataPermission;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.AiRequirement;
import org.dromara.system.domain.vo.AiRequirementVo;

public interface AiRequirementMapper extends BaseMapperPlus<AiRequirement, AiRequirementVo> {

    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default Page<AiRequirementVo> selectPageRequirementList(Page<AiRequirement> page, Wrapper<AiRequirement> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }

}
