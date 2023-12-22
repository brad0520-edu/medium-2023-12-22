package com.mysite.medium.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.mysite.medium.answer.Answer;
import com.mysite.medium.user.SiteUser;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(length = 200)
  private String subject;

  @Column(columnDefinition = "TEXT")
  private String content;

  private Boolean isPublished;

  @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
  private List<Answer> answerList;

  @ManyToOne
  private SiteUser author; // 작성자

  @ManyToMany
  Set<SiteUser> voter;

  private LocalDateTime modifyDate;

  private LocalDateTime createDate;
}
