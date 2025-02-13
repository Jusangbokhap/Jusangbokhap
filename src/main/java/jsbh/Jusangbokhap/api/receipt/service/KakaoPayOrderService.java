package jsbh.Jusangbokhap.api.receipt.service;

import jsbh.Jusangbokhap.api.receipt.dto.KakaoPayOrderResponseDto;
import jsbh.Jusangbokhap.common.config.KakaoPayProperties;
import jsbh.Jusangbokhap.common.exception.CustomException;
import jsbh.Jusangbokhap.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoPayOrderService {

    private final KakaoPayProperties kakaoPayProperties;
    private final RestTemplate restTemplate;

    public KakaoPayOrderResponseDto getOrderStatus(String tid) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoPayProperties.getAdminKey());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("tid", tid);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<KakaoPayOrderResponseDto> responseEntity = restTemplate.exchange(
                kakaoPayProperties.getOrderUrl(),
                HttpMethod.POST,
                request,
                KakaoPayOrderResponseDto.class
        );

        if (responseEntity.getStatusCode() != HttpStatus.OK || responseEntity.getBody() == null) {
            throw new CustomException(ErrorCode.KAKAO_PAY_API_ERROR);
        }

        return responseEntity.getBody();
    }
}
