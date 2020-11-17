package com.daum.crw.repository;

import com.daum.crw.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SrcRepository extends JpaRepository<Source,String> {

    Source findBySiteNmAndArticleCategoryAndUseYn(String naver, String headline, String y);

}