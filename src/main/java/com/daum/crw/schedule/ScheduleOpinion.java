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
public class ScheduleOpinion implements SchedulerInterface {

    @Autowired
    private SrcRepository srcRepository;

    private ScheduledFuture<?> future;

    private TaskScheduler scheduler;
    
    @Autowired
    public ScheduleOpinion(@Lazy TaskScheduler scheduler) {
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
                    /**
                     *  네이버 오피니언 속보 페이징 부분 하루치 기사 수집.
                     *  00:00 시에 시작하여 어제 날짜에 해당 하는 칼럼 수집.
                     */

                    log.info("NAVER OPINION PAGING AREA schedule START =================================================================");
                    long beforeTime = System.currentTimeMillis();
                    crwMain.startOpiPageCrw();
                    long afterTime = System.currentTimeMillis();
                    log.info("OPINION 소요 시간  :: {} 초" , (afterTime - beforeTime)/1000 );
                    log.info("NAVER OPINION PAGING AREA schedule END =================================================================");

                } catch (EmptySourceInfoException e) {
                    e.printStackTrace();
                } catch (CrwErrorException e) {
                    e.printStackTrace();
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron  = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.OPINION.toString(), "Y").getCrwCycle();
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
