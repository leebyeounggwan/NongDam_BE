package com.example.formproject.repository;

import com.example.formproject.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface MemberRepository extends JpaRepository<Member,Integer> {
    public Optional<Member> findByEmail(String email);
}
