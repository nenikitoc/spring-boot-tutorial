# Руководство по spring-boot
Краткое руководство по spring-boot. Включает в себя основные понятия и примеры использования.

## Содержание
- [Способы создания Bean-ов (Inversion of Controll)](#способы-создания-bean-ов)
- [Зависимости (Dependency Injection)](#зависимости)

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

  @Autowired
  public UserService(UserRepository userRepository){
    this.userRepository = userRepository;
  }
}
```

## Разработка

### Требования
Для установки и запуска проекта, необходим [NodeJS](https://nodejs.org/) v8+.

### Установка зависимостей
Для установки зависимостей, выполните команду:
```sh
$ npm i
```

### Запуск Development сервера
Чтобы запустить сервер для разработки, выполните команду:
```sh
npm start
```

### Создание билда
Чтобы выполнить production сборку, выполните команду: 
```sh
npm run build
```

## Тестирование
Какие инструменты тестирования использованы в проекте и как их запускать. Например:

Наш проект покрыт юнит-тестами Jest. Для их запуска выполните команду:
```sh
npm run test
```

## Deploy и CI/CD
Расскажите, как развернуть приложение. Как запустить пайплайны и т.д.

## Contributing
Как помочь в разработке проекта? Как отправить предложение или баг-репорт. Как отправить доработку (оформить pull request, какие стайлгайды используются). Можно вынести в отдельный файл — [Contributing.md](./CONTRIBUTING.md).

## FAQ 
Если потребители вашего кода часто задают одни и те же вопросы, добавьте ответы на них в этом разделе.

### Зачем вы разработали этот проект?
Чтобы был.

## To do
- [x] Добавить крутое README
- [ ] Всё переписать
- [ ] ...

## Команда проекта
Оставьте пользователям контакты и инструкции, как связаться с командой разработки.

- [Богдан Звягинцев](tg://resolve?domain=bzvyagintsev) — Front-End Engineer

## Источники
Если вы чем-то вдохновлялись, расскажите об этом: где брали идеи, какие туториалы смотрели, ссылки на исходники кода. 