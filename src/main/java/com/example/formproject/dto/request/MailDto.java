package com.example.formproject.dto.request;

import com.example.formproject.FinalValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailDto {
    @Schema(type = "email",example = "example@abcd.com")
    private String email;

//    public String buildContent(String confirmChars) {
//        StringBuffer br = new StringBuffer();
//        br.append("<html><body>");
//        br.append("<meta http-equiv='Content-Type' content='text/html; charset=euc-kr'>");
//        br.append("<h1>" + FinalValue.APPLICATION_TITLE + "</h1><br>");
//        br.append("<b>농담 : 농사를 한눈에 담다 회원가입 인증 메일입니다.</b><br>");
//        br.append("<b>아래에 보이는 문자를 입력해 주세요.</b><br>");
//        br.append("<h1>" + confirmChars + "</h1>");
//        br.append("</body><html>");
//        return br.toString();
//    }
    public String buildContent(String href){
        StringBuffer br = new StringBuffer();
        br.append("<html>" +
                "    <head>\n" +
                "        <link rel='stylesheet' href='ttps://cdn.jsdelivr.net/npm/bootstrap@4.4.1/dist/css/bootstrap.min.css' integrity='sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh' crossorigin='anonymous'>"+
                "    </head>" +
                "<body>");
        br.append("<meta http-equiv='Content-Type' content='text/html; charset=euc-kr'>");
        br.append("<h1>" + FinalValue.APPLICATION_TITLE + "</h1><br>");
        br.append("<b>농담 : 농사를 한눈에 담다 회원가입 인증 메일입니다.</b><br>");
        br.append("<b>아래에 보이는 버튼을 클릭하여 회원가입을 마무리 해 주세요.</b><br>");
        br.append("<a class='btn btn-outline-primary' href='"+href+"'>회원가입 완료하기</a>");
        br.append("</body><html>");
        return br.toString();
    }
}
