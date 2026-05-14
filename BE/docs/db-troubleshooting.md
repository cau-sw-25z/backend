# DB Troubleshooting

## 1. DataSource 설정 오류

### 증상

`.\gradlew clean build` 실행 시 테스트 단계에서 아래와 같은 오류가 발생할 수 있다.

```text
DataSourceProperties$DataSourceBeanCreationException
Failed to configure a DataSource
```

### 원인

`spring-boot-starter-data-jpa`와 MySQL Connector 의존성은 추가되어 있지만,
로컬 환경에서 Spring Boot가 사용할 DB 접속 정보가 설정되어 있지 않으면 발생한다.

특히 `application-local.yml`은 개인 로컬 DB 접속 정보가 포함되므로 GitHub에 올리지 않고,
각자 로컬에서 직접 생성하여 사용한다.

---

## 2. 로컬 DB 실행 확인

본 프로젝트는 로컬 MySQL을 직접 설치하지 않고 Docker MySQL을 사용한다.

DB-Quant 프로젝트 루트에서 Docker Desktop 실행 후 아래 명령어를 실행한다.

```powershell
docker compose up -d
```

또는 환경에 따라 아래 명령어를 사용할 수 있다.

```powershell
docker-compose up -d
```

Docker Desktop에서 MySQL 컨테이너가 실행 중이고, 로그에 아래와 같은 문구가 보이면 정상이다.

```text
ready for connections
```

---

## 3. application-local.yml 설정

백엔드 프로젝트의 아래 위치에 `application-local.yml`을 생성한다.

```text
BE/src/main/resources/application-local.yml
```

예시 구조는 아래와 같다.

```yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quant_db
    username: quant_user
    password: 각자 .env에 설정한 DB_PASSWORD 값
    driver-class-name: com.mysql.cj.jdbc.Driver

    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 30000

  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
```


## 4. local profile 활성화 후 빌드

PowerShell에서 백엔드 프로젝트 루트로 이동한다.

```powershell
cd C:\Users\admin\Documents\GitHub\backend\BE
```

local profile을 활성화한다.

```powershell
$env:SPRING_PROFILES_ACTIVE="local"
```

Gradle 빌드를 실행한다.

```powershell
.\gradlew clean build
```

아래 문구가 출력되면 정상이다.

```text
BUILD SUCCESSFUL
```

---

## 5. 자주 발생하는 문제

| 문제 | 원인 | 해결 |
|---|---|---|
| `DataSourceProperties$DataSourceBeanCreationException` | DB 접속 정보 없음 | `application-local.yml` 생성 및 local profile 활성화 |
| `Access denied for user` | DB 비밀번호 불일치 | Docker `.env`의 `DB_PASSWORD`와 `application-local.yml`의 password 확인 |
| `Unknown database 'quant_db'` | DB가 생성되지 않음 | DB-Quant 프로젝트에서 Docker Compose 재실행 |
| `Connection refused` | MySQL 컨테이너가 실행되지 않음 | Docker Desktop에서 MySQL 컨테이너 실행 상태 확인 |
| `Table 'quant_db.users' doesn't exist` | 초기 SQL 실행 누락 또는 테이블 미생성 | Docker volume 초기화 여부 및 `init.sql` 실행 확인 |

---

## 6. 확인 완료 기록

로컬 Docker MySQL 실행 후 `SPRING_PROFILES_ACTIVE=local` 설정 상태에서 아래 명령어를 실행하였다.

```powershell
.\gradlew clean build
```

결과:

```text
BUILD SUCCESSFUL
```

따라서 로컬 DB 연결 및 JPA Repository 저장/조회 테스트가 정상적으로 동작함을 확인하였다.