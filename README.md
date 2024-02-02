# A708 
# ![캡처-removebg-preview](/uploads/48eb51623a0fd66c4f9834feda6aca61/캡처-removebg-preview.png) Drwa (Debate refined wisdom arena)
## 토론 게임 서비스
- 재미와 논리적 사고력을 기를 수 있는 다양한 주제의 토론 게임 플랫폼

|팀원|Backend|Frontend|
|:---|:---:|:---:|
|김지환|:white_check_mark:|:white_check_mark:|
|곽민우|:white_check_mark:|:white_check_mark:|
|이경태|:white_check_mark:|:white_check_mark:|
|이동현|:white_check_mark:|:white_check_mark:|
|지준호|:white_check_mark:|:white_check_mark:|
|차다운|:white_check_mark:|:white_check_mark:|

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
|할 일|월요일|화요일|수요일|목요일|금요일|
|:---|:---:|:---:|:---:|:---:|:---:|
|1주차|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|
|2주차|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|
|3주차|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|
|4주차|:white_check_mark:|:white_check_mark:|:white_check_mark:|:white_check_mark:|⬜|
|5주차|⬜|⬜|⬜|⬜|⬜|
|6주차|⬜|⬜|⬜|⬜|⬜|

### 팀원별 맡은 기능 / 진행상황

#### Infra - 이경태
- Jenkins, nginx 연결 후 배포

#### 소셜 로그인 - 곽민우
- kakao, naver, google

#### 랭킹 / 칭호 기능 백엔드 - 이동현
- 카테고리 별 랭킹 Redis 저장 및 조회 기능
- 칭호 저장

#### 토론 방 생성 / 입장 - 김지환
- 방 생성 / 입장 시 백엔드 요청 및 Redis 통신
- 채널 생성은 아직 X

#### 토론 진행 - 이경태
- 토론 시작 후 백엔드 요청 및 Redis 통신

#### 전적 저장 - 지준호
- 토론 종료 후 Redis 를 통해 전적에 저장되는 정보만 DB에 저장
- 멤버 전적에 추가

#### ElasticSearch 검색 - 차다운
- Elasticsearch의 nori분석기를 활용하여 검색어를 통해 redis에 있는 방 정보를 가져오기

#### Frontend
- 상단 nav 바 - 곽민우
- 메인페이지 케러셀 - 김지환
- openvidu session 연결 - 김지환


