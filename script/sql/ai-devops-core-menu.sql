-- ----------------------------
-- AI-DevOps 核心菜单与权限（Phase 0）
-- 说明：
--   1. 依赖 ai-devops-requirement.sql 中的 6100 目录
--   2. 页面菜单先隐藏（visible=1），前端工作台就绪后再改为显示
--   3. 使用 INSERT IGNORE，重复执行不会报错
-- ----------------------------

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6301, '工件图', 6100, 4, 'artifact-graph', 'ai-devops/graph/index', '', 1, 0, 'C', '1', '0',
        'ai:devops:artifact:list', 'tree-table', 103, 1, NOW(), NULL, NULL, 'AI-DevOps工件图与追溯');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6311, '工件列表', 6301, 1, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:artifact:list', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6312, '工件查询', 6301, 2, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:artifact:query', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6313, '工件新增', 6301, 3, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:artifact:add', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6314, '工件修改', 6301, 4, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:artifact:edit', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6315, '工件删除', 6301, 5, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:artifact:remove', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6316, '关系列表', 6301, 6, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:link:list', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6317, '关系新增', 6301, 7, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:link:add', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6318, '关系删除', 6301, 8, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:link:remove', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6319, '追溯查询', 6301, 9, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:trace:query', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6326, '关系确认', 6301, 10, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:link:ack', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6327, '影响分析', 6301, 11, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:change:impact', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6328, '一致性巡检', 6301, 12, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:consistency:check', '#', 103, 1, NOW(), NULL, NULL, '');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6302, '交接与上下文', 6100, 5, 'collaboration', 'ai-devops/collaboration/index', '', 1, 0, 'C', '1', '0',
        'ai:devops:handoff:list', 'guide', 103, 1, NOW(), NULL, NULL, 'AI-DevOps交接契约与任务上下文包');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6320, '交接列表', 6302, 1, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:handoff:list', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6321, '交接查询', 6302, 2, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:handoff:query', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6322, '交接新增', 6302, 3, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:handoff:add', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6323, '交接签收', 6302, 4, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:handoff:sign', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6324, '上下文查询', 6302, 5, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:context:query', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6325, '上下文构建', 6302, 6, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:context:build', '#', 103, 1, NOW(), NULL, NULL, '');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6303, 'AI 需求助手', 6100, 6, 'assistant', 'ai-devops/assistant/index', '', 1, 0, 'C', '0', '0',
        'ai:devops:agent:requirement:clarify', 'magic', 103, 1, NOW(), NULL, NULL, 'AI需求澄清与Spec助手');

INSERT IGNORE INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query_param, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
VALUES (6330, '需求澄清', 6303, 1, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:agent:requirement:clarify', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6331, 'Spec校验', 6303, 2, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:spec:validate', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6332, 'AC覆盖率', 6303, 3, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:spec:coverage', '#', 103, 1, NOW(), NULL, NULL, ''),
       (6333, 'Spec Schema', 6303, 4, '', '', '', 1, 0, 'F', '0', '0', 'ai:devops:spec:schema', '#', 103, 1, NOW(), NULL, NULL, '');
