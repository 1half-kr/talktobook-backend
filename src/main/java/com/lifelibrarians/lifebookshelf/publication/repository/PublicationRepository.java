package com.lifelibrarians.lifebookshelf.publication.repository;

import com.lifelibrarians.lifebookshelf.publication.domain.Publication;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface PublicationRepository extends JpaRepository<Publication, Long> {

	@Query(value = "SELECT p FROM Publication p JOIN FETCH p.book b JOIN FETCH b.member m WHERE m.id = :memberId",
			countQuery = "SELECT count(p) FROM Publication p JOIN p.book b WHERE b.member.id = :memberId")
	Page<Publication> getAllByMemberId(Long memberId, Pageable pageable);

	@Query("select p from Publication p"
			+ " JOIN FETCH p.book b"
			+ " JOIN FETCH b.member m"
			+ " where p.id = :publicationId and m.id = :memberId")
	Optional<Publication> findByMemberIdWithBook(Long publicationId, Long memberId);
}
