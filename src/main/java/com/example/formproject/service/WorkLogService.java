package com.example.formproject.service;

import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.dto.response.LineChartDataDto;
import com.example.formproject.dto.response.LineChartDto;
import com.example.formproject.dto.response.WorkLogResponseDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.entity.Images;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.WorkLog;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.repository.WorkLogRepository;
import com.example.formproject.security.MemberDetail;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Getter
public class WorkLogService {
    private final WorkLogRepository workLogRepository;
    private final CropRepository cropRepository;
    private final AwsS3Service s3Service;

    public LineChartDto getHarvestData(Member m) {
        LineChartDto ret = new LineChartDto();
        List<LocalDate[]> times = workLogRepository.findTimesOfHarvest(m.getId());
        for (Crop c : m.getCrops()) {
            List<Object[]> datas = workLogRepository.selectHarvest(m.getId(), c.getId());
            LineChartDataDto dto = LineChartDataDto.builder().name(c.getName()).build();
            LocalDate startTime = times.get(0)[1];
            LocalDate endTime = times.get(0)[0];
            while (startTime.isBefore(endTime) || (startTime.getYear() == endTime.getYear() && startTime.getMonthValue() == endTime.getMonthValue())) {
                int year = startTime.getYear();
                int month = startTime.getMonthValue();
                ret.addLabel(startTime.getYear() + "-" + startTime.getMonthValue() + "01");
                int data = (int) datas.stream().filter(e -> (int) e[0] == year && (int) e[1] == month).findFirst().orElse(new Object[]{startTime.getYear(), startTime.getMonthValue(), 0})[2];
                dto.addData(data);
                startTime = startTime.plusMonths(1L);
            }
            ret.addData(dto);
        }
        return ret;
    }
    public LineChartDto getWorkTimeData(Member m){
        LineChartDto ret = new LineChartDto();
        int year = LocalDate.now().getYear();
        List<Object[]> thisYear = workLogRepository.selectWorkTimeofYear(m.getId(),year);
        List<Object[]> preYear = workLogRepository.selectWorkTimeofYear(m.getId(),year-1);
        ret.addLabel(Integer.toString(year-1));
        ret.addLabel(Integer.toString(year));
        for(int idx = 1; idx < 5;idx++){
            int finalIdx = idx;
            LineChartDataDto data = LineChartDataDto.builder().name(idx+"분기").build();
            Object[] preYearData = preYear.stream().filter(e->Integer.parseInt(e[1].toString()) == finalIdx).findFirst().orElse(null);
            int number1 = preYearData ==null?0:Integer.parseInt(preYearData[2].toString());
            data.addData(number1);
            Object[] thisYearData = thisYear.stream().filter(e->Integer.parseInt(e[1].toString()) == finalIdx).findFirst().orElse(null);
            int number2 = thisYearData ==null?0:Integer.parseInt(thisYearData[2].toString());
            data.addData(number2);
            ret.addData(data);
        }
        return ret;
    }

    @Transactional
    public void createWorkLog(Member member, WorkLogRequestDto dto, List<MultipartFile> files) {
        List<String> fileList = new ArrayList<>();
        WorkLog workLog = dto.build(member, cropRepository);
        for (MultipartFile file : files) {
            Map<String,String> result = s3Service.uploadFile(file);
            fileList.add(result.get("url"));
            workLog.addPicture(result.get("url"),result.get("fileName"));
        }
        workLogRepository.save(workLog);
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponseDto> getWorkLogList(MemberDetail detail) throws IllegalArgumentException {
        List<WorkLog> workLogList = workLogRepository.findAllByMemberOrderByDateDesc(detail.getMember());
        List<WorkLogResponseDto> responseDtoList = new ArrayList<>();
        for(WorkLog log : workLogList) responseDtoList.add(new WorkLogResponseDto(log));
        return responseDtoList;
    }

    @Transactional
    public WorkLogResponseDto getWorkLogDetails(Long worklogid, String userEmail) {
        WorkLog workLog = workLogRepository.findById(worklogid).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (Objects.equals(workLog.getMember().getEmail(), userEmail)) {
            return new WorkLogResponseDto(workLog);
        } else throw new IllegalArgumentException("작성자 본인이 아닙니다.");
    }

    @Transactional
    public void deleteWorkLog(Long worklogid, String userEmail) {
        WorkLog workLog = workLogRepository.findById(worklogid).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (Objects.equals(workLog.getMember().getEmail(), userEmail)) {
            List<Images> list = workLog.getImages();
            for (Images picture : list) s3Service.deleteFile(picture.getFileName());
            workLogRepository.deleteById(worklogid);
        } else throw new IllegalArgumentException("작성자 본인이 아닙니다.");
    }

//    @Transactional
//    public List<WorkLogResponseDto> updateWorkLog(Long worklogid, String userEmail) {
//
//    }
}