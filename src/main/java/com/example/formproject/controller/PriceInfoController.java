package com.example.formproject.controller;

import com.example.formproject.dto.response.PriceInfoResponseDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.PriceInfoService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PriceInfoController {

    private final PriceInfoService priceInfoService;

    @GetMapping("/marketprice")
    public PriceInfoResponseDto PriceInfo(String productClsCode, String gradeRank, int cropId,
                          @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException {
        //정보 받은걸 여기서 풀어서 통합 DTO로 만들고 넘겨준다.

        return priceInfoService.priceInfo(productClsCode,gradeRank, cropId, memberdetail);
    }
}
