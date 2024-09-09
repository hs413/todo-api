## 개요

사용자 기반의 Todo API

- 사용자를 등록하고 로그인을 한 뒤 API에 접근할 수 있다.
- 로그인한 사용자는 할일을 등록하고 관리할 수 있다.
- 조건을 지정하여 조건에 맞는 할일 목록을 조회할 수 있다.
    - 제목 또는 설명에 포함되는 검색어를 입력하여 조회할 수 있다.
    - 상태값을 지정하고 상태값에 해당하는 할일 목록을 조회할 수 있다.
        - 상태: 대기중, 진행중, 종료, 중단
    - 우선순위를 지정하여 우선순위가 높은 순, 낮은 순 등으로 정렬할 수 있다.
- 다른 사용자에게 할일을 공유할 수 있다.
    - 읽기 전용, 수정 가능 권한을 지정할 수 있다.
    - 공유되지 않은 다른 사용자의 할일에는 접근할 수 없다.
- 할일에 대한 알림을 설정할 수 있다.
    - 알림 일시, 반복 횟수, 반복 간격, 반복 단위(분, 시간, 일) 등을 지정하여 해당 일시에 알림이 발송되도록 설정할 수 있다.

## 빌드 및 실행 방법

### **실행환경**

- mac os sonoma 14.4.1
- open jdk 17.0.11
- spring boot 3.3.3
- gradle 8.10
- mysql 9.0.1
- redis-cli 7.2.5

**JDK 17이상, 스프링 부트 3 이상 권장**

### 빌드 및 실행

**mysql database 생성**

1. mysql 접속 후 `create database todo; create database todo_test;` 명령어 실행
    - 로컬 환경 DB와 테스트 DB
2. 테이블은 flyway 마이그레이션을 사용하여 자동 생성

<img src="https://github.com/user-attachments/assets/4f24d8b8-85d5-43be-9300-11cabfc7052e" width="700">


**프로젝트 실행**

1. 깃허브 리포지토리 클론
2. 프로젝트 디렉토리로 이동한 뒤 아래 경로의 .yml 설정 파일 수정
    - `src/main/resources/application.yml`
    - `src/test/resources/application.yml`
    - 위 두 파일에서 datasource 항목의 DB연결 정보 수정
3. 수정 후 프로젝트 루트 디렉토리로 이동하여 `./gradlew build` 명령어 실행
4. 빌드 완료 후 `./build/libs` 경로로 이동한 뒤 `java -jar` 명령어를 사용하여 .jar파일 실행 
5. http://localhost:8080/swagger-ui/index.html 링크로 접속 또는 postman 등을 사용하여 API 테스트 가능
    - 사용자 등록 → 로그인 후 토큰 등록 필수

## API 명세서

