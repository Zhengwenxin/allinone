-- ----------------------------
-- AI-DevOps 研发上下文核心表
-- 说明：
--   1. ado_artifact 是统一工件注册表，领域表（需求/设计/测试等）后续通过 artifact_id 关联
--   2. ado_link + based_on_version 构成变更传播的基础
--   3. ado_event 采用 transactional outbox，保证业务操作与事件写入同事务
-- ----------------------------

CREATE TABLE IF NOT EXISTS ado_artifact
(
    artifact_id     BIGINT       NOT NULL COMMENT '工件ID',
    tenant_id       VARCHAR(20)  DEFAULT '000000' COMMENT '租户编号',
    project_id      BIGINT       NOT NULL COMMENT '项目ID',
    artifact_type   VARCHAR(32)  NOT NULL COMMENT '工件类型 requirement/story/page/contract/task/test_case/test_run/release/incident/decision/question/doc/mr',
    artifact_key    VARCHAR(128) NOT NULL COMMENT '人类可读编号，如 REQ-2026-018、AC-3',
    title           VARCHAR(500) NOT NULL COMMENT '标题',
    summary         TEXT COMMENT '摘要',
    status          VARCHAR(32)  DEFAULT 'draft' COMMENT '状态',
    owner_id        BIGINT       DEFAULT NULL COMMENT '负责人ID',
    owner_name      VARCHAR(64)  DEFAULT NULL COMMENT '负责人',
    current_version INT          DEFAULT 1 COMMENT '当前版本号',
    source_system   VARCHAR(64)  DEFAULT NULL COMMENT '来源系统 gitlab/figma/jira/...',
    source_ref      VARCHAR(255) DEFAULT NULL COMMENT '来源引用，如 MR !123',
    payload         LONGTEXT COMMENT '结构化扩展 JSON',
    search_text     TEXT COMMENT '检索文本',
    del_flag        CHAR(1)      DEFAULT '0' COMMENT '删除标志（0存在 1删除）',
    create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (artifact_id),
    UNIQUE KEY uk_ado_artifact_key (tenant_id, project_id, artifact_type, artifact_key),
    KEY idx_ado_artifact_project (tenant_id, project_id, artifact_type, status),
    KEY idx_ado_artifact_owner (tenant_id, owner_id)
) ENGINE = InnoDB COMMENT ='AI-DevOps 工件注册表';

CREATE TABLE IF NOT EXISTS ado_artifact_version
(
    version_id     BIGINT      NOT NULL COMMENT '版本ID',
    tenant_id      VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    artifact_id    BIGINT      NOT NULL COMMENT '工件ID',
    version_no     INT         NOT NULL COMMENT '版本号',
    snapshot       LONGTEXT COMMENT '完整快照 JSON',
    diff           LONGTEXT COMMENT '与上一版差异 JSON',
    change_summary VARCHAR(1000) DEFAULT NULL COMMENT '变更摘要',
    author_type    VARCHAR(16) DEFAULT 'human' COMMENT 'human/agent/system',
    author_id      BIGINT      DEFAULT NULL COMMENT '作者ID',
    author_name    VARCHAR(64) DEFAULT NULL COMMENT '作者',
    approved_by    BIGINT      DEFAULT NULL COMMENT '审批人',
    approved_at    DATETIME    DEFAULT NULL COMMENT '审批时间',
    create_dept    BIGINT      DEFAULT NULL COMMENT '创建部门',
    create_by      BIGINT      DEFAULT NULL COMMENT '创建者',
    create_time    DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by      BIGINT      DEFAULT NULL COMMENT '更新者',
    update_time    DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark         VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (version_id),
    UNIQUE KEY uk_ado_artifact_version (tenant_id, artifact_id, version_no),
    KEY idx_ado_artifact_version_artifact (tenant_id, artifact_id)
) ENGINE = InnoDB COMMENT ='AI-DevOps 工件版本表';

