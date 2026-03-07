package com.lifelibrarians.lifebookshelf.notification.repository;

import com.lifelibrarians.lifebookshelf.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface NotificationRepository extends JpaRepository<Notification, Long> {

}
