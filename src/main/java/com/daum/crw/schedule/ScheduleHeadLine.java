package com.daum.crw.schedule;

import com.daum.crw.dto.ArticleCate;
import com.daum.crw.dto.SiteName;
import com.daum.crw.exception.CrwErrorException;
import com.daum.crw.exception.EmptySourceInfoException;
import com.daum.crw.main.CrwMain;
import com.daum.crw.repository.SrcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * User: wandol<br/>
 * Date: 2020/11/10<br/>
 * Time: 9:08 오후<br/>
 * Desc:    스케줄이 실행 될때 실행될 메소드 설정 및 다음 스케줄 주기 변경된 부분 체크 및 적용.
 */
@Component
@Slf4j
public class ScheduleHeadLine implements SchedulerInterface {

    @Autowired
    private SrcRepository srcRepository;

    private ScheduledFuture<?> future;

    private TaskScheduler scheduler;
    
    @Autowired
    public ScheduleHeadLine(@Lazy TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    @Autowired
    private CrwMain crwMain;

    @Override
    public void start() {
        future = scheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("NAVER NEWS HOME, POLITICS, SOCIAL HEDLINE AREA schedule START =================================================================");
                    long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
                    crwMain.startHeadLineCrw();
                    long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
                    log.info("HEADLINE 소요 시간  :: {} 초" , (afterTime - beforeTime)/1000 );
                    log.info("NAVER NEWS HOME, POLITICS, SOCIAL HEDLINE AREA schedule END =================================================================");
                } catch (EmptySourceInfoException e) {
                    e.printStackTrace();
                } catch (CrwErrorException e) {
                    e.printStackTrace();
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron  = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.HEADLINE.toString(), "Y").getCrwCycle();
                log.info("update HEADLINE cron value :: {}" , cron);
                return new CronTrigger(cron).nextExecutionTime(triggerContext);
            }
        });
    }

    @Override
    public void stop() {
        future.cancel(false);
    }


}