CREATE TABLE IF NOT EXISTS ado_link
(
    link_id          BIGINT      NOT NULL COMMENT '链接ID',
    tenant_id        VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    project_id       BIGINT      NOT NULL COMMENT '项目ID',
    from_artifact_id BIGINT      NOT NULL COMMENT '源工件ID',
    from_version     INT         DEFAULT NULL COMMENT '源工件版本（消费时版本）',
    to_artifact_id   BIGINT      NOT NULL COMMENT '目标工件ID',
    to_version       INT         DEFAULT NULL COMMENT '目标工件版本',
    relation         VARCHAR(32) NOT NULL COMMENT '关系 derives_from/implements/verifies/impacts/depends_on/documents/releases/causes/resolves/duplicates/supersedes',
    confidence       DECIMAL(5, 4) DEFAULT NULL COMMENT 'AI自动链接置信度 0-1',
    origin           VARCHAR(16) DEFAULT 'manual' COMMENT 'manual/auto/ai',
    status           VARCHAR(16) DEFAULT 'active' COMMENT 'active/stale/rejected',
    create_dept      BIGINT      DEFAULT NULL COMMENT '创建部门',
    create_by        BIGINT      DEFAULT NULL COMMENT '创建者',
    create_time      DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by        BIGINT      DEFAULT NULL COMMENT '更新者',
    update_time      DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark           VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (link_id),
    UNIQUE KEY uk_ado_link (tenant_id, from_artifact_id, to_artifact_id, relation),
    KEY idx_ado_link_from (tenant_id, from_artifact_id, status),
    KEY idx_ado_link_to (tenant_id, to_artifact_id, status)
) ENGINE = InnoDB COMMENT ='AI-DevOps 工件关系表';

CREATE TABLE IF NOT EXISTS ado_event
(
    event_id          BIGINT      NOT NULL COMMENT '事件ID',
    tenant_id         VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    project_id        BIGINT      DEFAULT NULL COMMENT '项目ID',
    event_type        VARCHAR(64) NOT NULL COMMENT '事件类型，如 artifact.updated',
    aggregate_type    VARCHAR(32) NOT NULL COMMENT '聚合类型',
    aggregate_id      BIGINT      NOT NULL COMMENT '聚合ID',
    aggregate_version INT         DEFAULT NULL COMMENT '聚合版本',
    actor_type        VARCHAR(16) DEFAULT 'human' COMMENT 'human/agent/system',
    actor_id          BIGINT      DEFAULT NULL COMMENT '操作者ID',
    payload           LONGTEXT COMMENT '事件载荷 JSON',
    trace_id          VARCHAR(64) DEFAULT NULL COMMENT '链路追踪ID',
    correlation_id    VARCHAR(64) DEFAULT NULL COMMENT '关联ID',
    status            VARCHAR(16) DEFAULT 'pending' COMMENT 'pending/published/failed',
    retry_count       INT         DEFAULT 0 COMMENT '重试次数',
    occurred_at       DATETIME    DEFAULT NULL COMMENT '发生时间',
    published_at      DATETIME    DEFAULT NULL COMMENT '发布时间',
    create_dept       BIGINT      DEFAULT NULL COMMENT '创建部门',
    create_by         BIGINT      DEFAULT NULL COMMENT '创建者',
    create_time       DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by         BIGINT      DEFAULT NULL COMMENT '更新者',
    update_time       DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark            VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (event_id),
    KEY idx_ado_event_status (tenant_id, status, occurred_at),
    KEY idx_ado_event_aggregate (tenant_id, aggregate_type, aggregate_id)
) ENGINE = InnoDB COMMENT ='AI-DevOps 领域事件 Outbox 表';

CREATE TABLE IF NOT EXISTS ado_handoff
(
    handoff_id       BIGINT      NOT NULL COMMENT '交接ID',
    tenant_id        VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    project_id       BIGINT      NOT NULL COMMENT '项目ID',
    handoff_type     VARCHAR(16) NOT NULL COMMENT '交接类型 H1~H7',
    from_role        VARCHAR(32) DEFAULT NULL COMMENT '交出方角色',
    to_role          VARCHAR(32) DEFAULT NULL COMMENT '接收方角色',
    artifact_ids     LONGTEXT COMMENT '交接工件ID列表 JSON',
    contract_version VARCHAR(32) DEFAULT NULL COMMENT '契约版本',
    checklist        LONGTEXT COMMENT '检查清单 JSON',
    status           VARCHAR(16) DEFAULT 'draft' COMMENT 'draft/checking/passed/rejected/signed',
    snapshot         LONGTEXT COMMENT '交接时的工件版本快照 JSON',
    signed_off_by    BIGINT      DEFAULT NULL COMMENT '签收人',
    signed_off_at    DATETIME    DEFAULT NULL COMMENT '签收时间',
    create_dept      BIGINT      DEFAULT NULL COMMENT '创建部门',
    create_by        BIGINT      DEFAULT NULL COMMENT '创建者',
    create_time      DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by        BIGINT      DEFAULT NULL COMMENT '更新者',
    update_time      DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark           VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (handoff_id),
    KEY idx_ado_handoff_project (tenant_id, project_id, handoff_type, status)
) ENGINE = InnoDB COMMENT ='AI-DevOps 交接契约表';

