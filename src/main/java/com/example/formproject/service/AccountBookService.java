package com.example.formproject.service;

import com.example.formproject.FinalValue;
import com.example.formproject.dto.request.AccountRequestDto;
import com.example.formproject.dto.response.AccountResponseDto;
import com.example.formproject.dto.response.CircleChartDto;
import com.example.formproject.dto.response.LineChartDto;
import com.example.formproject.dto.response.LineChartDataDto;
import com.example.formproject.entity.AccountBook;
import com.example.formproject.entity.Member;
import com.example.formproject.enums.AccountType;
import com.example.formproject.repository.QueryDslRepository;
import com.example.formproject.repository.AccountBookRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Getter
@RequiredArgsConstructor
public class AccountBookService {
    private final AccountBookRepository accountBookRepository;
    private final QueryDslRepository queryDslRepository;

    public LineChartDto getResults(Member m){
        List<Object[]> incomes = accountBookRepository.incomeOfYear(m.getId());
        List<Object[]> spands = accountBookRepository.spandOfYear(m.getId());

        LineChartDto ret = new LineChartDto();

        LineChartDataDto income = LineChartDataDto.builder().name("수입").build();
        LineChartDataDto spand = LineChartDataDto.builder().name("지출").build();
        LineChartDataDto rawIncome = LineChartDataDto.builder().name("순이익").build();
        LocalDate[] times = accountBookRepository.getMinDate(m.getId()).get(0);
        LocalDate startTime = times[0];
        LocalDate endTime = times[1];
        while(startTime.isBefore(endTime) || (startTime.getMonthValue() == endTime.getMonthValue() && startTime.getYear() == endTime.getYear())){
            int year = startTime.getYear();
            int month = startTime.getMonthValue();
            Object[] incomeData = incomes.stream().filter(e ->(int) e[0] == year && (int) e[1] == month)
                    .findFirst().orElse(new Object[]{startTime.getYear(),startTime.getMonthValue(),0});
            Object[] spandData = spands.stream().filter(e ->(int) e[0] == year && (int) e[1] == month)
                    .findFirst().orElse(new Object[]{startTime.getYear(),startTime.getMonthValue(),0});
            ret.addLabel(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-01")));
            income.addData((long)incomeData[2]);
            spand.addData((long) spandData[2]);
            rawIncome.addData((long) incomeData[2]- (long) spandData[2]);
            startTime = startTime.plusMonths(1L);
        }
        ret.addData(income);
        ret.addData(spand);
        ret.addData(rawIncome);
        return ret;
    }

    public CircleChartDto getIncomeResult(Member m){
        List<Object[]> datas = accountBookRepository.getIncomeData(m.getId(), LocalDate.now().getYear());
        return makeCircleChart(datas);
    }
    public CircleChartDto getExpenseResult(Member m){
        List<Object[]> datas = accountBookRepository.getExpenseData(m.getId(), LocalDate.now().getYear());
        return makeCircleChart(datas);
    }
    private CircleChartDto makeCircleChart(List<Object[]> datas){
        CircleChartDto dto = new CircleChartDto();
        for(Object[] data : datas){
            dto.addLabel(AccountType.values()[(int)data[0]].name());
            dto.addData((long) data[1]);
        }
        return dto;
    }

    public AccountResponseDto save(Member member, AccountRequestDto dto){
        return new AccountResponseDto(accountBookRepository.save(dto.build(member)));
    }
    public AccountResponseDto save(long id, AccountRequestDto dto){
        AccountBook book = accountBookRepository.findById(id).orElseThrow(()->new IllegalArgumentException("장부 정보를 찾을 수 없습니다."));
        book.update(dto);
        return new AccountResponseDto(accountBookRepository.save(book));
    }

    public List<AccountResponseDto> findByLimits(Member member,int maxResult){
        List<AccountBook> books = queryDslRepository.selectAccountBookByMaxResult(member,maxResult);
        return convertResponse(books);
    }
    public List<AccountResponseDto> findByMonth(Member member,int year,int month){
        YearMonth yearMonth = YearMonth.of(year,month);
        LocalDate startTime = LocalDate.of(year,month,1);
        LocalDate endTime = yearMonth.atEndOfMonth();
        startTime = startTime.minusDays(FinalValue.getBackDayOfWeekValue(startTime.getDayOfWeek()));
        endTime = endTime.plusDays(FinalValue.getForwardDayOfWeekValue(endTime.getDayOfWeek()));
        List<AccountBook> books = accountBookRepository.findAccountBookByMonth(member.getId(),startTime,endTime);
        return convertResponse(books);
    }
    private List<AccountResponseDto> convertResponse(List<AccountBook> list){
        List<AccountResponseDto> ret = new ArrayList<>();
        list.stream().forEach(e->ret.add(new AccountResponseDto(e)));
        return ret;
    }
}
