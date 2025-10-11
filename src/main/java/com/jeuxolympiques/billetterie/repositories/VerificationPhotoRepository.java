package com.jeuxolympiques.billetterie.repositories;

import com.jeuxolympiques.billetterie.entities.VerificationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationPhotoRepository extends JpaRepository<VerificationPhoto, String> {
}