CREATE TABLE IF NOT EXISTS ado_context_package
(
    package_id        BIGINT      NOT NULL COMMENT '上下文包ID',
    tenant_id         VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    project_id        BIGINT      NOT NULL COMMENT '项目ID',
    task_id           BIGINT      NOT NULL COMMENT '任务工件ID',
    spec_artifact_id  BIGINT      NOT NULL COMMENT 'Spec 工件ID',
    spec_version      INT         DEFAULT NULL COMMENT 'Spec 版本',
    content           LONGTEXT COMMENT '上下文包内容 JSON',
    ai_brief          TEXT COMMENT 'AI 5分钟简报',
    status            VARCHAR(16) DEFAULT 'active' COMMENT 'active/invalidated',
    generated_at      DATETIME    DEFAULT NULL COMMENT '生成时间',
    create_dept       BIGINT      DEFAULT NULL COMMENT '创建部门',
    create_by         BIGINT      DEFAULT NULL COMMENT '创建者',
    create_time       DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by         BIGINT      DEFAULT NULL COMMENT '更新者',
    update_time       DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark            VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (package_id),
    KEY idx_ado_context_task (tenant_id, task_id, status),
    KEY idx_ado_context_spec (tenant_id, spec_artifact_id, spec_version)
) ENGINE = InnoDB COMMENT ='AI-DevOps 任务上下文包表';

CREATE TABLE IF NOT EXISTS ado_ai_run
(
    run_id            BIGINT      NOT NULL COMMENT 'AI运行ID',
    tenant_id         VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    project_id        BIGINT      DEFAULT NULL COMMENT '项目ID',
    agent_type        VARCHAR(64) NOT NULL COMMENT 'Agent类型',
    provider          VARCHAR(32) DEFAULT NULL COMMENT '模型供应商标识',
    model_name        VARCHAR(64) DEFAULT NULL COMMENT '模型名称',
    system_prompt     LONGTEXT COMMENT '系统提示词',
    input_text        LONGTEXT COMMENT '用户输入',
    output_text       LONGTEXT COMMENT '模型输出',
    status            VARCHAR(16) DEFAULT 'success' COMMENT 'success/failed',
    error_message     TEXT COMMENT '错误信息',
    prompt_tokens     INT         DEFAULT 0 COMMENT '输入Token',
    completion_tokens INT         DEFAULT 0 COMMENT '输出Token',
    total_tokens      INT         DEFAULT 0 COMMENT '总Token',
    latency_ms        BIGINT      DEFAULT 0 COMMENT '耗时毫秒',
    trace_id          VARCHAR(64) DEFAULT NULL COMMENT '链路追踪ID',
    create_dept       BIGINT      DEFAULT NULL COMMENT '创建部门',
    create_by         BIGINT      DEFAULT NULL COMMENT '创建者',
    create_time       DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by         BIGINT      DEFAULT NULL COMMENT '更新者',
    update_time       DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark            VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (run_id),
    KEY idx_ado_ai_run_project (tenant_id, project_id, agent_type, create_time)
) ENGINE = InnoDB COMMENT ='AI-DevOps AI 运行审计表';

-- ----------------------------
-- IPD 特性树 / 基线 / 受控变更（Phase 2 逐步启用）
-- ----------------------------

