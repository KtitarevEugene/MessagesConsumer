package consumer_app.db.models;

import java.util.Date;
import java.util.List;

public class ResultModel {

    private int id;
    private String value;
    private List<Integer> primeNumbers;
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

    public List<Integer> getPrimeNumbers() {
        return primeNumbers;
    }

    public void setPrimeNumbers(List<Integer> primeNumbers) {
        this.primeNumbers = primeNumbers;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
