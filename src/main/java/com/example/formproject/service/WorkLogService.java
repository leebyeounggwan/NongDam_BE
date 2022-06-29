package com.example.formproject.service;

import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.dto.response.LineChartDataDto;
import com.example.formproject.dto.response.LineChartDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.entity.Member;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.repository.WorkLogRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class WorkLogService {
    private final WorkLogRepository workLogRepository;
    private final CropRepository cropRepository;
    public LineChartDto getHarvestData(Member m){
        LineChartDto ret = new LineChartDto();
        LocalDateTime[] times = workLogRepository.findTimesOfHarvest(m.getId());
        for(Crop c : m.getCrops()) {
            List<Object[]> datas = workLogRepository.selectHarvest(m.getId(),c.getId());
            LineChartDataDto dto = LineChartDataDto.builder().name(c.getName()).build();
            LocalDate startTime = times[1].toLocalDate();
            LocalDate endTime = times[0].toLocalDate();
            while(startTime.isBefore(endTime)|| (startTime.getYear() == endTime.getYear() && startTime.getMonthValue() == endTime.getMonthValue())){
                int year = startTime.getYear();
                int month = startTime.getMonthValue();
                ret.addLabel(startTime.getYear()+"-"+startTime.getMonthValue()+"01");
                int data = (int) datas.stream().filter(e->(int)e[0]==year&&(int)e[1]== month).findFirst().orElse(new Object[]{startTime.getYear(),startTime.getMonthValue(),0})[2];
                dto.addData(data);
                startTime = startTime.plusMonths(1L);
            }
            ret.addData(dto);
        }
        return  ret;
    }
    public void saveWork(Member member,WorkLogRequestDto dto, List<MultipartFile> files){
        workLogRepository.save(dto.build(member,cropRepository));
    }

}
