package com.showtime.gameservice.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "userEmail")
public class UserInfo {


    private Long userId;
    private String userEmail;
    private String userName;
    private String nickName;

}
