package com.example.formproject.service;

import com.example.formproject.dto.response.CropCategoryDto;
import com.example.formproject.dto.response.PersonalCropDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.enums.CropCategoryCode;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.security.MemberDetail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Service
public class CropService {
    private final CropRepository cropRepository;

    public List<CropCategoryDto> findAllData(){
        List<Crop> crops = cropRepository.findAll();
        List<CropCategoryDto> ret = new ArrayList<>();

        CropCategoryDto foodCrop = new CropCategoryDto();
        CropCategoryDto vegitable = new CropCategoryDto();
        CropCategoryDto fruits = new CropCategoryDto();
        foodCrop.setCategory(CropCategoryCode.식량작물.name());
        vegitable.setCategory(CropCategoryCode.채소류.name());
        fruits.setCategory(CropCategoryCode.과일류.name());

        for(Crop c : crops){
            switch(c.getCategory()){
                case(100):
                    foodCrop.addCrop(c);
                    break;
                case(200):
                    vegitable.addCrop(c);
                    break;
                case(400):
                    fruits.addCrop(c);
                    break;
            }
        }
        ret.add(foodCrop);
        ret.add(vegitable);
        ret.add(fruits);
        return ret;
    }

    public List<PersonalCropDto> getPersonalCrop(MemberDetail memberdetail) {
        List<Crop> cropList = cropRepository.findAllByMember(memberdetail.getMember());
        List<PersonalCropDto> list = new ArrayList<>();
        for (Crop c : cropList) {
            PersonalCropDto personalCropDto = new PersonalCropDto();
            personalCropDto.setId(c.getId());
            personalCropDto.setName(c.getName());
            list.add(personalCropDto);
        }
        return list;
    }
}