package com.itau.pixconfirm.infrastructure.repository;

import com.itau.pixconfirm.infrastructure.entity.Cob;
import com.itau.pixconfirm.type.StatusOperationType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface CobRepository extends MongoRepository<Cob, String> {

    @Query("{'tx_id': ?0}")
    @Update("{'$set': {'status_operation': ?1}}")
    void updateStatusOperationById(String txId, StatusOperationType status);

}
