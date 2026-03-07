package com.lifelibrarians.lifebookshelf.member.repository;

import com.lifelibrarians.lifebookshelf.member.domain.SocialMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SocialMemberRepository extends JpaRepository<SocialMember, Long> {

}
