package com.example.formproject.service;

import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.dto.response.LineChartDataDto;
import com.example.formproject.dto.response.LineChartDto;
import com.example.formproject.entity.Crop;
import com.example.formproject.entity.Member;
import com.example.formproject.entity.WorkLog;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.repository.WorkLogRepository;
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
    private final WorkLog workLog;
    private final WorkLogRepository workLogRepository;
    private final CropRepository cropRepository;
    private final AwsS3Service s3Service;

    public LineChartDto getHarvestData(Member m) {
        LineChartDto ret = new LineChartDto();
        LocalDateTime[] times = workLogRepository.findTimesOfHarvest(m.getId());
        for (Crop c : m.getCrops()) {
            List<Object[]> datas = workLogRepository.selectHarvest(m.getId(), c.getId());
            LineChartDataDto dto = LineChartDataDto.builder().name(c.getName()).build();
            LocalDate startTime = times[1].toLocalDate();
            LocalDate endTime = times[0].toLocalDate();
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

    @Transactional
    public void createWorkLog(Member member, WorkLogRequestDto dto, List<MultipartFile> files) {
        List<String> fileList = new ArrayList<>();
        for (MultipartFile file : files) {
            fileList.add("" + s3Service.uploadFile(file).values());
            workLog.addPicture("" + s3Service.uploadFile(file).values());
        }
        workLogRepository.save(dto.build(fileList, member, cropRepository));
    }

    @Transactional
    public void deleteWorkLog(Long worklogid, String userEmail) {
        WorkLog workLog = workLogRepository.findById(worklogid).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (Objects.equals(workLog.getMember().getEmail(), userEmail)) {
            List<String> list = workLog.getPictures();
            for (String picture : list) s3Service.deleteFile(picture);
            workLogRepository.deleteById(worklogid);
        } else throw new IllegalArgumentException("작성자 본인이 아닙니다.");
    }

//    @Transactional
//    public List<WorkLogResponseDto> updateWorkLog(Long worklogid, String userEmail) {
//    }
}