package com.lifelibrarians.lifebookshelf.notification.repository;

import com.lifelibrarians.lifebookshelf.notification.domain.DeviceRegistry;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeviceRegistryRepository extends JpaRepository<DeviceRegistry, Long> {

	@Query("SELECT d FROM DeviceRegistry d WHERE d.member.id = :memberId")
	List<DeviceRegistry> findByMemberIdOrderByCreatedAt(Long memberId);
}