[API 명세서](https://www.notion.so/API-47f6227300b84f2bb7403288bf868c7f?pvs=21)

위 링크로 접속하여 확인

## 주요 컴포넌트

### BindingResultHandler

- Spring framework에서 제공하는 validation 을 이용한 유효성 검증 시 발생하는 바인딩 에러를 처리하기 위한 컴포넌트

**BindingResultHandler 적용 전**

```java
// DTO
public record AccountCreateReq(
    @NotBlank(message = "REQUIRED_NAME")
    String username,

    @NotBlank(message = "REQUIRED_EMAIL")
    @Email(message = "INVALID_EMAIL_PATTERN")
    String email,
    
    // ...
) {}

// Controller 
public void createAccount(@Valid @RequestBody AccountCreateReq req) {

    accountService.createAccount(req);
}

// ControllerAdvice
@ExceptionHandler(BindException.class)
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
    Map<String, String> errorMap = new HashMap<>();

    if (e.hasErrors()) {
        BindingResult bindingResult = e.getBindingResult();

        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
    }
    String errorMapString = errorMap.toString();

    log.error("유효성 검증 에러 : {}", errorMapString);

    return ResponseEntity
            .badRequest()
            .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), errorMapString));
}
```

- 위와 같이 DTO를 작성 후 컨트롤러에서 @Valid 에너테이션을 사용하여 검증 시 유효하지 않은 경우 바인딩 에러 발생
- ControllerAdvice에서 BindException을 캐치하여 처리
- 위와 같이 처리할 경우 발생한 에러 메시지를 한 번에 처리해야 하고 에러 메시지의 순서도 일관되지 않음

**에러를 순서대로 처리하기 위해 다음과 같이 수정**

```java
// Controller 
public void createAccount(
		@Valid @RequestBody AccountCreateReq req,
		BindingResult bindingResult
) {
    // bindingResult.contains 메서드는 없지만 이런 방식으로 처리했다는 것을 표현하기 위해 사용
    if (bindingResult.contains(AccountErrorCode.REQUIRED_NAME)) {
        throw new CustomException(AccountErrorCode.REQUIRED_NAME);
    }
		
    if (bindingResult.contains(AccountErrorCode.REQUIRED_EMAIL)) {
        throw new CustomException(AccountErrorCode.REQUIRED_EMAIL);
    }
		
    // ... if 문 반복
		
    accountService.createAccount(req);
}

// ControllerAdvice
@ExceptionHandler(CustomException.class)
public ResponseEntity<ErrorResponse> handleException(CustomException e) {
    ErrorCode errorCode = e.getErrorCode();
    int statusValue = errorCode.getStatus().value();

    ErrorResponse errorResponse =
            new ErrorResponse(statusValue, errorCode.getMessage());

    log.error("사용자 정의 에러 : {}", e.getMessage(), e);
    return ResponseEntity
            .status(statusValue)
            .body(errorResponse);
}
```

- 에러를 직접 핸들링 하기위해 BindingResult 사용
- if 문을 사용하여 순서대로 하나씩 예외처리가 가능하지만 중복 코드가 많아짐

**BindingResultHandler 적용 후**

```java
// BindingResultHandler
public class BindingResultHandler {
    // bindingResult와 errorCode 리스트를 받음
    // errorCode 리스트는 에러를 순서대로 처리하기 위함
    public static void execute(BindingResult bindingResult, List<ErrorCode> errorCodes) {
        // 에러가 없으면 통과
        if (!bindingResult.hasErrors()) {
            return;
        }

        // bindingResult에 등록된 에러 메시지를 set에 저장
        Set<String> errorMessages = new HashSet<>();
        bindingResult.getFieldErrors().forEach(fieldError -> {
            errorMessages.add(fieldError.getDefaultMessage());
        });

        // 에러코드 순서대로 예외처리
        errorCodes.forEach(errorCode -> {
            if (errorMessages.contains(errorCode.getCode())) {
                throw new CustomException(errorCode);
            }
        });
    }
}

// Controller 
public void createAccount(
		@Valid @RequestBody AccountCreateReq req,
		BindingResult bindingResult
) {
		
    BindingResultHandler.execute(bindingResult, List.of(
        AccountErrorCode.REQUIRED_NAME,
        AccountErrorCode.REQUIRED_EMAIL
    ));
		
    accountService.createAccount(req);
}
```

- BindingResultHandler를 사용하여 bindingResult와 에러코드 리스트를 넘겨주면 순서대로 하나씩 예외처리 가능

### CustomPrincipalArgumentResolver

- 사용자 정보(아이디)를 인증정보로부터 가져오기 위한 컴포넌트

```java
// 애너테이션 정의
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomPrincipal {

}

// 유저 정보를 담을 객체
public record UserInfo(
        Long id,
        String username
) {}

// CustomPrincipalArgumentResolver
@Component
public class CustomPrincipalArgumentResolver implements HandlerMethodArgumentResolver {

    // 파라미터에서 @CustomPrincipal 사용 여부를 확인
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(CustomPrincipal.class) != null
                && parameter.getParameterType().equals(UserInfo.class);
    }

    // 파라미터에서 @CustomPrincipal가 사용되었으면 해당 메서드 실행
    // security context에 저장된 인증 정보를 확인해서 UserInfo 객체에 담아 반환 
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken) {
            Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();

            return new UserInfo(
                    Long.parseLong((String) principal.get("id")),
                    (String) principal.get("username")
            );
        }
        return null;
    }
}
```

**사용 예**

```java
@DeleteMapping("{todoId}")
public void deleteTodo(
    @CustomPrincipal UserInfo userInfo,
    @PathVariable Long todoId
) {
    todoService.todoDelete(userInfo.id(), todoId);
}
```

- @CustomPrincipal 애너테이션을 사용하여 UserInfo 객체를 받아 유저 아이디를 사용
