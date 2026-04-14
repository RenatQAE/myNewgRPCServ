package com.example.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final Map<Integer, UserResponse> userStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    // Существующий метод CreateUser
    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        int newId = idCounter.getAndIncrement();
        UserResponse newUser = UserResponse.newBuilder()
                .setUserId(newId)
                .setName(request.getName())
                .setEmail(request.getEmail())
                .build();
        userStorage.put(newId, newUser);
        responseObserver.onNext(newUser);
        responseObserver.onCompleted();
    }

    // Существующий метод GetUser
    @Override
    public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        int userId = request.getUserId();
        UserResponse user = userStorage.get(userId);
        if (user != null) {
            responseObserver.onNext(user);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User not found with id: " + userId)
                    .asRuntimeException());
        }
    }

    // НОВЫЙ МЕТОД: UpdateUser
    @Override
    public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        int userId = request.getUserId();
        UserResponse existingUser = userStorage.get(userId);

        if (existingUser == null) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("User not found with id: " + userId)
                    .asRuntimeException());
            return;
        }

        // Создаём обновлённого пользователя (имя и email могут быть пустыми – тогда оставляем старые)
        String newName = request.getName().isEmpty() ? existingUser.getName() : request.getName();
        String newEmail = request.getEmail().isEmpty() ? existingUser.getEmail() : request.getEmail();

        UserResponse updatedUser = UserResponse.newBuilder()
                .setUserId(userId)
                .setName(newName)
                .setEmail(newEmail)
                .build();

        userStorage.put(userId, updatedUser);
        responseObserver.onNext(updatedUser);
        responseObserver.onCompleted();
    }

    // НОВЫЙ МЕТОД: DeleteUser
    @Override
    public void deleteUser(UserRequest request, StreamObserver<DeleteUserResponse> responseObserver) {
        int userId = request.getUserId();
        UserResponse removed = userStorage.remove(userId);

        DeleteUserResponse response;
        if (removed != null) {
            response = DeleteUserResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("User " + userId + " deleted successfully")
                    .build();
        } else {
            response = DeleteUserResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("User not found with id: " + userId)
                    .build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    // НОВЫЙ МЕТОД: ListUsers (возвращает всех пользователей)
    @Override
    public void listUsers(EmptyRequest request, StreamObserver<ListUsersResponse> responseObserver) {
        List<UserResponse> usersList = new ArrayList<>(userStorage.values());
        ListUsersResponse response = ListUsersResponse.newBuilder()
                .addAllUsers(usersList)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}