package com.daum.crw.service;


import com.daum.crw.domain.Contents;
import com.daum.crw.domain.Source;
import com.daum.crw.dto.SiteName;

import java.util.List;

public interface HeadLineService {

    int updatePostEndDt(List<String> pks);

    List<Contents> saveAll(List<Contents> contentsList);

    Source findBySiteNmAndArticleCategoryAndUseYn(String sitem, String toString1, String y);

    Contents findFirstBySiteNmAndArticlePkAndDelYn(String toString, String pk_v, String n);
}
