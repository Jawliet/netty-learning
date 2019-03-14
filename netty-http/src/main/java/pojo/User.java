package pojo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author JiangJian on 2019/3/6 15:18
 */
public class User {
    @Getter
    @Setter
    private String userName;
    @Getter
    @Setter
    private String method;

    private Date date;

    public Date getDate() {
        return (Date) date.clone();
    }

    public void setDate(Date date) {
        this.date = (Date) date.clone();
    }
}
