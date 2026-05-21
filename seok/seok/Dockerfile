# [핵심] 이 도커파일은 빌드킷(BuildKit) 최신 문법을 사용하겠다고 엔진에 선언합니다.
# syntax=docker/dockerfile:1

# ==========================================
# 1단계: 빌드 및 테스트 스테이지 (Build Stage)
# ==========================================
FROM gradle:9.0-jdk17 AS builder
WORKDIR /app

COPY build.gradle settings.gradle ./

# 빌드킷의 캐시 마운트 기능을 활용해 라이브러리 다운로드 가속화
RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle build -x test --no-daemon || true

COPY src ./src

RUN --mount=type=cache,target=/home/gradle/.gradle \
    gradle bootJar --no-daemon

# ==========================================
# 2단계: 최종 실행 스테이지 (Run Stage)
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENV APP_NAME=seok

ENTRYPOINT ["java", "-jar", "app.jar"]