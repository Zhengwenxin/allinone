-- AI-DevOps 产品原型与设计协同子系统

CREATE TABLE IF NOT EXISTS ai_design_project (
    design_id BIGINT NOT NULL COMMENT '设计ID',
    tenant_id VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    design_code VARCHAR(64) NOT NULL COMMENT '设计编号',
    design_name VARCHAR(200) NOT NULL COMMENT '设计名称',
    requirement_id BIGINT DEFAULT NULL COMMENT '关联需求ID',
    requirement_title VARCHAR(200) DEFAULT NULL COMMENT '关联需求标题',
    design_type VARCHAR(32) DEFAULT NULL COMMENT '设计类型 wireframe/prototype/visual/design-system',
    design_tool VARCHAR(32) DEFAULT NULL COMMENT '设计工具 figma/mastergo/axure/sketch/other',
    owner_id BIGINT DEFAULT NULL COMMENT '负责人ID',
    owner_name VARCHAR(64) DEFAULT NULL COMMENT '负责人',
    collaborator VARCHAR(500) DEFAULT NULL COMMENT '协作者',
    status VARCHAR(32) DEFAULT 'draft' COMMENT '设计状态 draft/designing/reviewing/approved/delivered',
    review_status VARCHAR(32) DEFAULT 'pending' COMMENT '评审状态 pending/reviewing/passed/rejected',
    prototype_url VARCHAR(500) DEFAULT NULL COMMENT '原型地址',
    design_url VARCHAR(500) DEFAULT NULL COMMENT '设计稿地址',
    version_no VARCHAR(32) DEFAULT 'V1.0' COMMENT '版本号',
    page_count INT DEFAULT 0 COMMENT '页面数量',
    interaction_count INT DEFAULT 0 COMMENT '交互数量',
    due_date DATETIME DEFAULT NULL COMMENT '计划完成日期',
    design_brief VARCHAR(1000) DEFAULT NULL COMMENT '设计摘要',
    ai_suggestion TEXT COMMENT 'AI设计建议',
    del_flag CHAR(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
    create_dept BIGINT DEFAULT NULL COMMENT '创建部门',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (design_id),
    UNIQUE KEY uk_ai_design_project_code (tenant_id, design_code),
    KEY idx_ai_design_project_req (tenant_id, requirement_id),
    KEY idx_ai_design_project_status (tenant_id, status, review_status)
) ENGINE=InnoDB COMMENT='AI-DevOps设计协同项目表';

CREATE TABLE IF NOT EXISTS ai_design_page (
    page_id BIGINT NOT NULL COMMENT '页面ID',
    tenant_id VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    design_id BIGINT NOT NULL COMMENT '设计ID',
    page_name VARCHAR(120) NOT NULL COMMENT '页面名称',
    page_type VARCHAR(32) DEFAULT NULL COMMENT '页面类型 entry/form/list/detail/dashboard/mobile',
    route_path VARCHAR(200) DEFAULT NULL COMMENT '路由路径',
    user_scenario VARCHAR(1000) DEFAULT NULL COMMENT '用户场景',
    interaction_desc TEXT COMMENT '交互说明',
    component_spec TEXT COMMENT '组件规范',
    status VARCHAR(32) DEFAULT 'draft' COMMENT '页面状态 draft/designing/reviewing/confirmed',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_dept BIGINT DEFAULT NULL COMMENT '创建部门',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (page_id),
    KEY idx_ai_design_page_design (tenant_id, design_id),
    KEY idx_ai_design_page_status (tenant_id, status)
) ENGINE=InnoDB COMMENT='AI-DevOps原型页面表';

CREATE TABLE IF NOT EXISTS ai_design_review (
    review_id BIGINT NOT NULL COMMENT '评审ID',
    tenant_id VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    design_id BIGINT NOT NULL COMMENT '设计ID',
    review_title VARCHAR(200) NOT NULL COMMENT '评审标题',
    reviewer_name VARCHAR(64) DEFAULT NULL COMMENT '评审人',
    review_type VARCHAR(32) DEFAULT NULL COMMENT '评审类型 product/ux/ui/frontend/qa/security',
    severity VARCHAR(32) DEFAULT NULL COMMENT '严重级别 low/medium/high/blocker',
    status VARCHAR(32) DEFAULT 'open' COMMENT '状态 open/processing/closed',
    review_time DATETIME DEFAULT NULL COMMENT '评审时间',
    issue_desc TEXT COMMENT '问题描述',
    suggestion TEXT COMMENT '修改建议',
    create_dept BIGINT DEFAULT NULL COMMENT '创建部门',
    create_by BIGINT DEFAULT NULL COMMENT '创建者',
    create_time DATETIME DEFAULT NULL COMMENT '创建时间',
    update_by BIGINT DEFAULT NULL COMMENT '更新者',
    update_time DATETIME DEFAULT NULL COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (review_id),
    KEY idx_ai_design_review_design (tenant_id, design_id),
    KEY idx_ai_design_review_status (tenant_id, status, severity)
) ENGINE=InnoDB COMMENT='AI-DevOps设计评审表';

INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6201, '原型设计', 6100, 2, 'design', 'ai-devops/design/index', NULL, 1, 0, 'C', '0', '0', 'ai:design:list', 'edit', 103, 1, NOW(), NULL, NULL, '产品原型与设计协同菜单');
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6202, '原型设计平台', 6100, 3, 'design/editor', 'ai-devops/design/editor', NULL, 1, 1, 'C', '1', '0', 'ai:design:edit', '#', 103, 1, NOW(), NULL, NULL, '/ai-devops/design');
INSERT INTO sys_menu(menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6211, '设计查询', 6201, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'ai:design:query', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6212, '设计新增', 6201, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'ai:design:add', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6213, '设计修改', 6201, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'ai:design:edit', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6214, '设计删除', 6201, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'ai:design:remove', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6215, '设计导出', 6201, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'ai:design:export', '#', 103, 1, NOW(), NULL, NULL, '');
