package com.itau.pixms.infrastructure.entity;

import com.itau.pixms.controller.dto.StatusOperation;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Document(collection = "cob_transaction")
public class Cob {

    @MongoId
    private String id;

    @Indexed(unique = true)
    @Field("tx_id")
    private String txId;

    @Indexed
    private String key;

    private int revision;

    @Field("status_operation")
    private StatusOperation statusOperation;

    private CobCalendar cobCalendar;

    private Debtor debtor;

    private Amount amount;

    private String location;

    @Field("payer_request")
    private String payerRequest;

    @Field("additional_info")
    private List<AdditionalInfo> additionalInfo;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    // Construtor privado - só pode ser chamado pelo Builder
    private Cob(Builder builder) {
        this.id = builder.id;
        this.txId = builder.txId;
        this.key = builder.key;
        this.revision = builder.revision;
        this.statusOperation = builder.statusOperation;
        this.cobCalendar = builder.cobCalendar;
        this.debtor = builder.debtor;
        this.amount = builder.amount;
        this.location = builder.location;
        this.payerRequest = builder.payerRequest;
        this.additionalInfo = builder.additionalInfo != null ?
                List.copyOf(builder.additionalInfo) :
                Collections.emptyList();
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    // Construtor padrão para o MongoDB
    private Cob() {
        this.id = null;
        this.txId = null;
        this.key = null;
        this.revision = 0;
        this.statusOperation = null;
        this.cobCalendar = null;
        this.debtor = null;
        this.amount = null;
        this.location = null;
        this.payerRequest = null;
        this.additionalInfo = Collections.emptyList();
        this.createdAt = null;
        this.updatedAt = null;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTxId() {
        return txId;
    }

    public String getKey() {
        return key;
    }

    public int getRevision() {
        return revision;
    }

    public StatusOperation getStatusOperation() {
        return statusOperation;
    }

    public CobCalendar getCalendar() {
        return cobCalendar;
    }

    public Debtor getDebtor() {
        return debtor;
    }

    public Amount getAmount() {
        return amount;
    }

    public String getLocation() {
        return location;
    }

    public String getPayerRequest() {
        return payerRequest;
    }

    public List<AdditionalInfo> getAdditionalInfo() {
        return additionalInfo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters necessários para o MongoDB
    public void setId(String id) {
        this.id = id;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setRevision(int revision) {
        this.revision = revision;
    }

    public void setStatusOperation(StatusOperation statusOperation) {
        this.statusOperation = statusOperation;
    }

    public void setCobCalendar(CobCalendar cobCalendar) {
        this.cobCalendar = cobCalendar;
    }

    public void setDebtor(Debtor debtor) {
        this.debtor = debtor;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPayerRequest(String payerRequest) {
        this.payerRequest = payerRequest;
    }

    public void setAdditionalInfo(List<AdditionalInfo> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public static class Builder {
        private String id;
        private String txId;
        private String key;
        private int revision;
        private StatusOperation statusOperation;
        private CobCalendar cobCalendar;
        private Debtor debtor;
        private Amount amount;
        private String location;
        private String payerRequest;
        private List<AdditionalInfo> additionalInfo;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder() {
            this.additionalInfo = new ArrayList<>();
            this.createdAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
            this.revision = 0;
        }

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        public Builder withTxId(String txId) {
            this.txId = txId;
            return this;
        }

        public Builder withKey(String key) {
            this.key = key;
            return this;
        }

        public Builder withRevision(int revision) {
            this.revision = revision;
            return this;
        }

        public Builder withStatusOperation(StatusOperation statusOperation) {
            this.statusOperation = statusOperation;
            return this;
        }

        public Builder withLocation(URI location) {
            if (location != null) {
                this.location = location.toString();
            }
            return this;
        }

        public Builder withPayerRequest(String payerRequest) {
            this.payerRequest = payerRequest;
            return this;
        }

        public Builder withCalendar(LocalDateTime createdAt, Integer expiration) {
            CobCalendar cal = new CobCalendar(createdAt, expiration);
            this.cobCalendar = cal;
            return this;
        }

        public Builder withDebtor(String cpf, String cnpj, String name) {
            Debtor deb = new Debtor(cpf, cnpj, name);
            this.debtor = deb;
            return this;
        }

        public Builder withAmount(BigDecimal original, int changeMode) {
            Amount amt = new Amount(original, changeMode);
            this.amount = amt;
            return this;
        }

        public Builder withAdditionalInfo(List<AdditionalInfo> additionalInfo) {
            if (additionalInfo != null) {
                this.additionalInfo = new ArrayList<>(additionalInfo);
            }
            return this;
        }

        public Builder addAdditionalInfo(String name, String value) {
            this.additionalInfo.add(new AdditionalInfo(name, value));
            return this;
        }

        public Builder withCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Cob build() {
            return new Cob(this);
        }
    }
}