# ruoyi-aidevops

AI-DevOps 研发协同平台的核心基础设施：工件图、变更传播、交接契约、任务上下文包，以及 AI 需求澄清、结构化 Spec 校验与 AC 覆盖率。

## 1. 当前能力

- 工件注册与版本快照：`ado_artifact` + `ado_artifact_version`
- 工件关系：`ado_link`，支持 `derives_from`、`implements`、`verifies`、`impacts` 等关系
- 工件追溯：按上游/下游/双向 BFS 查询，返回节点与边
- 领域事件：`ado_event` transactional outbox + 定时中继
- 交接契约：H1-H7 交接单、检查清单、版本快照、签收
- 任务上下文包：从 Spec 版本与关联工件自动组装
- 变更传播：识别下游仍基于旧版本的工件，支持下游 ack
- 一致性巡检：需求无用例、契约无任务、孤立工件、陈旧链接
- AI 需求澄清：需求 → 结构化 Spec 草稿（支持 mock / OpenAI 兼容模型）
- Spec 校验：JSON Schema 校验
- AC 覆盖率：按 `verifies` 关系 + 测试用例 `acIds` 计算覆盖

## 2. 初始化

1. 执行 `script/sql/ai-devops-core.sql`
2. 执行 `script/sql/ai-devops-core-menu.sql`（可选，注册权限）
3. 在 Nacos 增加 `ruoyi-aidevops.yml`（参考 `script/config/nacos/ruoyi-aidevops.yml`）
4. 启动 `RuoYiAiDevOpsApplication`，端口默认 `9215`

### AI 配置

```yaml
ai-devops:
  ai:
    provider: mock   # mock / openai
    base-url: https://api.deepseek.com/v1
    api-key:          # 使用 openai 时填写
    model: deepseek-chat
    timeout-seconds: 60
    max-tokens: 4096
    temperature: 0.2
```

- `provider=mock`：不需要密钥，返回确定性 Spec 草稿，用于打通流程。
- `provider=openai`：任意 OpenAI 兼容接口（DeepSeek / Qwen / vLLM / Ollama）。
- 所有 AI 调用写入 `ado_ai_run`，记录模型、Prompt、输出、Token、耗时和状态。

## 3. API

### 工件

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/ai-devops/artifact/list` | 分页查询 |
| GET | `/ai-devops/artifact/{artifactId}` | 详情 |
| GET | `/ai-devops/artifact/key` | 按项目+类型+编号查询 |
| POST | `/ai-devops/artifact` | 新建（自动生成 v1） |
| PUT | `/ai-devops/artifact` | 更新（自动递增版本并记录快照） |
| DELETE | `/ai-devops/artifact/{ids}` | 删除 |
| GET | `/ai-devops/artifact/{artifactId}/versions` | 版本历史 |

### 关系与追溯

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/ai-devops/link` | 新增/更新关系 |
| DELETE | `/ai-devops/link/{ids}` | 删除关系 |
| GET | `/ai-devops/link/list` | 查询某工件的关系 |
| PUT | `/ai-devops/link/ack/{linkId}` | 下游确认已消费上游最新版本 |
| GET | `/ai-devops/traceability/{artifactId}` | 上游/下游追溯 |
| GET | `/ai-devops/change-impact/{artifactId}` | 变更影响分析 |
| GET | `/ai-devops/consistency/{projectId}` | 一致性巡检 |

### 交接与上下文包

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/ai-devops/handoff/list` | 交接列表 |
| GET | `/ai-devops/handoff/{id}` | 交接详情 |
| POST | `/ai-devops/handoff` | 创建交接（自动快照工件版本） |
| PUT | `/ai-devops/handoff/sign/{id}` | 签收/驳回 |
| POST | `/ai-devops/context-package/build` | 构建任务上下文包 |
| GET | `/ai-devops/context-package/task/{taskId}` | 查询任务当前上下文包 |

### AI 与 Spec

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/ai-devops/agent/requirement/clarify` | AI 需求澄清，返回结构化 Spec 草稿 |
| GET | `/ai-devops/spec/schema` | 获取 Spec JSON Schema |
| POST | `/ai-devops/spec/validate` | 校验 Spec |
| GET | `/ai-devops/spec/{artifactId}/ac-coverage` | 计算 AC 覆盖率 |

## 4. 资源

- `src/main/resources/spec/spec-schema.json`：结构化 Spec JSON Schema
- `src/main/resources/spec/handoff-checklists.json`：H1-H7 交接检查清单

## 5. 下一步

- Prompt 版本管理与多 Agent 编排
- 测试设计 Agent：AC → 用例草稿 → 自动建立 verifies 关系
- 变更传播任务化：自动创建变更任务、失效上下文包、重开门禁
- GitLab/Jenkins Webhook 自动建图
- 工件图、影响分析、一致性巡检前端页面
- Agent 评测集与模型路由
