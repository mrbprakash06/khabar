package com.mrbprakash;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query(value = "SELECT * FROM News n WHERE n.provider = :provider ORDER BY n.sequence DESC LIMIT 1", nativeQuery = true)
    Optional<News> findOneByProvider(@Param("provider") String provider);
}
