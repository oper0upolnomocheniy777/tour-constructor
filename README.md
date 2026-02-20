# Application

A simple Web Service for a Tour Agency

# Description

Система Турагентство
Заказчик выбирает и оплачивает Тур (отдых,экскурсия, шоппинг).
Турагент определяет тур как «горящий»,размеры скидок постоянным клиентам.

# Features

- Creating/editing tours
- Enabling/disabling tours
- Purchases
- Login/registration
- Two roles: user, tour agent
- Personal discounts

# Technologies

- Servlet
- JSP
- JDBC
- MySQL
- JQuery
- Maven, Bower

# Build & Run

## Requirements

- Maven
- Bower

Before building and running you should have bower installed

## Database

You can change login credentials in ```webapp/META-INF/context.xml```

If it doesn't work you should try modify ```resources/database.properties```
(just leaving this there in case I would use this file in the future and
would forget to update README.md)

## Tomcat

The repository ship with tomcat7 maven plugin, so you could run the 
application simply doing:
    ```
    mvn install
    mvn tomcat7:run
    ```
