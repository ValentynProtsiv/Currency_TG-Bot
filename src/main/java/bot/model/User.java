package bot.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "usersDataTable")
public class User {
    @Id
    private Long chatId;
    private String userName;
    private Integer bankName;
    private Integer numberAFP;
    private Integer currency;
    private Integer hour;

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", userName='" + userName + '\'' +
                ", bankName=" + bankName +
                ", numberAFP=" + numberAFP +
                ", currency=" + currency +
                ", hour=" + hour +
                '}';
    }
}