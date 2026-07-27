package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.AiRequirement;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiRequirement.class)
public class AiRequirementVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "需求ID")
    private Long requirementId;

    @ExcelProperty(value = "需求编号")
    private String requirementCode;

    @ExcelProperty(value = "需求标题")
    private String requirementTitle;

    @ExcelProperty(value = "需求类型")
    private String requirementType;

    @ExcelProperty(value = "需求来源")
    private String source;

    @ExcelProperty(value = "优先级")
    private String priority;

    @ExcelProperty(value = "状态")
    private String status;

    @ExcelProperty(value = "负责人ID")
    private Long ownerId;

    @ExcelProperty(value = "负责人")
    private String ownerName;

    @ExcelProperty(value = "迭代")
    private String iterationName;

    @ExcelProperty(value = "业务价值")
    private Integer businessValue;

    @ExcelProperty(value = "技术复杂度")
    private Integer technicalComplexity;

    @ExcelProperty(value = "风险等级")
    private String riskLevel;

    @ExcelProperty(value = "计划完成日期")
    private Date dueDate;

    @ExcelProperty(value = "需求摘要")
    private String summary;

    private String description;

    private String acceptanceCriteria;

    private String aiAnalysis;

    private String remark;

    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
