package org.dromara.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.AiDesignProject;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = AiDesignProject.class)
public class AiDesignProjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @ExcelProperty(value = "设计ID")
    private Long designId;

    @ExcelProperty(value = "设计编号")
    private String designCode;

    @ExcelProperty(value = "设计名称")
    private String designName;

    @ExcelProperty(value = "关联需求ID")
    private Long requirementId;

    @ExcelProperty(value = "关联需求")
    private String requirementTitle;

    @ExcelProperty(value = "设计类型")
    private String designType;

    @ExcelProperty(value = "设计工具")
    private String designTool;

    @ExcelProperty(value = "负责人ID")
    private Long ownerId;

    @ExcelProperty(value = "负责人")
    private String ownerName;

    @ExcelProperty(value = "协作者")
    private String collaborator;

    @ExcelProperty(value = "设计状态")
    private String status;

    @ExcelProperty(value = "评审状态")
    private String reviewStatus;

    @ExcelProperty(value = "原型地址")
    private String prototypeUrl;

    @ExcelProperty(value = "设计稿地址")
    private String designUrl;

    @ExcelProperty(value = "版本号")
    private String versionNo;

    @ExcelProperty(value = "页面数")
    private Integer pageCount;

    @ExcelProperty(value = "交互数")
    private Integer interactionCount;

    @ExcelProperty(value = "计划完成日期")
    private Date dueDate;

    @ExcelProperty(value = "设计摘要")
    private String designBrief;

    private String aiSuggestion;

    private String remark;

    @ExcelProperty(value = "创建时间")
    private Date createTime;

}
