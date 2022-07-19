package com.example.formproject.repository;

import com.example.formproject.entity.AccountBook;
import com.example.formproject.entity.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;
import static com.example.formproject.entity.QMember.member;
import static com.example.formproject.entity.QCrop.crop;
import static com.example.formproject.entity.QImages.images;

import java.util.List;

import static com.example.formproject.entity.QAccountBook.accountBook;

@Repository
public class QueryDslRepository extends QuerydslRepositorySupport {
    private final JPAQueryFactory queryFactory;
    @Autowired
    public QueryDslRepository(JPAQueryFactory queryFactory){
        super(AccountBook.class);
        this.queryFactory = queryFactory;
    }
    public List<AccountBook> selectAccountBookByMaxResult(Member member, int maxResult){
        return queryFactory.selectFrom(accountBook)
                .where(accountBook.member.id.eq(member.getId()))
                .orderBy(accountBook.date.desc())
                .limit(maxResult)
                .fetch();
    }
    public Member selectMemberByIdFetch(int id){
        return queryFactory.selectFrom(member)
                .leftJoin(member.crops,crop).fetchJoin()
                .leftJoin(member.profileImage,images).fetchJoin()
                .where(member.id.eq(id)).fetch().get(0);
    }
}
