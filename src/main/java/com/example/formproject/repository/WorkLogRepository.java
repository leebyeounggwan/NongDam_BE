package com.example.formproject.repository;

import com.example.formproject.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog,Long> {
    @Query("Select month(this_.endTime),sum(this_.harvest) from WorkLog this_ where this_.member.id=:memberid and this_.endTime between :pastDay and :now group by year(this_.endTime),month(this_.endTime),this_.crop")
    public List<Object[]> selectHarvest(int memberid, LocalDate pastDay,LocalDate now);
}
