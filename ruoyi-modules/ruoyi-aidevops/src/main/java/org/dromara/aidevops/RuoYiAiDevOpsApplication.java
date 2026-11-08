package org.dromara.aidevops;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI-DevOps 研发协同平台
 *
 * @author ruoyi
 */
@EnableDubbo
@EnableScheduling
@SpringBootApplication
public class RuoYiAiDevOpsApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiAiDevOpsApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  AI-DevOps 平台启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }

}
