package org.dromara.aidevops.constant;

/**
 * AI-DevOps 常量
 *
 * @author ruoyi
 */
public interface AdoConstants {

    /**
     * 工件类型
     */
    String ARTIFACT_REQUIREMENT = "requirement";
    String ARTIFACT_STORY = "story";
    String ARTIFACT_PAGE = "page";
    String ARTIFACT_CONTRACT = "contract";
    String ARTIFACT_TASK = "task";
    String ARTIFACT_TEST_CASE = "test_case";
    String ARTIFACT_TEST_RUN = "test_run";
    String ARTIFACT_RELEASE = "release";
    String ARTIFACT_INCIDENT = "incident";
    String ARTIFACT_DECISION = "decision";
    String ARTIFACT_QUESTION = "question";
    String ARTIFACT_DOC = "doc";
    String ARTIFACT_MR = "mr";

    /**
     * 关系类型
     */
    String REL_DERIVES_FROM = "derives_from";
    String REL_IMPLEMENTS = "implements";
    String REL_VERIFIES = "verifies";
    String REL_IMPACTS = "impacts";
    String REL_DEPENDS_ON = "depends_on";
    String REL_DOCUMENTS = "documents";
    String REL_RELEASES = "releases";
    String REL_CAUSES = "causes";
    String REL_RESOLVES = "resolves";
    String REL_DUPLICATES = "duplicates";
    String REL_SUPERSEDES = "supersedes";

    /**
     * 事件状态
     */
    String EVENT_PENDING = "pending";
    String EVENT_PUBLISHED = "published";
    String EVENT_FAILED = "failed";

    /**
     * 角色类型
     */
    String ACTOR_HUMAN = "human";
    String ACTOR_AGENT = "agent";
    String ACTOR_SYSTEM = "system";

    /**
     * 工件状态
     */
    String STATUS_ACTIVE = "active";
    String STATUS_INVALIDATED = "invalidated";

    /**
     * 交接状态
     */
    String HANDOFF_DRAFT = "draft";
    String HANDOFF_CHECKING = "checking";
    String HANDOFF_PASSED = "passed";
    String HANDOFF_REJECTED = "rejected";
    String HANDOFF_SIGNED = "signed";

}
