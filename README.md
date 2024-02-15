# A708 

# ![캡처-removebg-preview](/uploads/48eb51623a0fd66c4f9834feda6aca61/캡처-removebg-preview.png) Drwa (Debate Refined Wisdom Arena)

## 토론 게임 서비스

- 재미와 논리적 사고력을 기를 수 있는 다양한 주제의 토론 게임 플랫폼

## 💡 UCC

## Member

| 팀원   |      Backend       |      Frontend      |
| :----- | :----------------: | :----------------: |
| 김지환 | :white_check_mark: | :white_check_mark: |
| 곽민우 | :white_check_mark: | :white_check_mark: |
| 이경태 | :white_check_mark: | :white_check_mark: |
| 이동현 | :white_check_mark: | :white_check_mark: |
| 지준호 | :white_check_mark: | :white_check_mark: |
| 차다운 | :white_check_mark: | :white_check_mark: | 

## 🚩 목차
1. 프로젝트 개요
2. 서비스 소개
3. 기능 소개
4. 시연
5. 기술 스택
6. 기타 산출물


## 📑 프로젝트 개요
#### 개요
 - 재미와 논리적 사고력을 기를 수 있는 다양한 주제의 '토론 게임 플랫폼'
 - 서비스 명 : DRWA(Debate Refined Wisdom Arena)

#### 진행기간 
 - 2024.01.08 ~ 2024.02.16 (6주)

#### 목적
 - 자신이 원하는 주제로 토론을 가볍게 즐기자
 - 말하는 것이 두려운 사람들에게 도움이 되는 게임을 만들자

####  배경
 - 인공지능 시대에 자기만의 생각을 정리하고 표출하는 것이 중요하다. 그러나 대한민국 학생과 사람들은 토론 문화에 익숙지 않다. 대한민국 사람들이 인공지능 시대에 살아남을 수 있도록 역량을 기르기 위한 토론 플랫폼을 재미를 더해서 쉽게 접근할 수 있도록 제공한다.
 - 실제 토론을 컨텐츠한 인터넷 방송이 엄청난 인기를 끌면서 사람들의 관심도가 높다는 것을 알 수 있었다. 이에 토론을 가볍게 즐기고 싶은 사람들이 자신이 원하는 재미있는 주제로 직접 참여할 수 있도록 한다.
 - 사람들 앞에서 말하는 것이 두려운 사람들이나 면접상황에서 자신의 의견을 표출하는 것이 힘든 사람들에게 주장, 발표 능력을 기를 수 있도록 한다.

## 🧮 서비스 소개
#### 페르소나
 - 모범생 : 자유롭게 토론하며 토론 역량 기르고 싶은 사람
 - 수강생 : 자유로운 토론을 보고 배우고 싶은 사람
 - 언변꾼 : 자신의 의견을 표현하고 설득하러 온 사람
 - 구경꾼 : 개판 5분 전 토론을 보면서 즐기고 싶은 사람

#### 서비스 간단 소개
 - Drwa는 webRTC를 기반으로 한 실시간 화상채팅을 이용한 토론 게임입니다. 자신이 토론하고 싶은 주제로 방을 생성하여 상대 토론자를 기다리세요 ! 게임이 시작되면 Drwa가 방설정 정보를 토대로 사회자 역할을 대신 해드립니다. 내 발언시간에 상대팀은 모두 '마이크 X' ! 자신의 의견을 주장해보아요. 배심원을 설득해 투표를 과반수 얻게되면 승리하여 포인트를 얻게 됩니다. 자신의 랭크 또한 확인해보세요 !

## 💡 기능 소개
1. 시스템 사회자
2. 재접속
3. Unsplash API를 이용한 썸네일 지정
4. ElasticSearch를 이용한 검색 기능
5. 랭킹

## 시연


## 기술 스택

- #### BackEnd
  - Openjdk17
  - SpringBoot 3.2.1
  - Spring Data JPA
  - Spring Security
  - QueryDSL
  - Swagger 3.0.0
  - jjwt 0.9.1

- #### FrontEnd
  - Vue3
  - Axios
  - Quasar
  - Pinia
  -  

- #### Database
  - MySQL 8.3.0
  - Redis 7.2.4

- #### Infra
  - Docker 25.0.0
  - Jenkins 2.426.2
  - Nginx 1.18.0
  - AWS EC2

#### Project Architecture

![아키텍쳐](./documents/architecture/아키텍쳐.png)


## 기타 산출물

[기능 명세서](https://discovered-lemongrass-789.notion.site/2c04cab8ab864f1caf205112e58a76b2?v=c197501aa796455fabc3eb0a913ff680)

[API 명세서](https://discovered-lemongrass-789.notion.site/6085e93cfd0441028830c2de640f3f00?v=6d2c1d313382493a87cb396067ce9bdf&pvs=4)

[Convention](./documents/convention/convention.md)
