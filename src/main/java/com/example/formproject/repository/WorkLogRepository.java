package com.example.formproject.repository;

import com.example.formproject.entity.Member;
import com.example.formproject.entity.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @Query("Select year(this_.date),month(this_.date),sum(this_.harvest) from WorkLog this_ where this_.member.id=:memberId and this_.crop.id=:cropId and this_.harvest > 0 and year(this_.date) >= :year and month(this_.date) >= :month group by year(this_.date),month(this_.date) order by year(this_.date),month(this_.date)")
    public List<Object[]> selectHarvestMonth(@Param("memberId") int memberId, @Param("cropId") int cropId, @Param("year") int year, @Param("month") int month);

    @Query("Select year(this_.date),sum(this_.harvest) from WorkLog this_ where this_.member.id=:memberId and this_.crop.id=:cropId and this_.harvest > 0 and year(this_.date) >= :year group by year(this_.date)")
    public List<Object[]> selectHarvestYear(@Param("memberId") int memberId, @Param("cropId") int cropId, @Param("year") int year);

    @Query("Select year(this_.date),month(this_.date),this_.crop.name,sum(this_.harvest) from WorkLog this_ where this_.member.id=:memberId and this_.crop.id=:cropId and this_.harvest > 0 group by year(this_.date),month(this_.date) order by this_.date")
    public List<Object[]> selectHarvest(@Param("memberId") int memberId, @Param("cropId") int cropId);

    @Query("Select max(this_.date),min(this_.date) from WorkLog this_ where this_.member.id=:memberId and this_.harvest > 0")
    public List<LocalDate[]> findTimesOfHarvest(@Param("memberId") int memberId);

    @Query("Select year(this_.date),this_.quarter,sum(this_.workTime) from WorkLog this_ where this_.member.id=:memberId and year(this_.date) = :year group by year(this_.date),this_.quarter order by this_.quarter,year(this_.date)")
    public List<Object[]> selectWorkTimeofYear(@Param("memberId") int memberId, @Param("year") int year);

    @Query("Select sum(this_.workTime) from WorkLog this_ where year(this_.date)=:year and this_.member.id=:id")
    public int getSumWorkTimeOfYear(@Param("year")int year,@Param("id")int id);

    List<WorkLog> findAllByMemberOrderByIdDesc(Member member);
    Page<WorkLog> findAllByMemberOrderByIdDesc(Member member, PageRequest pageRequest);
}