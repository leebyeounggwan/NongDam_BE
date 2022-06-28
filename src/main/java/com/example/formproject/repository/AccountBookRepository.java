package com.example.formproject.repository;

import com.example.formproject.entity.AccountBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountBookRepository extends JpaRepository<AccountBook,Long> {
    @Query("Select month(this_.date),sum(this_.price) from AccountBook this_ where this_.member.id=:memberId and year(this_.date) = :year and this_.type < 3 group by month(this_.date)")
    public List<Object[]> incomeOfYear(@Param("memberId") int memberId,@Param("year") int year);

    @Query("Select month(this_.date),sum(this_.price) from AccountBook this_ where this_.member.id=:memberId and year(this_.date) = :year and this_.type > 2 group by month(this_.date)")
    public List<Object[]> spandOfYear(@Param("memberId") int memberId,@Param("year") int year);

}
