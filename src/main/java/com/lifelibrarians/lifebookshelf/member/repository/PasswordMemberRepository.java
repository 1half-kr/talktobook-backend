package com.lifelibrarians.lifebookshelf.member.repository;

import com.lifelibrarians.lifebookshelf.member.domain.PasswordMember;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PasswordMemberRepository extends JpaRepository<PasswordMember, Long> {

}
