package com.daum.crw.main;


import com.daum.crw.domain.Source;
import com.daum.crw.dto.ArticleArea;
import com.daum.crw.dto.ArticleCate;
import com.daum.crw.dto.SiteName;
import com.daum.crw.exception.CrwErrorException;
import com.daum.crw.exception.EmptySourceInfoException;
import com.daum.crw.module.HeadLineModule;
import com.daum.crw.module.PageModule;
import com.daum.crw.service.HeadLineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class CrwMain {

    @Autowired
    private HeadLineService headLineService;

    @Autowired
    private HeadLineModule headLineModule;

    @Autowired
    private PageModule pageModule;

    /**
     *  다음 메인 홈 뉴스 헤드라인 뉴스 3개 영역 수집.
     *  영역이 ajax으로 불러 오기에 2번 클릭 적용.
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    public void startHeadLineCrw() throws EmptySourceInfoException, CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.DAUM.toString(), ArticleCate.HEADLINE.toString(),"Y");

        if (src == null) {
            throw new EmptySourceInfoException(" 수집원이 없습니다. ");
        }
        log.info("DAUM HEADLINE SOURCE INFO :: {}" , src);

        //  TODO 다음 뉴스 홈 헤드라인,정치,사회 영역 link 수집
        List<String> newsHomHeadLineLinkList = headLineModule.getHeadLineLink(src);
        newsHomHeadLineLinkList.forEach(url -> log.info("DAUM news home headline url list :: {}",url));

        //  todo 다음 뉴스 홈 urls 상세 기사 수집. 게시 시간 수집.
        headLineModule.getDetailArticle(src, newsHomHeadLineLinkList, true , ArticleArea.HOMEHEADLINE);

    }

    /**
     *  정치 홈 페이징 부분 수집.
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    public void startPolPageCrw() throws EmptySourceInfoException, CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기음
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.DAUM.toString(), ArticleCate.POLITICS.toString(), "Y");

        if (src == null) {
            throw new EmptySourceInfoException(" 수집원이 없습니다. ");
        }
        log.info("DAUM POLITICS SOURCE INFO :: {}" , src.toString());

        //  todo 다음 뉴스 홈 헤드라인 쪽 link 수집.  ( 더보기 버튼 클릭 )
        List<String> polHomHeadLineLinkList = headLineModule.getHeadLineAjaxLink(src, src.getStartUrl());
        polHomHeadLineLinkList.forEach(url -> log.info("DAUM politics home headline url list :: {}",url));

        //  todo 정치,사회 헤드라인 영역 상세기사 수집 후 중복확인 및 DB 저장.
        headLineModule.getDetailArticle(src, polHomHeadLineLinkList, false, ArticleArea.POLHEADLINE);


        //  todo 오늘 날짜에 생성된 기사만 페이징 처리하여 link 수집.
        List<String> urlList = pageModule.getPagingAllUrl(src,src.getStartUrl(),"pol");
        urlList.forEach(url -> log.info("DAUM politics home paging url list :: {} ",url));

        //  todo 해당 url list로 상세 기사 수집하여  중복체크 및 DB 저장.
        headLineModule.getDetailArticle(src, urlList, false, ArticleArea.POLPAGING);

    }

    /**
     *  사회 홈 페이징 부분 수집 .
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    public void startSocPageCrw() throws EmptySourceInfoException, CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.DAUM.toString(), ArticleCate.SOCIAL.toString(), "Y");

        if (src == null) {
            throw new EmptySourceInfoException(" 수집원이 없습니다. ");
        }
        log.info("DAUM SOCIAL SOURCE INFO :: {}" , src.toString());

        //  todo 다음 사회 홈 헤드라인 쪽 link 수집.  ( 더보기 버튼 클릭 )
        List<String> socHomHeadLineLinkList = headLineModule.getHeadLineAjaxLink(src,src.getStartUrl());
        socHomHeadLineLinkList.forEach(url -> log.info("DAUM social home headline url list :: {}",url));
        //  todo 정치,사회 헤드라인 영역 상세기사 수집 후 중복확인 및 DB 저장.
        headLineModule.getDetailArticle(src, socHomHeadLineLinkList, false, ArticleArea.SOCHEADLINE);

        //  todo 어제 날짜에 생성된 기사만 페이징 처리하여 link 수집.
        List<String> urlList = pageModule.getPagingAllUrl(src,src.getStartUrl(),"soc");
        urlList.forEach(url -> log.info("DAUM social home paging url list :: {} ",url));

        //  todo 해당 url list로 상세 기사 수집하여  중복체크 및 DB 저장.
        headLineModule.getDetailArticle(src, urlList, false, ArticleArea.SOCPAGING);
    }

    /**
     *  전체 칼럼. -> 속보  페이징 부분 수집 .
     *
     * @throws EmptySourceInfoException
     * @throws CrwErrorException
     */
    public void startOpiPageCrw() throws EmptySourceInfoException, CrwErrorException {

        //  TODO 수집원 설정 값 DB에서 가져오기
        Source src = headLineService.findBySiteNmAndArticleCategoryAndUseYn(SiteName.DAUM.toString(), ArticleCate.OPINION.toString(), "Y");

        if (src == null) {
            throw new EmptySourceInfoException(" 수집원이 없습니다. ");
        }
        log.info("DAUM OPINION SOURCE INFO :: {}" , src.toString());

        //  todo 오피니언 속보 영역 페이징 부분 페이징 처리 하여 LINK 수집
        List<String> urlList = pageModule.getPagingParamDateGetUrl(src);
        urlList.forEach(url -> log.info("DAUM opinion paging url list :: {} ",url));

        //  todo 해당 url list로 상세 기사 수집하여  중복체크 및 DB 저장.
        headLineModule.getDetailArticle(src, urlList, false, ArticleArea.OPINIONPAGING);
    }
}
