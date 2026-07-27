package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.common.mybatis.annotation.DataColumn;
import org.dromara.common.mybatis.annotation.DataPermission;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.AiDesignProject;
import org.dromara.system.domain.vo.AiDesignProjectVo;

public interface AiDesignProjectMapper extends BaseMapperPlus<AiDesignProject, AiDesignProjectVo> {

    @DataPermission({
        @DataColumn(key = "deptName", value = "create_dept"),
        @DataColumn(key = "userName", value = "create_by")
    })
    default Page<AiDesignProjectVo> selectPageDesignList(Page<AiDesignProject> page, Wrapper<AiDesignProject> queryWrapper) {
        return this.selectVoPage(page, queryWrapper);
    }

}
