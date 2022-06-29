package com.example.formproject.repository;

import com.example.formproject.entity.Crop;
import com.example.formproject.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CropRepository extends JpaRepository<Crop,Integer> {
    @Query("Select this_ from Crop this_ order by this_.category, this_.type")
    List<Crop> findAllOrderByCategoryAndType();

    List<Crop> findAllByMember(Member member);
}
