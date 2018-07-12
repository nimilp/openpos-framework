package org.jumpmind.pos.trans.model;

import java.util.Date;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Table;

@Table
public class Transaction extends TransactionEntity {

    @Column
    private int transType;

    @Column
    private int transStatus;

    @Column
    private String businessUnitId;

    @Column
    private String operatorUsername;

    @Column
    private Date beginTime;

    @Column
    private Date endTime;

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public int getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(int transStatus) {
        this.transStatus = transStatus;
    }

    public String getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(String businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public String getOperatorUsername() {
        return operatorUsername;
    }

    public void setOperatorUsername(String operatorUsername) {
        this.operatorUsername = operatorUsername;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    
}