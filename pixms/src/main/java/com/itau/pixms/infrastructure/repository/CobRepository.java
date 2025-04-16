package com.itau.pixms.infrastructure.repository;

import com.itau.pixms.infrastructure.entity.Cob;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CobRepository extends MongoRepository<Cob, String> {

    Optional<Cob> findByTxId(String txId);
}
