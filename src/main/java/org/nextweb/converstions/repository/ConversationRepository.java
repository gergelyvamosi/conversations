package org.nextweb.converstions.repository;

import java.time.LocalDate;

import org.nextweb.converstions.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

  @Query("SELECT c FROM Conversation c WHERE c.conference.title LIKE %:title%")
  List<Conversation> findByConferenceTitleContaining(@Param("title") String title);

  @Query("SELECT c FROM Conversation c WHERE c.conference.plannedDate BETWEEN :startDate AND :endDate")
  List<Conversation> findByConferencePlannedDateBetween(
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate);

  List<Conversation> findByArchived(String archived);

  @Query("SELECT c FROM Conversation c WHERE c.conference.title LIKE %:title% AND c.conference.plannedDate BETWEEN :startDate AND :endDate")
  List<Conversation> findByConferenceTitleContainingAndConferencePlannedDateBetween(
          @Param("title") String title,
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate);

  @Query("SELECT c FROM Conversation c WHERE c.conference.title LIKE %:title% AND c.archived = :archived")
  List<Conversation> findByConferenceTitleContainingAndArchived(
          @Param("title") String title,
          @Param("archived") String archived);

  @Query("SELECT c FROM Conversation c WHERE c.conference.plannedDate BETWEEN :startDate AND :endDate AND c.archived = :archived")
  List<Conversation> findByConferencePlannedDateBetweenAndArchived(
          @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate,
          @Param("archived") String archived);

  @Query("SELECT c FROM Conversation c WHERE c.conference.title LIKE %:title% AND c.conference.plannedDate BETWEEN :startDate AND :endDate AND c.archived = :archived")
    List<Conversation> findByConferenceTitleContainingAndConferencePlannedDateBetweenAndArchived(
            @Param("title") String title,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("archived") String archived);

        
    @Query("SELECT c FROM Conversation c WHERE c.userA.id = :userId OR c.userB.id = :userId")
        List<Conversation> findByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Conversation c WHERE c.conference.id = :conferenceId")
        List<Conversation> findByConferenceId(@Param("conferenceId") Long conferenceId);

    @Transactional
    @Modifying
    @Query("UPDATE Conversation c SET c.archived = 'Y' WHERE c.conference.id = :conferenceId AND c.archived = 'N'")
        void archiveByConferenceId(@Param("conferenceId") Long conferenceId);

}
