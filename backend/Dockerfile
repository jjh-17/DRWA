FROM openjdk:17-jdk-alpine AS builder

# env
ENV ON_PROFILE ON_PROFILE
ENV DB_HOST DB_HOST
ENV MYSQL_DATABASE MYSQL_DATABASE
ENV MYSQL_USER MYSQL_USER
ENV MYSQL_PASSWORD MYSQL_PASSWORD
ENV REDIS_PASS REDIS_PASS
ENV JWT_KEY JWT_KEY
ENV KAKAO_CLIENT_ID KAKAO_CLIENT_ID
ENV KAKAO_CLIENT_SECRET KAKAO_CLIENT_SECRET
ENV GOOGLE_CLIENT_ID GOOGLE_CLIENT_ID
ENV GOOGLE_CLIENT_SECRET GOOGLE_CLIENT_SECRET
ENV NAVER_CLIENT_ID NAVER_CLIENT_ID
ENV NAVER_CLIENT_SECRET NAVER_CLIENT_SECRET
ENV OPENVIDU_HOSTNAME OPENVIDU_HOSTNAME
ENV OPENVIDU_SECRET OPENVIDU_SECRET
ENV ELK_USER ELK_USER
ENV ELK_PASS ELK_PASS
# env end

# necessary files copy
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

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
ENTRYPOINT ["java", "-jar", "-Dspring.config.activate.on-profile=${ON_PROFILE}", "-Ddatabase.hostname=${DB_HOST}", "-Ddatabase.schema=${MYSQL_DATABASE}", "-Ddatabase.username=${MYSQL_USER}", "-Ddatabase.password=${MYSQL_PASSWORD}", "-Djwt.key=${JWT_KEY}", "-Dredishost=${DB_HOST}", "-Dredispass=${REDIS_PASS}", "-Dkakao.id=${KAKAO_CLIENT_ID}", "-Dkakao.secret=${KAKAO_CLIENT_SECRET}", "-Dgoogle.id=${GOOGLE_CLIENT_ID}", "-Dgoogle.secret=${GOOGLE_CLIENT_SECRET}", "-Dnaver.id=${NAVER_CLIENT_ID}", "-Dnaver.secret=${NAVER_CLIENT_SECRET}", "-Dopenvidu.hostname=${OPENVIDU_HOSTNAME}", "-Dopenvidu.secret=${OPENVIDU_SECRET}", "-Delk.user=${ELK_USER}", "-Delk.pass=${ELK_PASS}", "/app.jar"]
