Склонировать репозиторий на свой компьютер:
git clone https://github.com/RenatQAE/myNewgRPCServ

Установите сборщик maven "brew install maven"

Запустите gRPC сервер:
`mvn clean compile`
потом
`mvn exec:java -Dexec.mainClass="com.example.grpc.GrpcServer"`

4.Откройте Postman и создайте gRPC запрос
**метод UserService.GetUser**
указываешь message:
```
{
  "user_id": 1
}
```
**метод UserService.CreateUser**
```{
  "name": "Alice",
  "email": "alice@example.com"
}
```

5.Имплементируйте Protobuf

Нажмите Service definition => Import a proto.file => Choose a File => Выбрать user.proto => Import as API => Create a New API**
6.В адресную строку вводите localhost:9090

7.Выбирайте метод, который хотите использовать в Select a method

Надеюсь вам понравится мой gRPC-тренажер :)
