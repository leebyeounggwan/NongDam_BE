package com.example.formproject.service;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.SubMaterialRequestDto;
import com.example.formproject.dto.request.WorkLogRequestDto;
import com.example.formproject.dto.response.*;
import com.example.formproject.entity.*;
import com.example.formproject.repository.CropRepository;
import com.example.formproject.repository.QueryDslRepository;
import com.example.formproject.repository.WorkLogRepository;
import com.example.formproject.security.MemberDetail;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AopInvocationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkLogService {
    private final WorkLogRepository workLogRepository;
    private final CropRepository cropRepository;
    private final AwsS3Service s3Service;

    private final QueryDslRepository queryDslRepository;

    public LineChartDto getHarvestMonthData(Member m) {
        LineChartDto ret = new LineChartDto();
        List<LocalDate[]> times = workLogRepository.findTimesOfHarvest(m.getId());
        LocalDate now = LocalDate.now();
        LocalDate time = now.minusMonths(5L);
        int minMonth = time.getMonthValue();
        int minYear = time.getYear();
        for (Crop c : m.getCrops()) {
            List<Object[]> datas = workLogRepository.selectHarvestMonth(m.getId(), c.getId(), minYear, minMonth);
            LineChartDataDto dto = LineChartDataDto.builder().name(c.getName()).build();
            LocalDate tmp = time;
            while (tmp.isBefore(now) || tmp.isEqual(now)) {
                LocalDate finalTmp = tmp;
                if (!ret.hasLabel(finalTmp.format(DateTimeFormatter.ofPattern("yyyy.MM"))))
                    ret.addLabel(finalTmp.format(DateTimeFormatter.ofPattern("yyyy.MM")));
                Object[] data = datas.stream().filter(e -> Integer.parseInt(e[0].toString()) == finalTmp.getYear() && Integer.parseInt(e[1].toString()) == finalTmp.getMonthValue()).findFirst().orElse(null);
                dto.addData(data == null ? 0 : Integer.parseInt(data[2].toString()));
                tmp = tmp.plusMonths(1L);
            }
            ret.addData(dto);
        }
        return ret;
    }

    public LineChartDto getHarvestYearData(Member m) {
        LineChartDto ret = new LineChartDto();
        List<LocalDate[]> times = workLogRepository.findTimesOfHarvest(m.getId());
        LocalDate now = LocalDate.now();
        LocalDate time = now.minusYears(5L);
        int minYear = time.getYear();
        for (Crop c : m.getCrops()) {
            List<Object[]> datas = workLogRepository.selectHarvestYear(m.getId(), c.getId(), minYear);
            LineChartDataDto dto = LineChartDataDto.builder().name(c.getName()).build();
            LocalDate tmp = time;
            while (tmp.isBefore(now) || tmp.isEqual(now)) {
                LocalDate finalTmp = tmp;
                if (!ret.hasLabel(Integer.toString(finalTmp.getYear())))
                    ret.addLabel(Integer.toString(finalTmp.getYear()));
                Object[] data = datas.stream().filter(e -> Integer.parseInt(e[0].toString()) == finalTmp.getYear()).findFirst().orElse(null);
                dto.addData(data == null ? 0 : Integer.parseInt(data[1].toString()));
                tmp = tmp.plusYears(1L);
            }
            ret.addData(dto);
        }
        return ret;
    }

    public LineChartDto getWorkTimeData(Member m) {
        LineChartDto ret = new LineChartDto();
        int year = LocalDate.now().getYear();
        List<Object[]> thisYear = workLogRepository.selectWorkTimeofYear(m.getId(), year);
        List<Object[]> preYear = workLogRepository.selectWorkTimeofYear(m.getId(), year - 1);
        ret.addLabel(Integer.toString(year - 1));
        ret.addLabel(Integer.toString(year));
        for (int idx = 1; idx < 5; idx++) {
            int finalIdx = idx;
            LineChartDataDto data = LineChartDataDto.builder().name(idx + "분기").build();
            Object[] preYearData = preYear.stream().filter(e -> Integer.parseInt(e[1].toString()) == finalIdx).findFirst().orElse(null);
            int number1 = preYearData == null ? 0 : Integer.parseInt(preYearData[2].toString());
            data.addData(number1);
            Object[] thisYearData = thisYear.stream().filter(e -> Integer.parseInt(e[1].toString()) == finalIdx).findFirst().orElse(null);
            int number2 = thisYearData == null ? 0 : Integer.parseInt(thisYearData[2].toString());
            data.addData(number2);
            ret.addData(data);
        }
        return ret;
    }

    @Transactional
    public void createWorkLog(Member member, WorkLogRequestDto dto, List<MultipartFile> files) {
        WorkLog workLog = dto.build(member, cropRepository);
        if (files != null) {
            for (MultipartFile file : files) {
                Map<String, String> result = s3Service.uploadFile(file);
                workLog.addPicture(result.get("url"));
            }
        }
        workLogRepository.save(workLog);
    }

    @Transactional(readOnly = true)
    public List<WorkLogResponseDto> getWorkLogList(MemberDetail detail) throws IllegalArgumentException {
        List<WorkLogResponseDto> responseDtoList = new ArrayList<>();
        List<WorkLog> workLogList = workLogRepository.findAllByMemberOrderByIdDesc(detail.getMember());
        for (WorkLog log : workLogList)
            responseDtoList.add(new WorkLogResponseDto(log,new CropDto(log.getCrop())));
        return responseDtoList;
    }

    @Transactional(readOnly = true)
    public WorkLogResponseDto getWorkLogDetails(Long worklogid, int memberId) {
        WorkLog workLog = workLogRepository.findById(worklogid).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        WorkLogResponseDto ret;
        if (workLog.getMember().getId() == memberId) {
            ret =  new WorkLogResponseDto(workLog, new CropDto(workLog.getCrop()));
        } else throw new IllegalArgumentException("작성자 본인이 아닙니다.");
        try {
            Tuple next = queryDslRepository.selectNextWorkLog(memberId, worklogid);
            ret.setNextWorkLogInfo(new SubWorkLogResponseDto(next.get(0,Long.class),next.get(1,String.class),next.get(2,LocalDate.class).format(FinalValue.DAY_FORMATTER)));
        }catch (IndexOutOfBoundsException e){
            ret.setNextWorkLogInfo(null);
        }
        try {
            Tuple pre = queryDslRepository.selectPreviousWorkLog(memberId, worklogid);
            ret.setPreviousWorkLogInfo(new SubWorkLogResponseDto(pre.get(0,Long.class),pre.get(1,String.class),pre.get(2,LocalDate.class).format(FinalValue.DAY_FORMATTER)));
        }catch (IndexOutOfBoundsException e){
            ret.setPreviousWorkLogInfo(null);
        }
        return ret;
    }

    @Transactional
    public void deleteWorkLog(Long worklogid, MemberDetail details) {
        WorkLog workLog = workLogRepository.findById(worklogid).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (Objects.equals(workLog.getMember().getEmail(), details.getUsername())) {
            List<String> list = workLog.getImages();
            for (String picture : list) {
                try {
                    String[] urlArr = picture.split("/");
                    String fileKey = urlArr[urlArr.length - 1];
                    s3Service.deleteFile(fileKey);
                } catch (AmazonS3Exception e) {
                   log.warn("삭제할 파일 없음");
                }
            }
            workLogRepository.deleteById(worklogid);
        } else throw new IllegalArgumentException("작성자 본인이 아닙니다.");
    }

    @Transactional
    public void updateWorkLog(Long worklogid, WorkLogRequestDto dto, MemberDetail details, List<MultipartFile> files) {
        WorkLog workLog = workLogRepository.findById(worklogid).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (Objects.equals(workLog.getMember().getEmail(), details.getUsername())) {
            List<String> list = workLog.getImages();
            if (files != null) {
                for (String picture : list) {
                    try {
                        String[] urlArr = picture.split("/");
                        String fileKey = urlArr[urlArr.length - 1];
                        s3Service.deleteFile(fileKey);
                    } catch (AmazonS3Exception e) {
                        log.warn("삭제할 파일 없음");
                    }
                }
            }
            Crop crop = cropRepository.findById(dto.getCrop()).orElseThrow(
                    () -> new IllegalArgumentException("작물 정보를 찾을 수 없습니다."));
            List<SubMaterial> SubMaterialList = new ArrayList<>();
            try {
                for (SubMaterialRequestDto subMaterialRequestDto : dto.getSubMaterial())
                    SubMaterialList.add(subMaterialRequestDto.build());
            } catch (NullPointerException e) {
                log.warn("부자제 목록 없음");
            }
            workLog.updateWorkLog(dto, crop, SubMaterialList, files);
            if (files != null) {
                for (MultipartFile file : files) {
                    Map<String, String> result = s3Service.uploadFile(file);
                    workLog.addPicture(result.get("url"));
                }
            }
            workLogRepository.save(workLog);
        } else throw new IllegalArgumentException("작성자 본인이 아닙니다.");
    }
    @Transactional(readOnly = true)
    public WorkTimeRateDto getWorkingRate(Member member){
        int year = LocalDate.now().getYear();
        float thisYear;
        float preYear;
        try {
            thisYear = workLogRepository.getSumWorkTimeOfYear(year, member.getId());
        }catch (AopInvocationException e){
            thisYear = 0;
        }
        try {
            preYear = workLogRepository.getSumWorkTimeOfYear(year - 1, member.getId());
        }catch (AopInvocationException e){
            preYear = 0;
        }
        int rate;
        String rateText=thisYear >= preYear?"증가":"감소";
        if(preYear == 0 && thisYear == 0){
            rate = 0;
        }else if(preYear == 0 && thisYear > 0){
            rate = 100;
        }else {
            rate = Math.abs(100 - Math.round((thisYear / preYear) * 100));
        }
        return new WorkTimeRateDto(rate,rateText);
    }
}