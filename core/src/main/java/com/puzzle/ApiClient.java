package com.puzzle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.puzzle.dto.LevelDto;
import com.puzzle.dto.LoginRequest;
import com.puzzle.dto.RegistrationRequest;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private String currentUser = null;
    public interface ApiResponseListener<T> {
        void onSuccess(T result);
        void onError(Throwable e);
    }
    public void registerUser(String username, String password, ApiResponseListener<String> listener) {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername(username);
        request.setPassword(password);
        String jsonPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/auth/register")
            .header("Content-Type", "application/json")
            .content(jsonPayload)
            .build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final int statusCode = httpResponse.getStatus().getStatusCode();
                Gdx.app.log("ApiClient", "HTTP Status Code: " + statusCode);
                if (statusCode >= 200 && statusCode < 300) {
                    Gdx.app.postRunnable(() -> listener.onSuccess(httpResponse.getResultAsString()));
                } else {
                    String errorMessage = "Registration failed: " + httpResponse.getResultAsString();
                    Gdx.app.log("ApiClient", "Error Message: " + errorMessage);
                    Gdx.app.postRunnable(() -> listener.onError(new Exception(errorMessage)));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> listener.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> listener.onError(new Exception("Request cancelled")));
            }
        });
    }
    public void loginUser(String username, String password, ApiResponseListener<String> listener) {
        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);
        String jsonPayload = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        Gdx.app.log("ApiClient", "Request JSON: " + jsonPayload);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/auth/login")
            .header("Content-Type", "application/json")
            .content(jsonPayload)
            .build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode >= 200 && statusCode < 300) {
                    String response = httpResponse.getResultAsString();
                    currentUser = response;
                    Gdx.app.postRunnable(() -> listener.onSuccess(response));
                } else {
                    String errorMessage = "Login failed: " + httpResponse.getResultAsString();
                    Gdx.app.postRunnable(() -> listener.onError(new Exception(errorMessage)));
                }
            }
            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> listener.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> listener.onError(new Exception("Request cancelled")));
            }
        });
    }
    public void saveLevel(String username, int levelNumber, String levelData, ApiResponseListener<String> listener) {
        LevelDto levelDto = new LevelDto(levelNumber, levelData);
        String jsonPayload = new Json().toJson(levelDto);
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.POST)
            .url(BASE_URL + "/levels/save/" + username)
            .header("Content-Type", "application/json")
            .content(jsonPayload)
            .build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == 200) {
                    Gdx.app.postRunnable(() -> listener.onSuccess("Level saved successfully"));
                } else {
                    String errorMessage = "Failed to save level: " + httpResponse.getResultAsString();
                    Gdx.app.postRunnable(() -> listener.onError(new Exception(errorMessage)));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> listener.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> listener.onError(new Exception("Request cancelled")));
            }
        });
    }
    public void loadLevel(String username, int levelNumber, ApiResponseListener<String[][]> listener) {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
            .method(Net.HttpMethods.GET)
            .url(BASE_URL + "/levels/" + username + "/" + levelNumber)
            .build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final int statusCode = httpResponse.getStatus().getStatusCode();
                if (statusCode == 200) {
                    String response = httpResponse.getResultAsString();
                    try {
                        JsonValue jsonValue = new JsonReader().parse(response);
                        String levelData = jsonValue.getString("levelData");
                        String[][] levelGrid = new Json().fromJson(String[][].class, levelData);
                        Gdx.app.postRunnable(() -> listener.onSuccess(levelGrid));

                    } catch (Exception e) {
                        Gdx.app.postRunnable(() -> listener.onError(new Exception("Error parsing level data: " + e.getMessage(), e)));
                    }


                } else if (statusCode == 404) {
                    Gdx.app.postRunnable(() -> listener.onSuccess(null));
                } else {
                    String errorMessage = "Failed to load level: " + httpResponse.getResultAsString();
                    Gdx.app.postRunnable(() -> listener.onError(new Exception(errorMessage)));
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.postRunnable(() -> listener.onError(t));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> listener.onError(new Exception("Request cancelled")));
            }
        });
    }
    public String getCurrentUser() {
        return currentUser;
    }
}
