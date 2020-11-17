package com.daum.crw.service.impl;

import com.daum.crw.domain.Contents;
import com.daum.crw.domain.Source;
import com.daum.crw.dto.SiteName;
import com.daum.crw.repository.ContRepository;
import com.daum.crw.repository.SrcRepository;
import com.daum.crw.service.HeadLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeadLineServiceImpl implements HeadLineService {

    @Autowired
    public SrcRepository lineSrcRepository;

    @Autowired
    public ContRepository contRepository;


    @Override
    public int updatePostEndDt(List<String> pks) {
        return contRepository.updatePostEndDt(pks);
    }

    @Override
    public List<Contents> saveAll(List<Contents> contentsList) {
        return contRepository.saveAll(contentsList);
    }

    @Override
    public Source findBySiteNmAndArticleCategoryAndUseYn(String sitem, String pk_v, String y) {
        return lineSrcRepository.findBySiteNmAndArticleCategoryAndUseYn(sitem,pk_v,y);
    }

    @Override
    public Contents findFirstBySiteNmAndArticlePkAndDelYn(String sitem, String pk_v, String n) {
        return contRepository.findFirstBySiteNmAndArticlePkAndDelYn(sitem,pk_v,n);
    }


}
