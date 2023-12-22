package com.mysite.medium.question;

import com.mysite.medium.answer.Answer;
import com.mysite.medium.user.SiteUser;
import com.mysite.medium.user.UserService;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {

  private final QuestionRepository questionRepository;

  private final UserService userService;

  private Specification<Question> search(String kw) {
    return new Specification<>() {
      private static final long serialVersionUID = 1L;
      @Override
      public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
        query.distinct(true);  // 중복을 제거
        Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
        Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
        Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
        return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
            cb.like(q.get("content"), "%" + kw + "%"),      // 내용
            cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
            cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
            cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
      }
    };
  }

  public Page<Question> getList(int page, String kw) { // kw : 글쓴이
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

    if(!kw.isEmpty()) { // kw에 입력값이 있는 경우
      SiteUser author = this.userService.getUser(kw); // 회원과 일치하는 kw가 넘어오는 경우만 에러 없음
      return this.questionRepository.findByAuthor(pageable, author);
    }else{// kw에 입력값이 없는 경우
      return this.questionRepository.findByIsPublishedTrue(pageable);
    }
  }

  public Page<Question> getMyList(int page, SiteUser author) {
    // 정렬기준
    List<Sort.Order> sorts = new ArrayList<>();
    sorts.add(Sort.Order.desc("createDate"));
    // 페이지 관련 정보에 정렬 기준을 담는다.
    Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
    return this.questionRepository.findByAuthor(pageable, author);
  }

  public Question getQuestion(Integer id) {
    Optional<Question> question = this.questionRepository.findById(id);
    if (question.isPresent()) {
      return question.get();
    } else {
      throw new RuntimeException("question not found");
    }
  }

  public void create(String subject, String content, Boolean isPublished, SiteUser user) {
    Question q = new Question();
    q.setSubject(subject);
    q.setContent(content);
    q.setIsPublished(isPublished);
    q.setCreateDate(LocalDateTime.now());
    q.setAuthor(user);
    this.questionRepository.save(q);
  }

  public void modify(Question question, String subject, String content, Boolean isPublished) {
    question.setSubject(subject);
    question.setContent(content);
    question.setIsPublished(isPublished);
    question.setModifyDate(LocalDateTime.now());
    this.questionRepository.save(question);
  }

  public void delete(Question question) {
    this.questionRepository.delete(question);
  }

  public void vote(Question question, SiteUser siteUser) {
    question.getVoter().add(siteUser);
    this.questionRepository.save(question);
  }
}
