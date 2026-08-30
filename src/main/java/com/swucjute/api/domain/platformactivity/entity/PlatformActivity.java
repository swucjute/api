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

  public void softDelete() {
    this.deleteYn = "Y";
  }
}
