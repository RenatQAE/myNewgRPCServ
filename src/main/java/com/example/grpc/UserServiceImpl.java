package com.example.grpc;

import io.grpc.stub.StreamObserver;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private final Map<Integer, UserResponse> userStorage = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger(1);

    @Override
    public void getUser(UserRequest request, StreamObserver<UserResponse> responseObserver) {
        int userId = request.getUserId();
        UserResponse user = userStorage.get(userId);
        if (user != null) {
            responseObserver.onNext(user);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(new RuntimeException("User not found with id: " + userId));
        }
    }

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
}