-- ----------------------------
-- AI-DevOps 需求主表
-- ----------------------------
create table if not exists ai_requirement
(
  requirement_id       bigint(20)    not null                   comment '需求ID',
  tenant_id            varchar(20)   default '000000'           comment '租户编号',
  requirement_code     varchar(64)   not null                   comment '需求编号',
  requirement_title    varchar(200)  not null                   comment '需求标题',
  requirement_type     varchar(32)   default null               comment '需求类型',
  source               varchar(32)   default null               comment '需求来源',
  priority             varchar(16)   default null               comment '优先级',
  status               varchar(16)   default null               comment '需求状态',
  owner_id             bigint(20)    default null               comment '负责人ID',
  owner_name           varchar(64)   default null               comment '负责人名称',
  iteration_name       varchar(100)  default null               comment '所属迭代',
  business_value       int(4)        default null               comment '业务价值评分',
  technical_complexity int(4)        default null               comment '技术复杂度评分',
  risk_level           varchar(16)   default null               comment '风险等级',
  due_date             datetime                                 comment '计划完成时间',
  summary              varchar(500)  default null               comment '需求摘要',
  description          text                                    comment '需求描述',
  acceptance_criteria  text                                    comment '验收标准',
  ai_analysis          text                                    comment 'AI分析结果',
  del_flag             char(1)       default '0'                comment '删除标志（0代表存在 1代表删除）',
  create_dept          bigint(20)    default null               comment '创建部门',
  create_by            bigint(20)    default null               comment '创建者',
  create_time          datetime                                 comment '创建时间',
  update_by            bigint(20)    default null               comment '更新者',
  update_time          datetime                                 comment '更新时间',
  remark               varchar(500)  default null               comment '备注',
  primary key (requirement_id),
  unique key uk_ai_requirement_code (tenant_id, requirement_code),
  key idx_ai_requirement_status (tenant_id, status),
  key idx_ai_requirement_priority (tenant_id, priority),
  key idx_ai_requirement_owner (tenant_id, owner_id)
) engine=innodb comment = 'AI-DevOps需求主表';

-- ----------------------------
-- AI-DevOps 用户故事表
-- ----------------------------
create table if not exists ai_requirement_story
(
  story_id            bigint(20)    not null                   comment '用户故事ID',
  tenant_id           varchar(20)   default '000000'           comment '租户编号',
  requirement_id      bigint(20)    not null                   comment '需求ID',
  story_title         varchar(200)  not null                   comment '用户故事标题',
  role_name           varchar(100)  default null               comment '用户角色',
  story_value         varchar(500)  default null               comment '故事价值',
  acceptance_criteria text                                    comment '验收标准',
  priority            varchar(16)   default null               comment '优先级',
  status              varchar(16)   default null               comment '状态',
  create_dept         bigint(20)    default null               comment '创建部门',
  create_by           bigint(20)    default null               comment '创建者',
  create_time         datetime                                 comment '创建时间',
  update_by           bigint(20)    default null               comment '更新者',
  update_time         datetime                                 comment '更新时间',
  remark              varchar(500)  default null               comment '备注',
  primary key (story_id),
  key idx_ai_requirement_story_req (tenant_id, requirement_id)
) engine=innodb comment = 'AI-DevOps需求用户故事表';

-- ----------------------------
-- AI-DevOps 需求变更表
-- ----------------------------
create table if not exists ai_requirement_change
(
  change_id       bigint(20)    not null                   comment '需求变更ID',
  tenant_id       varchar(20)   default '000000'           comment '租户编号',
  requirement_id  bigint(20)    not null                   comment '需求ID',
  change_title    varchar(200)  not null                   comment '变更标题',
  change_reason   text                                    comment '变更原因',
  impact_scope    text                                    comment '影响范围',
  status          varchar(16)   default null               comment '状态',
  create_dept     bigint(20)    default null               comment '创建部门',
  create_by       bigint(20)    default null               comment '创建者',
  create_time     datetime                                 comment '创建时间',
  update_by       bigint(20)    default null               comment '更新者',
  update_time     datetime                                 comment '更新时间',
  remark          varchar(500)  default null               comment '备注',
  primary key (change_id),
  key idx_ai_requirement_change_req (tenant_id, requirement_id)
) engine=innodb comment = 'AI-DevOps需求变更表';

-- ----------------------------
-- 可选：AI-DevOps 需求管理菜单权限
-- ----------------------------
insert ignore into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values('6100', 'AI-DevOps平台', '0', '6', 'ai-devops', null, '', 1, 0, 'M', '0', '0', '', 'workflow', 103, 1, sysdate(), null, null, 'AI-DevOps平台目录');
insert ignore into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values('6101', '需求管理', '6100', '1', 'requirement', 'ai-devops/requirement/index', '', 1, 0, 'C', '0', '0', 'ai:requirement:list', 'documentation', 103, 1, sysdate(), null, null, 'AI-DevOps需求管理菜单');
insert ignore into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values('6111', '需求查询', '6101', '1', '', '', '', 1, 0, 'F', '0', '0', 'ai:requirement:query', '#', 103, 1, sysdate(), null, null, ''),
       ('6112', '需求新增', '6101', '2', '', '', '', 1, 0, 'F', '0', '0', 'ai:requirement:add', '#', 103, 1, sysdate(), null, null, ''),
       ('6113', '需求修改', '6101', '3', '', '', '', 1, 0, 'F', '0', '0', 'ai:requirement:edit', '#', 103, 1, sysdate(), null, null, ''),
       ('6114', '需求删除', '6101', '4', '', '', '', 1, 0, 'F', '0', '0', 'ai:requirement:remove', '#', 103, 1, sysdate(), null, null, ''),
       ('6115', '需求导出', '6101', '5', '', '', '', 1, 0, 'F', '0', '0', 'ai:requirement:export', '#', 103, 1, sysdate(), null, null, '');
