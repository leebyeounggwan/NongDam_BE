package com.example.formproject.repository;

import com.example.formproject.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule,Long> {
    @Query("Select this_ from Schedule this_ where this_.member.id = :memberId and (this_.endTime > :now or this_.startTime > :lastWeek) order by this_.startTime")
    public List<Schedule> findScheduleLastWeek(@Param("memberId") int memberId, @Param("now") LocalDateTime now, @Param("lastWeek") LocalDateTime lastWeek);
    @Query("Select this_ from Schedule this_ where this_.member.id = :memberId and month(this_.startTime) = :month and year(this_.startTime) = :year order by this_.startTime")
    public List<Schedule> findScheduleOfMonth(@Param("memberId") int memberId, @Param("year") int year, @Param("month") int month);

    @Query("Select this_ from Schedule this_ where this_.member.id = :memberId and ((this_.startTime between :startTime and :endTime) or (this_.startTime < :startTime and this_.endTime < :endTime)) order by this_.startTime")
    public List<Schedule> findScheduleOfMonth(@Param("memberId") int memberId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    @Query("Select this_ from Schedule this_ where this_.member.id = :memberId and this_.startTime between :start and :end order by this_.startTime")
    public List<Schedule> findScheduleOfDay(@Param("memberId") int memberId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
