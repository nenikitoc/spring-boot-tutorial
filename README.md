# Руководство по spring-boot
Краткое руководство по spring-boot. Включает в себя основные понятия и примеры использования.

## Содержание
- [Руководство по spring-boot](#руководство-по-spring-boot)
  - [Содержание](#содержание)
  - [Способы создания Bean-ов](#способы-создания-bean-ов)
  - [Зависимости](#зависимости)
  - [HTTP-запросы и REST](#http-запросы-и-rest)
  - [Spring Boot Starter WEB](#spring-boot-starter-web)
  - [Команда проекта](#команда-проекта)
  - [Источники](#источники)

## Способы создания Bean-ов
Чтобы создать Bean и поместить его в Application Context, необходимо как-то пометить класс.  
__При помощи аннотаций.__ @Component (базовый), @Service, @Repository, @RestController. @Autowired - аннотация указывающая на метод, который будет использован для создания Bean-а.

```Java
@Service
public class UserService{
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
  }
}
```

__При помощи класса-конфигурации.__ @Configuration. В классе-конфигурации указываються все Bean-ы, которые нужно создать, и их способы создания. В проекте может быть несколько классов-конфигураций.

```Java
@Configuration
public class AppConfig{
  @Bean
  public UserRepository userRepository(){
    return new userRepository();
  }

  @Bean
  public UserService userService(UserRepository userRepository){
    UserService userService = new UserService();
    userService.setUserRepository(userRepository);
    return userService;
  }
}
```

## Зависимости
Если в создании класса принимают участие другие классы, то в создающемся классе нужно прописать зависимости. Для этой операции используеться аннотация @Autowired.  
__Field injection__
```Java
@Service
public class UserService{
  @Autowired // Несмотря на private поле будет принудительно заполнено с помощью рефлексию
  private UserRepository userRepository;

  public UserService(){

  }
}
```
__Setter injection__
```Java
@Service
public class UserService{
  private UserRepository userRepository;

  public UserService(){

  }

  @Autowired
  public UserService setUserRepository(UserRepository userRepository){
    this.userRepository = userRepository;
  }
}
```
__Constructor injection__
```Java
@Service
public class UserService{
  private UserRepository userRepository;

  @Autowired // можно не указывать явно, spring сам поймет, что это Autowired-конструктор
  public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
  }
}
```

## HTTP-запросы и REST
__HTTP (HyperText Transfer Protocol)__ - протокол передачи данных в сети интернет. Запрос и ответ имеют четкие структуры соответственно. Клиент посылает запрос (request), а Сервер отвечает (response).  

__Из чего состоит HTTP request:__
* Тип запроса:  
  * GET (получение данных)
  * POST (сохранение данных)
  * PUT (замена ресурса)
  * DELETE (удаление)
  * PATCH (частичное изменение)  
  и т.д.
* Адрес ресурса (путь): /user/registrations
* Адрес хоста (куда отправить запрос): host:booking.com
* Заголовки (Headers) - вспомогательные данные  
  * Autorization: ASdhajsfwr7cabeacem
  * Content-Type: application/json
  * Accept: application/text  
  и т.д.
* Тело запроса - информация запроса, нужна, если мы хотим сохранить информацию о запросе. Тело может быть представленно в различных форматах, чаще json  

__Из чего состоит HTTP response:__

* Коды ответов:
  * 1xx - Информационные коды
  * 2xx - Успешные коды
  * 3xx - Перенаправление
  * 4xx - Ошибка клиента
  * 5xx - Ошибка сервера
* Заголовки:
  * Date: Fri, 7 Oct 2024 16:10:23 GMT
  * Context-Type: application/json
  * Set-Cookie: name=user123
* Тело ответа - информация ответа, чаще всего json

__REST (Representational State Transfer)__ - это архитектурный стиль, использующий HTTP для взаимодействия с ресурсами.
Принципы REST:
* Клиент-Серверное взаимодействие
* Statless сервер - сервер не хранит информацию о клиенте
* Кэширование запросов - сервер может запомнить какие-то запросы
* Многослойная система
* Единый интерфейс.  
  
Все является ресурсом. У каждого ресурса свой путь. Нужен унифицированный интерфейс. Пример:  
  * Список пользователей: http://api.example.com/users  
  * Конкретный пользователь: http://api.example.com/users/123  
  * Список заказов пользователя: http://api.example.com/users/123/orders  
  
Манипуляция с ресурсами через стандарные HTTP методы.  
  * Список пользователей: GET/users  
  * Получить пользователя: GET/users/123  
  * Создать пользователя: POST/users  
  * Обновить пользователя: PUT/users/123  
  * Удалить пользователя: DELETE/users/123  


## Spring Boot Starter WEB  
__Spring Boot Starter WEB__ - стартер из Spring Boot, который дает инструменты для приема и обработки HTTP-запросов. Упрозает работу с HTTP, обработку ошибок, валидацию и тестирование.  
Обработка разных типов запросов:
* GET -> @GetMapping("/users")
* POST -> @PostMapping
* PUT -> @PutMapping
* DELETE -> @DeleteMapping("/users/{id}")
* PATCH -> @PatchMapping

Вспомогательные классы и аннотации:
* `@RequestBody` - заполняет класс после себя данными из http-запроса
* `ResponseEntity<>` - позволяет управлять ответом, пример: `ResponseEntity.header("test-header", "123").status(201).body(response)`

Решение проблемы единства интерфейса:
в аннотацию @GetMapping автоматически добавиться путь из @RequestMapping, и мы получим @GetMapping("/reservation/{id}"), соответсвенно можно сделать с каждым запросом в классе.
```Java
@RestController
@RequestMapping("/reservation")
public class ReservationController{
  @GetMapping("/{id}")
  public ResponseEntity<Reservation> getReservationById(
            @PathVariable("id") Long id
    ){
        log.info("Called getReservationById: id = " + id);
        return ResponseEntity.status(HttpStatus.OK).body(reservationService.getReservationById(id));
    }
}
```


## Команда проекта
Оставьте пользователям контакты и инструкции, как связаться с командой разработки.

- [Богдан Звягинцев](tg://resolve?domain=bzvyagintsev) — Front-End Engineer

## Источники
Если вы чем-то вдохновлялись, расскажите об этом: где брали идеи, какие туториалы смотрели, ссылки на исходники кода. 