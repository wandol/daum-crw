package com.daum.crw.module;

import com.daum.crw.domain.Contents;
import com.daum.crw.domain.Source;
import com.daum.crw.dto.ArticleArea;
import com.daum.crw.dto.SiteName;
import com.daum.crw.exception.CrwErrorException;
import com.daum.crw.service.HeadLineService;
import com.daum.crw.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HeadLineModule {

    @Value("${chrome.driver.path}")
    private String chromeDirverPath;

    @Value("${article.write.date.default}")
    private String articleWriteDateDefault;

    @Autowired
    private HeadLineService headLineService;

    public List<String> getHeadLineLink(Source src) throws CrwErrorException {

        List<String> result = new ArrayList<>();
        //	크롬 드라이버 설정.
        System.setProperty("webdriver.chrome.driver", chromeDirverPath);
        // 크롬 브라우져 열기.
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.navigate().to(src.getStartUrl());
        WebDriverWait wait;

        try {

            By container = By.cssSelector("#news");
            wait = new WebDriverWait(driver, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(container));

            WebElement tap1 = driver.findElement(By.className("wrap_news"));
            wait.until(ExpectedConditions.visibilityOf(tap1));
            List<WebElement> tap1_one = tap1.findElements(By.xpath("//div[@class='news_prime news_tab1']/div/div/ul/li/a"));
            tap1_one.forEach(v -> log.info("tap1_one :: {}" , v.getAttribute("href")));
            List<WebElement> tap1_two = tap1.findElements(By.xpath("//div[@class='news_prime news_tab1']/div/ul/li/a"));
            tap1_two.forEach(v -> log.info( "tap1_two :: {}" ,v.getAttribute("href")));;

            driver.findElement(By.id("mediaNextBtn")).click();
            Thread.sleep(1000);

            WebElement tap2 = driver.findElement(By.className("wrap_news"));
            wait.until(ExpectedConditions.visibilityOf(tap2));
            List<WebElement> tap2_one = tap1.findElements(By.xpath("//div[@class='news_prime news_tab2']/div/div/ul/li/a"));
            tap2_one.forEach(v -> log.info("tap2_one :: {}" , v.getAttribute("href")));
            List<WebElement> tap2_two = tap1.findElements(By.xpath("//div[@class='news_prime news_tab2']/div/ul/li/a"));
            tap2_two.forEach(v -> log.info( "tap2_two :: {}" ,v.getAttribute("href")));

            driver.findElement(By.id("mediaNextBtn")).click();
            Thread.sleep(1000);

            WebElement tap3 = driver.findElement(By.className("wrap_news"));
            wait.until(ExpectedConditions.visibilityOf(tap2));
            List<WebElement> tap3_one = tap1.findElements(By.xpath("//div[@class='news_prime news_tab3']/div/div/ol/li/a"));
            tap3_one.forEach(v -> log.info("tap3_one :: {}" , v.getAttribute("href")));

        } catch (Exception e) {
            e.printStackTrace();
            driver.quit();
            throw new CrwErrorException("DAUM HOME HEADLINE LINK 수집 에러. {}", e);
        }finally {
            driver.quit();
        }

        return result;
    }

    public void getDetailArticle(Source src, List<String> list, boolean postFlag, ArticleArea area) throws CrwErrorException {

        List<Contents> result = new ArrayList<>();
        List<String> pks = new ArrayList<>();
        Document doc;

        try {
            for (String url : list) {
                doc = Jsoup.connect(url).get();

                //  PK 구하기
                String pk_v = new CommonUtil().getEncMD5(url + doc.getElementsByAttributeValue("property", src.getArticleTitleXpth() ).attr("content"));

                //  pk 리스트
                //  게시 되어 있는지 안되어 있는지 판단하는 기준이 되는 pk를 list로 가지고 있는다.
                pks.add(pk_v);

                //  기사 작성일 parse
                //  이와 같은 형식. :: 2020.10.26. 오전 9:35
                //  디폴트 값을 설정해놓음. properties
                //  기사 작성일 간혹 수정일이 포함되어 옴. ( 이에 배열 첫번째 요소로 parse )
                DateFormat dateParser = new SimpleDateFormat("yyyy.MM.dd. a KK:mm");
                Elements writeDtTag = doc.getElementsByClass(src.getArticleWriteDtXpth());
                String parseWDt = articleWriteDateDefault;
                if(writeDtTag.size() > 0){
                    String writeDt = writeDtTag.get(0).text();
                    parseWDt = writeDt.contains("오전") ? writeDt.replace("오전","AM") : writeDt.replace("오후","PM") ;
                }

                //  중복체크 및 게시 시간을 구하기 위해. 해당 pk로 먼저 수집원 데이터 체크.
                Contents contents = headLineService.findFirstBySiteNmAndArticlePkAndDelYn(SiteName.DAUM.toString(), pk_v,"N");

                //  중복된 contents는 담지 않음.
                if(contents == null){
                    Contents cont = Contents.builder()
                            .articleCategory(doc.getElementsByAttributeValue("property",src.getArticleCateXpth()).attr("content"))
                            .articleContents(doc.getElementsByClass(src.getArticleContXpth()).text())
                            .articleImgCaption(doc.getElementsByClass(src.getArticleImgContXpth()).stream().map(Element::text).collect(Collectors.joining("|")))
                            .articleMediaNm(doc.getElementsByAttributeValue("property",src.getArticleMediaNmXpth()).attr("content"))
                            // 해당 제목과 url의 텍스트를 합쳐서 md5를 구하고 pk로 함.
                            .articlePk(pk_v)
                            .articleTitle(doc.getElementsByAttributeValue("property", src.getArticleTitleXpth() ).attr("content"))
                            .articleUrl(url)
                            .articleWriteDt(LocalDateTime.ofInstant(dateParser.parse(parseWDt).toInstant(), ZoneId.of("Asia/Seoul")))
                            .articleWriter(doc.getElementsByAttributeValue("property", src.getArticleWriterXpth()).attr("content"))
                            .siteNm(SiteName.DAUM.toString())
                            .srcType(area.toString())
                            .delYn("N")
                            .articlePostStartDt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                            .articleCrwDt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                            .upDt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
                            .build();

                    result.add(cont);
                }
            }

            //  PK list
            pks.forEach(log::debug);

            //  상세 기사 데이터 DB 저장.
            if(result.size() > 0){
                List<Contents> saveCont = headLineService.saveAll(result);
                saveCont.forEach(vo -> log.debug("get Detail article save data :: {}" ,vo.toString()));
            }

            //  todo 해당 기사 게시 체크 ( 게시 되어 있는지 내려갔는지 체크 하는 로직. )
            //  한 헤드라인 수집 사이클에서 가져온 pks 들을 가지고  기존 contents_tb 에서  not in  pks   ..  and post_end_dt 가 null 인 데이터에
            //  post_end_dt 칼럼에 현재시각을 update 한다.
            if(postFlag && pks.size() > 0 ){
                int updatePostEndDt  = headLineService.updatePostEndDt(pks);
                log.info("update count end date :: {}" , updatePostEndDt);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new CrwErrorException("상세 기사 수집 에러. {}" , e );
        }
    }

    public List<String> getHeadLineAjaxLink(Source src, String url) throws CrwErrorException {

        List<String> result;

        System.setProperty("webdriver.chrome.driver", chromeDirverPath );
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        
        WebDriver wb = new ChromeDriver(options);
        wb.manage().window().maximize();
        wb.navigate().to(url);
        WebDriverWait wait;

        try {

            By container = By.cssSelector("#wrap");
            wait = new WebDriverWait(wb, 5);
            wait.until(ExpectedConditions.presenceOfElementLocated(container));

            //  더보기 클릭.
            wb.findElement(By.className("cluster_more_inner")).click();
            Thread.sleep(1000);

            //  더보기 클릭
            wb.findElement(By.className("cluster_more_inner")).click();
            Thread.sleep(1000);
            try {
                wb.findElement(By.className("cluster_more_inner")).click();
            }catch(ElementNotInteractableException e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);

            //  더보기 클릭하여 모든 화면 영역을 노출후 리스트가 있는 elements를 가져온다.
            WebElement TextElement = wb.findElement(By.className("list_body"));
            wait.until(ExpectedConditions.visibilityOf(TextElement));
            List<WebElement> elementList = TextElement.findElements(By.xpath(src.getCateHeadlineListXpath()));

            result = elementList.stream().map(v -> v.getAttribute("href")).collect(Collectors.toList());

        } catch (Exception e) {
            e.printStackTrace();
            wb.quit();
            throw new CrwErrorException("상세 기사 수집 에러. {}", e);
        }finally {
            wb.quit();
        }

        return result;
    }
}
