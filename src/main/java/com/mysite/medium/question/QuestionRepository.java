package com.mysite.medium.question;

import com.mysite.medium.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.security.Principal;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
  Question findBySubject(String subject);
  Question findBySubjectAndContent(String subject, String content);
  List<Question> findBySubjectLike(String subject);
  Page<Question> findAll(Pageable pageable);
  Page<Question> findByIsPublishedTrue(Pageable pageable);

  // search를 사용한 키워드 검색
  Page<Question> findAll(Specification<Question> spec, Pageable pageable);

  // @Query을 이용한 쿼리를 직접 작성해서 키워드 검색
  @Query(
      "select "
          + "distinct q "
          + "from Question q "
          + "left outer join SiteUser u1 on q.author=u1 "
          + "left outer join Answer a on a.question=q "
          + "left outer join SiteUser u2 on a.author=u2 "
          + "where "
          + "   q.subject like %:kw% "
          + "   or q.content like %:kw% "
          + "   or u1.username like %:kw% "
          + "   or a.content like %:kw% "
          + "   or u2.username like %:kw% "
  )
  Page<Question> findAllByKeyword(@Param("kw") String kw, Pageable pageable);

  Page<Question> findByAuthor(Pageable pageable, SiteUser author);
}
