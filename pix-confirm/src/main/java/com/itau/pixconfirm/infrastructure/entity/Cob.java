package com.itau.pixconfirm.infrastructure.entity;

import com.itau.pixconfirm.type.StatusOperationType;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document(collection = "cob_transaction")
public class Cob {

    @MongoId
    private String id;

    @Indexed(unique = true)
    @Field("tx_id")
    private String txId;

    @Field("status_operation")
    private StatusOperationType statusOperationType;

    public Cob() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public StatusOperationType getStatusOperationType() {
        return statusOperationType;
    }

    public void setStatusOperationType(StatusOperationType statusOperationType) {
        this.statusOperationType = statusOperationType;
    }
}
