package vn.duyhai.jobhunter.domain.response;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;
import vn.duyhai.jobhunter.util.constant.GenderEnum;


@Getter
@Setter
public class ResCreateUserDTO {
    private long id;
    private String name;
    private String email;
    private int age;
    private GenderEnum gender;
    private String address;
    private Instant createdAt;
    private CompanyUser companyUser;


    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }
    

}
