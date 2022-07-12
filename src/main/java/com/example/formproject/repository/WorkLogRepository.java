package com.example.formproject.repository;

import com.example.formproject.entity.Member;
import com.example.formproject.entity.WorkLog;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog,Long> {
    @Query("Select year(this_.endTime),month(this_.endTime),this_.crop.name,sum(this_.harvest) from WorkLog this_ where this_.member.id=:memberId and this_.crop.id=:cropId and this_.harvest > 0 group by year(this_.endTime),month(this_.endTime)")
    List<Object[]> selectHarvest(@Param("memberId") int memberId,@Param("cropId") int cropId);

    @Query("Select max(this_.endTime),min(this_.endTime) from WorkLog this_ where this_.member.id=:memberId and this_.harvest > 0")
    LocalDateTime[] findTimesOfHarvest(@Param("memberId") int memberId);

    List<WorkLog> findAllByOrderByStartTimeDesc(String userEmail);
}