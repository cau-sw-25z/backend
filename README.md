# Backend

## 프로젝트 소개
주식 포트폴리오 관리 서비스의 백엔드 레포지토리입니다.

## 기술 스택
- Java 17
- Spring Boot 3.x
- Gradle
- MySQL

## 로컬 실행 방법
1. 레포 클론
git clone https://github.com/cau-sw-25z/backend.git
2. `src/main/resources/`에 `application-local.yml` 추가
3. 실행
./gradlew bootRun

## 환경변수
| 변수명 | 설명 |
|--------|------|
| DB_PASSWORD | MySQL 비밀번호 |

## 브랜치 전략
- `main` : 최종 배포
- `dev` : 통합 브랜치  
- `feature/*` : 기능 개발

## 커밋 컨벤션
| 타입 | 설명 |
|------|------|
| feat | 새로운 기능 |
| fix | 버그 수정 |
| docs | 문서 수정 |
| refactor | 리팩토링 |
| chore | 빌드/설정 변경 |