package consumer_app.db.models;

import java.util.Date;

public class ResultModel {

    private int id;
    private String value;
    private String primeNumbers;
    private String queueId;
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrimeNumbers() {
        return primeNumbers;
    }

    public void setPrimeNumbers(String primeNumbers) {
        this.primeNumbers = primeNumbers;
    }

    public String getQueueId() {
        return queueId;
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
