package hu.informula.movieinfo.persistence.entity;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "REQUEST_HISTORY")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class RequestHistoryEntity {
  @Id
  @Column(name = "ID")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  @Column(name = "API")
  private String api;
  @Column(name = "IP_ADDRESS")
  private String ipAddress;
  @Column(name = "HOST")
  private String host;
  @Column(name = "USER_AGENT")
  private String userAgent;
  @Column(name = "SEARCH_TERM")
  private String searchTerm;
  @Column(nullable = false, updatable = false, name = "STARTED_AT")
  @Temporal(TemporalType.TIMESTAMP)
  @CreatedDate
  private Date startedAt;
  @Column(name = "FINISHED_AT")
  private Date finishedAt;
}