CREATE TABLE IF NOT EXISTS ado_feature
(
    feature_id      BIGINT       NOT NULL COMMENT '特性ID',
    tenant_id       VARCHAR(20)  DEFAULT '000000' COMMENT '租户编号',
    project_id      BIGINT       NOT NULL COMMENT '项目ID',
    parent_id       BIGINT       DEFAULT NULL COMMENT '父特性ID',
    feature_type    VARCHAR(32)  NOT NULL COMMENT 'epic/feature/system_feature/story',
    feature_key     VARCHAR(128) NOT NULL COMMENT '特性编号',
    title           VARCHAR(500) NOT NULL COMMENT '标题',
    description     TEXT COMMENT '描述',
    status          VARCHAR(32)  DEFAULT 'draft' COMMENT '状态',
    owner_id        BIGINT       DEFAULT NULL COMMENT '负责人ID',
    release_version VARCHAR(64)  DEFAULT NULL COMMENT '目标版本',
    baseline_id     BIGINT       DEFAULT NULL COMMENT '所属基线',
    sort_order      INT          DEFAULT 0 COMMENT '排序',
    create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (feature_id),
    UNIQUE KEY uk_ado_feature_key (tenant_id, project_id, feature_key),
    KEY idx_ado_feature_parent (tenant_id, project_id, parent_id),
    KEY idx_ado_feature_baseline (tenant_id, baseline_id)
) ENGINE = InnoDB COMMENT ='AI-DevOps 特性树';

CREATE TABLE IF NOT EXISTS ado_baseline
(
    baseline_id     BIGINT       NOT NULL COMMENT '基线ID',
    tenant_id       VARCHAR(20)  DEFAULT '000000' COMMENT '租户编号',
    project_id      BIGINT       NOT NULL COMMENT '项目ID',
    baseline_type   VARCHAR(32)  NOT NULL COMMENT 'product/release/iteration/milestone',
    baseline_name   VARCHAR(200) NOT NULL COMMENT '基线名称',
    baseline_version VARCHAR(64) NOT NULL COMMENT '基线版本',
    scope           LONGTEXT COMMENT '基线范围 JSON',
    snapshot        LONGTEXT COMMENT '工件快照 JSON',
    status          VARCHAR(16)  DEFAULT 'draft' COMMENT 'draft/frozen/archived',
    approved_by     BIGINT       DEFAULT NULL COMMENT '审批人',
    approved_at     DATETIME     DEFAULT NULL COMMENT '审批时间',
    create_dept     BIGINT       DEFAULT NULL COMMENT '创建部门',
    create_by       BIGINT       DEFAULT NULL COMMENT '创建者',
    create_time     DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_by       BIGINT       DEFAULT NULL COMMENT '更新者',
    update_time     DATETIME     DEFAULT NULL COMMENT '更新时间',
    remark          VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (baseline_id),
    UNIQUE KEY uk_ado_baseline_version (tenant_id, project_id, baseline_version),
    KEY idx_ado_baseline_status (tenant_id, project_id, status)
) ENGINE = InnoDB COMMENT ='AI-DevOps 基线';

CREATE TABLE IF NOT EXISTS ado_change_request
(
    change_request_id BIGINT      NOT NULL COMMENT '变更单ID',
    tenant_id         VARCHAR(20) DEFAULT '000000' COMMENT '租户编号',
    project_id        BIGINT      NOT NULL COMMENT '项目ID',
    baseline_id       BIGINT      DEFAULT NULL COMMENT '基线ID',
    artifact_id       BIGINT      DEFAULT NULL COMMENT '变更工件ID',
    change_type       VARCHAR(32) DEFAULT NULL COMMENT '需求/设计/契约/范围/进度',
    change_reason     TEXT COMMENT '变更原因',
    impact_analysis   LONGTEXT COMMENT '影响分析 JSON',
    status            VARCHAR(16) DEFAULT 'draft' COMMENT 'draft/reviewing/approved/rejected/implemented/closed',
    ccb_members       VARCHAR(500) DEFAULT NULL COMMENT 'CCB 成员',
    approved_by       BIGINT      DEFAULT NULL COMMENT '审批人',
    approved_at       DATETIME    DEFAULT NULL COMMENT '审批时间',
    implemented_at    DATETIME    DEFAULT NULL COMMENT '实施时间',
    create_dept       BIGINT      DEFAULT NULL COMMENT '创建部门',
    create_by         BIGINT      DEFAULT NULL COMMENT '创建者',
    create_time       DATETIME    DEFAULT NULL COMMENT '创建时间',
    update_by         BIGINT      DEFAULT NULL COMMENT '更新者',
    update_time       DATETIME    DEFAULT NULL COMMENT '更新时间',
    remark            VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (change_request_id),
    KEY idx_ado_change_project (tenant_id, project_id, status),
    KEY idx_ado_change_baseline (tenant_id, baseline_id)
) ENGINE = InnoDB COMMENT ='AI-DevOps 受控变更单';
