package com.mysite.medium.answer;

import java.time.LocalDateTime;
import java.util.Set;

import com.mysite.medium.question.Question;
import com.mysite.medium.user.SiteUser;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Answer {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(columnDefinition = "TEXT")
  private String content;

  private LocalDateTime modifyDate;

  private LocalDateTime createDate;

  @ManyToOne
  private Question question;

  @ManyToOne
  private SiteUser author;

  @ManyToMany
  Set<SiteUser> voter;
}
