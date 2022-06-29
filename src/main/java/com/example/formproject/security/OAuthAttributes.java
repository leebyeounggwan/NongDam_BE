package com.example.formproject.security;

import com.example.formproject.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
public class OAuthAttributes {
    private Map<String, Object> attributes;

    private long id;

    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    public static OAuthAttributes of(String serviceType, String userNameAttributeName, Map<String, Object> attributes) {
        if (serviceType.equals("naver"))
            return ofNaver(userNameAttributeName, attributes);
        else if (serviceType.equals("google"))
            return ofGoogle(userNameAttributeName, attributes);
        else
            return ofKakao(userNameAttributeName, attributes);
    }


    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public void setMember(Member member) {
        Map<String, Object> attr = new HashMap<>();
        for (String key : attributes.keySet()) {
            attr.put(key, attributes.get(key));
        }
        attr.put("member", member);
        this.attributes = attr;
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> attr = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) attr.get("name"))
                .email((String) attr.get("email"))
                .picture((String) attr.get("profile_image"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> accountInfo = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) accountInfo.get("profile");
        return OAuthAttributes.builder()
                .name((String) profile.get("nickname"))
                .email((String) accountInfo.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}
