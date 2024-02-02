FROM openjdk:17-jdk AS builder

# env
ENV USE_PROFILE aws
ENV HOST_NAME localhost
ENV SCHEMA a708
ENV USERNAME a708Drwa
ENV PASSWORD lmx14987
ENV JWT_SECRET do-not-spoil-what-you-have-by-desiring-what-you-have-not2024!
ENV REDIS_HOST a708
ENV REDIS_PASS lmx14987
ENV OV_HOST HOST
ENV OV_SECRET SECRET 

# necessary files copy
COPY backend/gradlew .
COPY backend/gradle gradle
COPY backend/build.gradle .
COPY backend/settings.gradle .
COPY backend/src src

# 권한 변경
RUN chmod +x ./gradlew
# 백엔드 빌드
RUN ./gradlew clean bootJar

FROM openjdk:17-jdk
# 빌드 파일 복사
COPY --from=builder build/libs/*.jar app.jar
# 포트 8081 공개
EXPOSE 8081
# 환경변수 적용 및 실행
ENTRYPOINT ["java", "-jar", "-Dspring.config.activate.on-profile=${USE_PROFILE}", "-Ddatabase.hostname=${HOST_NAME}", "-Ddatabase.schema=${SCHEMA}", "-Ddatabase.username=${USERNAME}", "-Ddatabase.password=${PASSWORD}", "-Djwt.secret=${JWT_SECRET}", "-Dredishost=${REDIS_HOST}", "-Dredispass=${REDIS_PASS}", "-Dopenvidu.hostname=${OV_HOST}", "-Dopenvidu.secret=${OV_SECRET}", "/app.jar"]
