# Руководство по spring-boot
Краткое руководство по spring-boot. Включает в себя основные понятия и примеры использования.

## Содержание
- [Способы создания Bean-ов](#способы-создания-bean-ов)
- [Зависимости](#зависимости)
- [HTTP-запросы и REST](#http-запросы-и-rest)
- [Spring Boot Starter WEB](#spring-boot-starter-web)
- [Docker](#docker)
- [Hibernate](#hibernate)

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

## Docker
Docker - платформа для разработки, доставки и эксплуатации приложения с использованием контейнерезации.  
Позволяет разработчику упаковывать приложения и их зависимости в контейнеры, которые можно запускать в любой среде, вне зависимости от различий в конфигурации систем.  
Образы и контейнеры:
* Dockerfile - это текстовый файл, содержащий набор инструкций для сборки Docker0образа.
* Docker image - шаблон, который используется для создания контейнеров. Образ содержит все для запуска приложения, включая код, runtime, библиотеки и зависимости, а также параметры конфигурации.
* Docker Container - это экземпляр, созданный на основе Docker образа.
Команды:
* docker -v - версия
* docker ps - все запущенные контейнеры
* docker images - список имаджей
* docker pull - можно подтянуть стандарный образ из docker hub (docker pull postgres:latest)
* docker run --name my-postgres -e POSTGRES_PASSWORD=root -p 5432:5432 -d postgres - создание контейнера postgres (--name {name} - задание имени, -e POSTGRES_PASSWORD={password} - задание пароля, -p {port} - задание порта, -d - запуск в детач моде)
* docker start {name} - запуск контейнера

Чтобы подключиться к базе данных в контейнере, необходимо использовать клиент для подключения.

## PostgreSQL

Типы данных
* Целые числа:
  * smallint (2 bytes) : 2^16 (от -2^15 до 2^15 - 1)
  * smallresial (2 bytes) : 2^16 (от 0 до 2^16)
  * integer (4 bytes) : 2^32 (от -2^31 до 2^31 - 1)
  * serial (4 bytes) : 2^16 (от 0 до 2^32)
  * bigint (8 bytes) : 2^64 (от -2^63 до 2^63 - 1)
  * bigserial (8 bytes) : 2^16 (от 0 до 2^64)
* Real numbers
  * decimal/numeric (variable, зависит от нужной нам точности. Часто задает денежные значения)
  * real/float4 (byte 4) : 6 знаков после запятой
  * double precision/float8/float (byte 8) : 15 знаков после запятой
* Characters
  * char (variable) - заполняет недостающие символы пробелами
  * varchar (variable) - подстраивается под более маленькие строки, создавая новый char
  * text (variable) - подстраивается под текст лубой длины
* Logical
  * Boolean/bool (1 byte) : True/False
* Temporal
  * date (4 bytes) : 4713 B.C. - 294276 AD
  * time (8 bytes) : 00:00:00 - 24:00:00
  * timestamp (8 bytes) хранит и время и дату в тех же диапозонах
  * interval (16 bytes) разница между двумя timestamp-ами, представляется в секундах
  * timestamptz (8 bytes) хранит timestamp и timezone

Это соновные типы, которые поддерживает PostgreSQL, однако их намного больше, а также PostgreSQL предоставляет возможность создавать свои типы.

Основные операторы и фильтрация (DQL)
* DISTINCT — убирает дубликаты из результата. Если указано несколько атрибутов, уникальной должна быть вся комбинация (строка).
* COUNT(атрибут) — количество значений в колонке (игнорирует NULL). COUNT(*) — общее количество кортежей.
* BETWEEN {A} AND {B} — фильтрация интервала (включая границы A и B).
* IN / NOT IN (список) — проверка на вхождение значения в перечень или подзапрос.
* MIN(), MAX(), AVG(), SUM() — агрегатные функции (минимум, максимум, среднее, сумма).
* LIKE '{шаблон}' — поиск по маске. % — любая последовательность символов (включая пустую), _ — ровно один любой символ.
* LIMIT {N} — ограничивает вывод первыми N кортежами.
* IS NULL / IS NOT NULL — единственно верный способ проверить поле на «пустоту» (NULL нельзя сравнивать через = или <>).

Группировка и сортировка
* GROUP BY (группировка) — разделяет результат на группы по уникальным значениям атрибута.
  * Правило SELECT: Если используется GROUP BY, то в SELECT могут быть только те атрибуты, что указаны в группировке, либо агрегатные функции (COUNT, SUM и т.д.).
  * Логика агрегатов: Агрегатные функции при наличии GROUP BY работают не со всей таблицей, а внутри каждой группы.
* HAVING — фильтрация после группировки (GROUP BY). В отличие от WHERE, позволяет использовать условия с агрегатными функциями (например, HAVING COUNT(*) > 5).
* ORDER BY {attr} ASC / DESC — сортировка по возрастанию / убыванию. Можно указать несколько атрибутов через запятую.

Теоретико-множественные операции (Set Operations)
* (объединение) UNION — объединение кортежей, убирает дубликаты. (A + B = {x | x in A or x in B})
* (объединение) UNION ALL — объединение кортежей, сохраняет дубликаты через сумму кратностей. (A +all B = {{x | m(A +all B) = mA(x) + mB(x)}})
* (пересечение) INTERSECT — выбирает только те кортежи, что есть в обоих отношениях, без дубликатов. (A intersect B = {x | x in A and x in B})
* (исключение) EXCEPT — кортежи, которые есть в первом отношении, но нет во втором. (A - B = {x | x in A and x not in B})
* (исключение) EXCEPT ALL — результат разности кратностей кортежей (сколько «лишних» копий в первом отношении относительно второго). (A -all B = {{x | m(A -all B) = max(0, mA(x) - mB(x))}})

```SQL

```

## Hibernate
__JPA (Java Persistence API)__ - это спецификация (набор правил) для управления реляционными базами данных в Java-приложениях.  
__ORM-framework__ позволяет работать с базой данных, используя объектно-ориентированный подход.  
__Hibernate__ - это популярная реализация JPA, предлагающая автоматическое создание схемы, кэширование, поддержку полиморфизма и наследования.
