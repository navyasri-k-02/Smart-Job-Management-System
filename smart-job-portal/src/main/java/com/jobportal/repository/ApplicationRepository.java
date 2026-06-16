package com.jobportal.repository;

import com.jobportal.entity.Application;
import com.jobportal.entity.Job;
import com.jobportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudent(User student);

    List<Application> findByJob(Job job);

    Optional<Application> findByStudentAndJob(User student, Job job);

    boolean existsByStudentAndJob(User student, Job job);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.status = 'PENDING'")
    long countPending();

    List<Application> findByJobIn(List<Job> jobs);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.employer.id = :employerId")
    long countByEmployerId(Long employerId);
}
