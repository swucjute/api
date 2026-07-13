package com.swucjute.api.domain.worship.document;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Document(collection = "worship_transcripts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WorshipTranscriptDocument {

  @Id private String id;

  @Indexed
  @Field("worship_id")
  private Long worshipId;

  @Indexed private String source;

  private String content;

  @Field("char_count")
  private Integer charCount;

  @CreatedDate
  @Field("created_at")
  private LocalDateTime createdAt;
}
