package com.daum.crw.schedule;

import com.daum.crw.exception.CrwErrorException;
import com.daum.crw.exception.EmptySourceInfoException;
import com.daum.crw.main.CrwMain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Schedule {

    @Autowired
    private CrwMain crwMain;

    /**
     *  다음 홈 헤드라인 영역 뉴스 3개 섹션 수집.
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    @Scheduled(cron = "#{@getHeadlineCycleCron}")
    public void HeadLineSchedule() throws EmptySourceInfoException, CrwErrorException {
        log.info("DAUM NEWS HOME, POLITICS, SOCIAL HEDLINE AREA schedule START =================================================================");
        long beforeTime = System.currentTimeMillis(); //코드 실행 전에 시간 받아오기
        crwMain.startHeadLineCrw();
        long afterTime = System.currentTimeMillis(); // 코드 실행 후에 시간 받아오기
        log.info("HEADLINE 소요 시간  :: {} 초", (afterTime - beforeTime) / 1000);
        log.info("DAUM NEWS HOME, POLITICS, SOCIAL HEDLINE AREA schedule END =================================================================");
    }

    /**
     * 다음 정치 홈 페이징 부분 하루치 기사 수집
     * 00:00 시에 시작하여 목록 부분에 '1일전' 이라는 글씨를 만나기 전까지 수집.음
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    @Scheduled(cron = "#{@getPolCycleCron}")
    public void polPagingSchedule() throws EmptySourceInfoException, CrwErrorException {
        log.info("DAUM POLITICS PAGING AREA schedule START =================================================================");
        long beforeTime = System.currentTimeMillis();
        crwMain.startPolPageCrw();
        long afterTime = System.currentTimeMillis();
        log.info("POLITICS 소요 시간  :: {} 초", (afterTime - beforeTime) / 1000);
        log.info("DAUM POLITICS PAGING AREA schedule END =================================================================");
    }

    /**
     * 다음 사회 홈 페이징 부분 하루치 기사 수집.
     * 00:00 시에 시작하여 목록 부분에 '1일전' 이라는 글씨를 만나기 전까지 수집.
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    @Scheduled(cron = "#{@getSocCycleCron}")
    public void socPagingSchedule() throws EmptySourceInfoException, CrwErrorException {

        log.info("DAUM SOCIAL PAGING AREA schedule START =================================================================");
        long beforeTime = System.currentTimeMillis();
        crwMain.startSocPageCrw();
        long afterTime = System.currentTimeMillis();
        log.info("SOCIAL 소요 시간  :: {} 초", (afterTime - beforeTime) / 1000);
        log.info("DAUM SOCIAL PAGING AREA schedule END =================================================================");

    }

    /**
     * 다음 오피니언 속보 페이징 부분 하루치 기사 수집.
     * 00:00 시에 시작하여 어제 날짜에 해당 하는 칼럼 수집.
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    @Scheduled(cron = "#{@getOpiCycleCron}")
    public void opiPagingSchedule() throws EmptySourceInfoException, CrwErrorException {

        log.info("DAUM OPINION PAGING AREA schedule START =================================================================");
        long beforeTime = System.currentTimeMillis();
        crwMain.startOpiPageCrw();
        long afterTime = System.currentTimeMillis();
        log.info("OPINION 소요 시간  :: {} 초", (afterTime - beforeTime) / 1000);
        log.info("DAUM OPINION PAGING AREA schedule END =================================================================");

    }
}
