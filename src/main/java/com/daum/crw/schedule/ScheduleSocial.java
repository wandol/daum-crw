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
public class ScheduleSocial implements SchedulerInterface {

    @Autowired
    private SrcRepository srcRepository;

    private ScheduledFuture<?> future;

    private TaskScheduler scheduler;
    
    @Autowired
    public ScheduleSocial(@Lazy TaskScheduler scheduler) {
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
                     *  네이버 사회 홈 페이징 부분 하루치 기사 수집.
                     *  00:00 시에 시작하여 목록 부분에 '1일전' 이라는 글씨를 만나기 전까지 수집.
                     * @throws EmptySourceInfoException
                     * @throws CrwErrorException
                     */

                    log.info("NAVER SOCIAL PAGING AREA schedule START =================================================================");
                    long beforeTime = System.currentTimeMillis();
                    crwMain.startSocPageCrw();
                    long afterTime = System.currentTimeMillis();
                    log.info("SOCIAL 소요 시간  :: {} 초" , (afterTime - beforeTime)/1000 );
                    log.info("NAVER SOCIAL PAGING AREA schedule END =================================================================");

                } catch (EmptySourceInfoException e) {
                    e.printStackTrace();
                } catch (CrwErrorException e) {
                    e.printStackTrace();
                }
            }
        }, new Trigger() {
            @Override
            public Date nextExecutionTime(TriggerContext triggerContext) {
                String cron  = srcRepository.findBySiteNmAndArticleCategoryAndUseYn(SiteName.NAVER.toString(), ArticleCate.SOCIAL.toString(), "Y").getCrwCycle();
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
