# A708 

# ![캡처-removebg-preview](/uploads/48eb51623a0fd66c4f9834feda6aca61/캡처-removebg-preview.png) Drwa (Debate refined wisdom arena)

## 토론 게임 서비스

- 재미와 논리적 사고력을 기를 수 있는 다양한 주제의 토론 게임 플랫폼

## Member

| 팀원   |      Backend       |      Frontend      |
| :----- | :----------------: | :----------------: |
| 김지환 | :white_check_mark: | :white_check_mark: |
| 곽민우 | :white_check_mark: | :white_check_mark: |
| 이경태 | :white_check_mark: | :white_check_mark: |
| 이동현 | :white_check_mark: | :white_check_mark: |
| 지준호 | :white_check_mark: | :white_check_mark: |
| 차다운 | :white_check_mark: | :white_check_mark: |

## 📑 프로젝트 개요
- 진행기간 : 2024.01.08 ~ 2024.02.16 (6주)
- Ssafy 공통 프로젝트
- 배경 : 인공지능 시대에 자기만의 생각을 정리하고 표출하는 것이 중요하다. 그러나 대한민국 학생과 사람들은 토론 문화에 익숙지 않다. 대한민국 사람들이 인공지능 시대에 살아남을 수 있도록 역량을 기르기 위한 토론 플랫폼을 재미를 더해서 쉽게 접근할 수 있도록 제공한다. 실제 토론을 컨텐츠한 인터넷 방송이 엄청난 인기를 끌면서 사람들의 관심도가 높다는 것을 알 수 있었다.

## 🧮 프로젝트 소개
Drwa는 webRTC를 기반으로 한 실시간 화상채팅을 이용한 토론 게임입니다. 자신이 토론하고 싶은 주제로 방을 생성하여 상대 토론자를 기다리세요 ! 게임이 시작되면 Drwa가 방설정 정보를 토대로 사회자 역할을 대신 해드립니다. 내 발언시간에 상대팀은 모두 '마이크 X' ! 자신의 의견을 주장해보아요. 배심원을 설득해 투표를 과반수 얻게되면 승리하여 포인트를 얻게 됩니다. 자신의 랭크 또한 확인해보세요 !

## 💡 주요 기능
1. 시스템 사회자
2. 재접속
3. Unsplash API를 이용한 썸네일 지정
4. ElasticSearch를 이용한 검색 기능
5. 랭킹

## 진행상황

### 스크럼
| 할 일 |       월요일       |       화요일       |       수요일       |       목요일       |       금요일       |
| :---- | :----------------: | :----------------: | :----------------: | :----------------: | :----------------: |
| 1주차 | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 2주차 | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 3주차 | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |
| 4주차 | :white_check_mark: | :white_check_mark: | :white_check_mark: | :white_check_mark: |         ⬜          |
| 5주차 |         ⬜          |         ⬜          |         ⬜          |         ⬜          |         ⬜          |
| 6주차 |         ⬜          |         ⬜          |         ⬜          |         ⬜          |         ⬜          |

### 팀원별 맡은 기능 / 진행상황

#### Infra - 이경태

- [x] DB Server on EC2

- [ ] jenkins CI / CD pipeline 구축
  - [x] Backend
  - [ ] Frontend

#### 소셜 로그인 - 곽민우
- kakao, naver, google OAuth2 구현
- JWT AccessToken, RefreshToken 발급
- RefreshToken Redis에 저장 및 silent-refresh 기능 구현

#### 랭킹 / 칭호 기능 백엔드 - 이동현
- 카테고리 별 랭킹 Redis 저장 및 조회 기능
- 칭호 저장

#### 토론 방 생성 / 입장 - 김지환
- [x] 토론 생성
  - [x] Openvidu session 반환
  - [x] 카테고리 별 db에 저장
- [ ] 토론 입장
  - [x] Openvidu token 생성
  - [ ] 플레이어가 모두 차면 시작 권한 생성
- [ ] Redis PUB/SUB, Message 

#### 토론 진행 - 이경태

- [ ] 토론 시작
  - [x] 시작 시 방 정보, 참여자 정보 저장
  - [ ] Redis PUB/SUB으로 방 전체 채널, 각 팀 채널 구독 처리
  - [x] 데이터 저장 후 준비 단계로 진행

- [ ] 단계 자동 진행
  - [x] 설정한 시간이 지나면 Scheduler를 이용해 다음 단계로 자동 진행
  - [ ] Redis PUB/SUB으로 SSE 처리

- [ ] 신고 기능

- [ ] 투표 기능
  - [ ] 토론 마지막 단계에서 클라이언트의 투표 정보를 계산 후 Redis에 결과 저장 

#### 전적 저장 - 지준호
- [x] 전적, 게임 정보 엔티티 구현
- [x] 전적, 게임 정보 테이블 과의 통신 메서드 구현
- [x] Redis에 저장된 토론방 공개 여부에 따라 다른 서비스를 제공하도록 구현
- [x] 공개방 여부에 따라 Redis에서 가져올 데이터를 달리함.
- [x] 공개방 종료 시에는 투표 정산을 수행
	- [x] 각 팀의 투표 수에 따라 승/패/비김 여부를 결정
	- [x] MVP 투표 수에 따라 최종 MVP를 선정한다. 동점자가 있을 경우, 동점자 중 랜덤으로 한명을 선정
- [x] 사설방 종료 시에는 정산 스킵

#### ElasticSearch 검색 - 차다운
- [x] Elasticsearch 서버와의 통신 메서드 구현
- [x] Elasticsearch 인덱스 생성 및 인덱스가 없을 경우의 인덱스 생성 메서드 구현
- [x] Elasticsearch에서 제목 기반으로 방을 검색하고, 검색 결과에 해당하는 Redis의 방 정보를 조회하여 반환하는 메서드 구현
- [x] Elasticsearch에서 제시어 기반으로 방을 검색하고, 검색 결과에 해당하는 Redis의 방 정보를 조회하여 반환하는 메서드 구현
- [ ] 기능 테스트

#### Frontend
- [x] 상단 nav 바 - 곽민우
- [x] 메인페이지 케러셀 - 김지환
- [x] openvidu session 연결 - 김지환

### 기술 스택

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

#### 명세서

[기능 명세서](https://discovered-lemongrass-789.notion.site/2c04cab8ab864f1caf205112e58a76b2?v=c197501aa796455fabc3eb0a913ff680)

[API 명세서](https://discovered-lemongrass-789.notion.site/6085e93cfd0441028830c2de640f3f00?v=6d2c1d313382493a87cb396067ce9bdf&pvs=4)

[Convention](./documents/convention/convention.md)
