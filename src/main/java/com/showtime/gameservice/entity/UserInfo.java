package com.showtime.gameservice.entity;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(of = "userId")
public class UserInfo {


    private String userId;
    private String userName;
    private String nickName;

}
