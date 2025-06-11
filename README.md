# ⚽ TIKI(축구 경기 매칭 & 팀원 모집 플랫폼)
<br/>
  
## 기획 배경

현재 소속되어 있는 축구 동호회 운영에서 팀 매칭과 인원 모집이 어렵다는 문제를 발견해, 이를 해결할 수 있는 웹 플랫폼을 기획하고 직접 개발했습니다.
<br/><br/>

## 프로젝트 소개

축구팀 간의 매칭과 팀원 모집을 돕는 플랫폼입니다. 팀을 생성하고, 다른 팀과의 매칭을 잡거나 회원 모집 공고를 올려 팀원을 받을 수 있습니다. 팔로우/알림 기능, 공지사항 관리, 팀 관리, 검색 필터 기능 등 다양한 부가 기능도 포함되어 있습니다.
<br/><br/>

## 사용 기술 스택

<div align="center">
  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Java&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=spring&logoColor=white"/>
  <img src="https://img.shields.io/badge/JPA-007396?style=flat&logo=Hibernate&logoColor=white"/>
  <img src="https://img.shields.io/badge/QueryDSL-000000?style=flat&logoColor=white"/>
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=MySQL&logoColor=white"/>
  <img src="https://img.shields.io/badge/Redis-DC382D?style=flat&logo=Redis&logoColor=white"/>
  <img src="https://img.shields.io/badge/Git-F05032?style=flat&logo=Git&logoColor=white"/>
  <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=Swagger&logoColor=black"/>
  <img src="https://img.shields.io/badge/Flyway-CC0200?style=flat&logo=Flyway&logoColor=white"/>
  <img src="https://img.shields.io/badge/SMTP-258FFA?style=flat&logo=Mail.Ru&logoColor=white"/>
  <img src="https://img.shields.io/badge/Kakao%20Map-FFCD00?style=flat&logo=Kakao&logoColor=black"/>
</div>
<br/><br/>

## 주요 기능

### 매칭 기능

* 팀 간 매칭 등록
* 매칭 수락 / 거절 / 취소
* 매칭 상태 변경 (스케줄러 기반)

### 모집 공고 기능

* 팀원 모집 게시글 등록
* 가입 신청 / 수락 / 거절
* 팀원 방출 / 팀 비활성화 기능 지원

### 검색 및 필터링

* 매칭 글 검색
  * 최신순 / 오래된 순 정렬
  * 키워드 검색 (제목)
  * 지역 기반 검색 (시/도, 시/군/구)
  * 날짜 기반 검색

### 팀 관리 기능

* 팀 생성, 수정, 비활성화, 팀원 방출
* 팔로우 기능 (팔로우한 팀의 새 게시글 알림)

### 관리자 기능

* 공지사항 CRUD (Create, Read, Update, Delete)
* 모집/매칭 게시글 삭제

### 인증 및 보안

* 회원가입 이메일 인증 (SMTP)
* 인증번호 Redis 저장
* Spring Security + JWT 로그인 인증

### 팔로우 및 알림

* 팀 간 팔로우 기능
* 팔로우한 팀이 글을 올리면 알림 발송

### 기타 정보

* 주소 → 좌표 변환 (Kakao 지도 API)
* 게시글 및 팀 정보 유효성 검증 (javax.validation)
<br/><br/>

## 기술적 도전과 해결

- **동적 쿼리 처리**: QueryDSL 활용하여 검색 필터 구현
- **보안**: JWT + Spring security
  
---
<br/>

## API 명세서

### 알림 API

![Image](https://github.com/user-attachments/assets/5e7ffcf6-f7ea-4f5d-83cf-006c81aa62f3)

---

### 회원 API

![Image](https://github.com/user-attachments/assets/487e5a4d-9245-4a3d-acf7-00f2d35ecc1e)

---

### 공지사항 API

![Image](https://github.com/user-attachments/assets/773e8eb8-2087-4ee9-b77c-45493878e587)

---

### 팀 API

![Image](https://github.com/user-attachments/assets/313a95c2-f461-4c3f-9975-91d7c935f8f0)

---

### 매칭 신청 API

![Image](https://github.com/user-attachments/assets/d5a8923b-e392-4752-a946-77f9c53ba21d)

---

### 매칭 게시글 API

![Image](https://github.com/user-attachments/assets/e308e1c4-3d2b-4025-975b-0ea84810eb50)

---

### 팔로우 API

![Image](https://github.com/user-attachments/assets/c215631b-c98d-44c8-849f-1d6eb1219624)

---

### 모집글 API

![Image](https://github.com/user-attachments/assets/d9cdcc14-9376-4759-83da-c47b457589fc)

---
### 기타 로직 및 코드
![Image](https://github.com/user-attachments/assets/87bc8ac7-bf24-4ed0-a6cc-6c98327cc475)
회원가입 로직

![Image](https://github.com/user-attachments/assets/5b493c7c-3817-4789-aef7-60f10c5fcb1e)

이메일 전송 및 Redis 저장

<br/><br/>

## 📎 포트폴리오 & 연락처

* 📄 [Notion 포트폴리오 보기](https://www.notion.so/f51c5fa1ceab478a91c406262c8e7f9e)
* 📧 [alsn0527@naver.com](mailto:alsn0527@naver.com)
* 📷 [Instagram @alsndpdh](https://www.instagram.com/alsndpdh)
