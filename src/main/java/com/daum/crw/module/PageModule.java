package com.daum.crw.module;

import com.daum.crw.domain.Source;
import com.daum.crw.exception.CrwErrorException;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * User: wandol<br/>
 * Date: 2020/10/30<br/>
 * Time: 6:54 오후<br/>
 * Desc:    페이징 으로 된 수집원 수집하는 모듈.
 */
@Slf4j
@Component
public class PageModule {

    @Value("${chrome.driver.path}")
    private String chromeDirverPath;

    @Value("${article.write.date.default}")
    private String articleWriteDateDefault;

    @Value("${crw.waiting.sec}")
    private int crwWaitingSec;

    public List<String> getPagingAllUrl(Source src, String crwUrl, String crwType) throws CrwErrorException {

        List<String> result = new ArrayList<>();
        System.setProperty("webdriver.chrome.driver", chromeDirverPath );
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        
        WebDriver wb = null;
        String linkX = "";
        String dateX = "";

        if("pol".equals(crwType)){
            linkX = src.getCatePagingListXpath();
            dateX = src.getCatePagingListDateXpath();
        }else{
            linkX = src.getCatePagingListXpath();
            dateX = src.getCatePagingListDateXpath();
        }

        try {
            //  today에 해당 되는 것만 수집하기 위한 flag
            boolean stopFlag = true;
            int page = 1;

            //
            while(stopFlag){
                String startUrl = crwUrl + page;
                wb = new ChromeDriver(options);
                wb.navigate().to(startUrl);

                List<WebElement> pageLinkList = wb.findElements(By.xpath(linkX));
                List<WebElement> pageDateList = wb.findElements(By.xpath(dateX));
                //  위 두개의 list는 사이즈가 같아야 한다.
                //  for문 돌며 날짜 체크 하여 2일전 텍스트를 만나면 종료 한다.
                for (int i = 0; i < pageLinkList.size(); i++) {
                    //  수집 목록 경과시간 수집한 텍스트.
                    String checkDate = pageDateList.get(i).getText().trim();
                    //  1일전 데이터만 수집한다.
                    if("1일전".equals(checkDate)){
                        stopFlag = false;
                    }else{
                        result.add(pageLinkList.get(i).getAttribute("href"));
                    }
                }

                //  page 증가.
                page++;
                //  2초간 페이징 이동 간격
                Thread.sleep(crwWaitingSec);

                //  브라우져 종료.
                wb.quit();
            }

        } catch (Exception e) {
            e.printStackTrace();
            wb.quit();
            throw new CrwErrorException("페이징 기사 수집 에러. {}", e);
        }finally {
            wb.quit();
        }

        return result;
    }

    public List<String> getPagingParamDateGetUrl(Source src) throws CrwErrorException {

        List<String> result = new ArrayList<>();
        System.setProperty("webdriver.chrome.driver", chromeDirverPath );
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        
        WebDriver wb = null;

        //  어제 날짜 'yyyyMMdd' 형식
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String addDateParam  = "&date=" + yesterday;
        String crwUrl = src.getStartUrl();

        //  어제 날짜를 구해 'yyyymmdd' url 파라미터로 추가한다.
        try {
            //  today에 해당 되는 것만 수집하기 위한 flag
            boolean stopFlag = true;
            int page = 1;

            while(stopFlag){
                //  수집 url make
                String startUrl = crwUrl + page + addDateParam;

                wb = new ChromeDriver(options);
                wb.navigate().to(startUrl);

                List<WebElement> pageLinkList = wb.findElements(By.xpath(src.getCatePagingListXpath()));

                //  pageLinkList  사이즈가 20 이 아니면 수집 종료
                if(pageLinkList.size() != 20) stopFlag = false;
                //  해당 url에 목록이 없으면 수집종료.
                if(pageLinkList.size() == 0) break;

                for (WebElement webElement : pageLinkList) {
                    result.add(webElement.getAttribute("href"));
                }

                //  page 증가.
                page++;
                //  2초간 페이징 이동 간격
                Thread.sleep(crwWaitingSec);

                //  브라우져 종료.
                wb.quit();
            }

        } catch (Exception e) {
            e.printStackTrace();
            wb.quit();
            throw new CrwErrorException("정치 홈 페이징 기사 수집 에러. {}", e);
        }finally {
            wb.quit();
        }

        return result;
    }
}
