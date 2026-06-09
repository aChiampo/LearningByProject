package com.spring.bar.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.bar.entities.ProdottoBar;

import java.util.List;

public interface ProdottoBarRepo extends JpaRepository<ProdottoBar, Long> {
    List<ProdottoBar> findBySezione(String sezione);
}