package com.example.formproject.repository;

import com.example.formproject.entity.Member;
import com.example.formproject.entity.WorkLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WorkLogRepository extends JpaRepository<WorkLog, Long> {
    @Query("Select year(this_.date),month(this_.date),this_.crop.name,sum(this_.harvest) from WorkLog this_ where this_.member.id=:memberId and this_.crop.id=:cropId and this_.harvest > 0 group by year(this_.date),month(this_.date)")
    public List<Object[]> selectHarvest(@Param("memberId") int memberId, @Param("cropId") int cropId);

    @Query("Select max(this_.date),min(this_.date) from WorkLog this_ where this_.member.id=:memberId and this_.harvest > 0")
    public LocalDateTime[] findTimesOfHarvest(@Param("memberId") int memberId);

    @Query("Select year(this_.date),this_.quarter,sum(this_.workTime) from WorkLog this_ where this_.member.id=:memberId and year(this_.date) = :year group by year(this_.date),this_.quarter order by this_.quarter,year(this_.date)")
    public List<Object[]> selectWorkTimeofYear(@Param("memberId") int memberId, @Param("year")int year);


    List<WorkLog> findAllByMemberOrderByDateDesc(Member member);
}