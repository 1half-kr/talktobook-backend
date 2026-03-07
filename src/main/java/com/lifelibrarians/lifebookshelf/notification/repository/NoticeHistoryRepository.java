package com.lifelibrarians.lifebookshelf.notification.repository;

import com.lifelibrarians.lifebookshelf.notification.domain.NoticeHistory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface NoticeHistoryRepository extends JpaRepository<NoticeHistory, Long> {

	@Modifying
	@Query("update NoticeHistory nh set nh.isRead = true where nh.member.id = :memberId and nh.id in :notificationHistoryIds")
	void updateIsReadByMemberIdAndNotificationIdIn(Long memberId, List<Long> notificationHistoryIds);

	@Modifying
	@Query("delete from NoticeHistory nh where nh.member.id = :memberId and nh.id in :notificationHistoryIds")
	void deleteByMemberIdAndIdIn(Long memberId, List<Long> notificationHistoryIds);

	@Query("select nh from NoticeHistory nh where nh.member.id = :memberId")
	List<NoticeHistory> findByMemberId(Long memberId);

	@Query("select nh from NoticeHistory nh where nh.member.id = :memberId")
	Page<NoticeHistory> findByMemberId(Long memberId, Pageable pageable);
}
