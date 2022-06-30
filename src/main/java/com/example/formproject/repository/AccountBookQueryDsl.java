package com.example.formproject.repository;

import com.example.formproject.entity.AccountBook;
import com.example.formproject.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.formproject.entity.QAccountBook.accountBook;

@Repository
public class AccountBookQueryDsl extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    @Autowired
    public AccountBookQueryDsl(JPAQueryFactory queryFactory){
        super(AccountBook.class);
        this.queryFactory = queryFactory;
    }
    public List<AccountBook> findByMaxResult(Member member,int maxResult){
        return queryFactory.selectFrom(accountBook)
                .where(accountBook.member.id.eq(member.getId()))
                .orderBy(accountBook.date.desc())
                .limit(maxResult)
                .fetch();
    }
}
