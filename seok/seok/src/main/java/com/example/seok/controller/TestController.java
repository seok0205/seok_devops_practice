package com.example.seok.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
@Tag(name = "데브옵스 실험용 API", description = "모니터링 및 로그 수집 테스트를 위한 자극용 API 세트")
public class TestController {

    @GetMapping("/log")
    @Operation(summary = "로그 뿜어내기 API", description = "Loki/ELK 수집 테스트를 위해 다양한 레벨의 로그를 강제로 남깁니다.")
    public String generateLogs() {
        log.info("인포 레벨 로그 발생 - 사용자가 로그 API를 호출함");
        log.warn("워닝 레벨 로그 발생 - 주의가 필요한 상황을 시뮬레이션함");
        log.error("에러 레벨 로그 발생 - 의도적인 런타임 에러 예외 상황 기록");
        return "로그 콘솔에 기록 ";
    }

    @GetMapping("/cpu-load")
    @Operation(summary = "CPU 과부하 유도 API", description = "쿠버네티스 Auto Scaling(HPA) 테스트용 부하 발생 로직")
    public String causeCpuLoad() {
        log.info("CPU 부하 테스트 시작...");
        long startTime = System.currentTimeMillis();

        // 약 2~3초간 CPU를 강제로 일하게 만드는 반복문 (자신의 PC 사양에 맞게 조절 가능)
        while (System.currentTimeMillis() - startTime < 3000) {
            Math.sin(Math.random());
        }

        log.info("CPU 부하 테스트 종료");
        return "CPU 3초간 매운맛 연산";
    }

    @GetMapping("/delay")
    @Operation(summary = "응답 지연(톰캣 스레드 고갈) API", description = "트래픽이 밀릴 때 그라파나 대시보드 변화를 보기 위한 API")
    public String simulateDelay() throws InterruptedException {
        log.info("지연 API 호출 - 톰캣 스레드 점유 시작");
        Thread.sleep(2500); // 2.5초간 스레드를 붙잡아 둡니다.
        log.info("지연 API 호출 완료");
        return "2.5초 응답하는 API";
    }
}