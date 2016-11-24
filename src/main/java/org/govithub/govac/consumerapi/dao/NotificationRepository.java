package org.govithub.govac.consumerapi.dao;

import java.util.List;

import org.govithub.govac.consumerapi.model.Notification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findById(long id);
    
    List<Notification> findByUserId(long id);
    
    List<Notification> findByApplication(String application);
    
    @Query("SELECT t "
    		+ " FROM Notification t WHERE user_id = :userId AND "
    		+ " timestamp >= :startTs AND timestamp <= :endTs AND ( "
    		+ " application LIKE :application AND provider LIKE :provider AND "
    		+ " (title LIKE :keyword OR description LIKE :keyword OR "
    		+ " shortDescription LIKE :keyword OR application LIKE :keyword OR provider LIKE :keyword ))")
    List<Notification> findByUserAndOtherFilters(@Param("userId") long userId, 
    		@Param("keyword") String keyword, @Param("application") String application, 
    		@Param("provider") String provider, @Param("startTs") long startTs, @Param("endTs") long endTs);
    
    @Query("SELECT t "
    		+ " FROM Notification t WHERE application LIKE :application AND (:userId < 0L OR user_id = :userId) AND "
    		+ " timestamp >= :startTs AND timestamp <= :endTs AND ( "
    		+ " application LIKE :application AND provider LIKE :provider AND "
    		+ " (title LIKE :keyword OR description LIKE :keyword OR "
    		+ " short_description LIKE :keyword OR application LIKE :keyword OR provider LIKE :keyword ))")
    List<Notification> findByApplicationAndOtherFilters(@Param("application") String application, 
    		@Param("keyword") String keyword, @Param("userId") long userId, 
    		@Param("provider") String provider, @Param("startTs") long startTs, @Param("endTs") long endTs);
}