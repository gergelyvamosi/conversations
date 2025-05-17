package org.nextweb.converstions.repository;

import java.util.List;

import org.nextweb.converstions.model.Conference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference, Long> {

  @Query("SELECT c FROM Conference c JOIN c.users u WHERE u.id = :userId")
  List<Conference> findByUsersId(@Param("userId") Long userId);

}
