package com.lifelibrarians.lifebookshelf.notification.repository;

import com.lifelibrarians.lifebookshelf.notification.domain.NotificationSubscribe;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface NotificationSubscribeRepository extends
		JpaRepository<NotificationSubscribe, Long> {

	@Query("SELECT ns FROM NotificationSubscribe ns WHERE ns.member.id = :memberId")
	List<NotificationSubscribe> findByMemberId(Long memberId);

	@Modifying
	@Query("DELETE FROM NotificationSubscribe ns WHERE ns.member.id = :id AND ns.notification.id IN :removedNotificationIds")
	void deleteByMemberIdAndNotificationIdIn(Long id, List<Long> removedNotificationIds);
}
