package com.example.formproject.controller;

import com.example.formproject.dto.request.PriceInfoRequestDto;
import com.example.formproject.dto.request.PriceInfoRequestDto2;
import com.example.formproject.dto.response.DailyPriceInfoDto;
import com.example.formproject.dto.response.PriceInfoDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.security.MemberDetail;
import com.example.formproject.service.PriceInfoService;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PriceInfoController {

    private final PriceInfoService priceInfoService;
    private final CropRepository cropRepository;

    @PostMapping("/todaymarketprice")
    public DailyPriceInfoDto todayPriceInfo(@RequestBody PriceInfoRequestDto2 priceInfoRequestDto2,
                                            @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException, java.text.ParseException {

        Crop crop = cropRepository.findById(priceInfoRequestDto2.getCropId()).orElseThrow();
        System.out.println(crop.getCategory());
        System.out.println(crop.getType());
        System.out.println(crop.getKind());
        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceInfoRequestDto2, memberdetail);
        System.out.println(memberdetail.getMember().getCountryCode());
        return priceInfoService.dailyPrice(priceInfoRequestDto);
    }

    @PostMapping("/marketprice")
    public List<PriceInfoDto> priceInfo(@RequestBody PriceInfoRequestDto2 priceInfoRequestDto2,
                                        @AuthenticationPrincipal MemberDetail memberdetail) throws IOException, ParseException, java.text.ParseException {
        Crop crop = cropRepository.findById(priceInfoRequestDto2.getCropId()).orElseThrow();
        System.out.println(crop.getCategory());
        System.out.println(crop.getType());
        System.out.println(crop.getKind());

        PriceInfoRequestDto priceInfoRequestDto = new PriceInfoRequestDto(crop, priceInfoRequestDto2, memberdetail);
        List<PriceInfoDto> reponseDto = (priceInfoRequestDto.getData().equals("month")) ? priceInfoService.monthlyPrice(priceInfoRequestDto) : priceInfoService.yearlyPrice(priceInfoRequestDto);

        return reponseDto;
    }
}
