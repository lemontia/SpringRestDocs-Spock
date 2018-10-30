package demonic.springdocs.sample;

import lombok.Data;

@Data
public class User {

    /**
     * 이름
     */
    private String name;
    /**
     * 나이
     */
    private int age;
    /**
     * 주소
     */
    private String address;
    /**
     * 인삿말
     */
    private String comment;
}
