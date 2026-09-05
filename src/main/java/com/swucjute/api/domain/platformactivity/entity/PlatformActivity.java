package com.swucjute.api.domain.platformactivity.entity;

import com.swucjute.api.domain.member.entity.Member;
import com.swucjute.api.domain.platform.entity.Platform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "tb_platform_activity")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlatformActivity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "platform_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_platform_activity_platform"))
  private Platform platform;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "user_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_platform_activity_user"))
  private Member user;

  @Column(columnDefinition = "text")
  private String content;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Column(name = "like_count", nullable = false)
  private int likeCount;

  @Column(name = "comment_count", nullable = false)
  private int commentCount;

  @CreatedDate
  @Column(name = "create_date", nullable = false, updatable = false)
  private LocalDateTime createDate;

  @LastModifiedDate
  @Column(name = "update_date", nullable = false)
  private LocalDateTime updateDate;

  @Column(name = "delete_yn", nullable = false, length = 1)
  private String deleteYn = "N";

  public static PlatformActivity create(
      Platform platform, Member user, String imageUrl, String content) {
    PlatformActivity activity = new PlatformActivity();
    activity.platform = platform;
    activity.user = user;
    activity.imageUrl = imageUrl;
    activity.content = content;
    activity.likeCount = 0;
    activity.commentCount = 0;
    activity.deleteYn = "N";
    return activity;
  }

  public void update(String imageUrl, String content) {
    this.imageUrl = imageUrl;
    this.content = content;
  }

  public void softDelete() {
    this.deleteYn = "Y";
  }

  public void increaseLikeCount() {
    this.likeCount++;
  }

  public void decreaseLikeCount() {
    if (this.likeCount > 0) this.likeCount--;
  }

  public void increaseCommentCount() {
    this.commentCount++;
  }

  public void decreaseCommentCount() {
    if (this.commentCount > 0) this.commentCount--;
  }
}
