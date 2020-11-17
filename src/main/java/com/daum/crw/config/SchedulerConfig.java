package com.daum.crw.config;

import com.daum.crw.dto.ArticleCate;
import com.daum.crw.dto.SiteName;
import com.daum.crw.repository.SrcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
@Slf4j
public class SchedulerConfig implements SchedulingConfigurer {

    @Autowired
    private SrcRepository srcRepository;

    @Value("${thread.pool.size}")
    private int POOL_SIZE;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(POOL_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("SCHEDULE-T-");
        threadPoolTaskScheduler.initialize();

        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler);


    }

    /**
     *  headline 수집 스케줄 cron 설정 가져오기.
     * @return
     */
    @Bean
    public String getHeadlineCycleCron(){
        String cycle = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.HEADLINE.toString(), "Y").getCrwCycle();
        log.info("====== get Schedule cron HeadLine :: {}",cycle);
        return cycle;
    }

    /**
     *  politics 수집 스케출 cron 설정 가져오기.
     * @return
     */
    @Bean
    public String getPolCycleCron(){
        String cycle = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.POLITICS.toString(), "Y").getCrwCycle();
        log.info("====== get Schedule cron Politics :: {}",cycle);
        return cycle;
    }

    /**
     *  social 수집 스케출 cron 설정 가져오기.
     * @return
     */
    @Bean
    public String getSocCycleCron(){
        String cycle = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.SOCIAL.toString(), "Y").getCrwCycle();
        log.info("====== get Schedule cron Social :: {}",cycle);
        return cycle;
    }

    /**
     *  opinion 수집 스케출 cron 설정 가져오기.
     * @return
     */
    @Bean
    public String getOpiCycleCron(){
        String cycle = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.OPINION.toString(), "Y").getCrwCycle();
        log.info("====== get Schedule cron Opinion :: {}",cycle);
        return cycle;
    }
}