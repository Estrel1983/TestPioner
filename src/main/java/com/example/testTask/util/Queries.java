package com.example.testTask.util;

public class Queries {
    //Phone
    public static final String GET_USER_BY_PHONE_QUERY = """
            SELECT p.user
            FROM PhoneData p
            WHERE p.phone = :phone
            """;
    public static final String GET_PHONE_BY_USER_QUERY = """
            SELECT e
            FROM PhoneData e
            WHERE e.user = :user
            """;

    //Email
    public static final String GET_USER_BY_EMAIL_QUERY = """
            SELECT e.user
            FROM EmailData e
            WHERE e.email = :email
            """;
    public static final String GET_EMAILS_BY_USER_QUERY = """
            SELECT e
            FROM EmailData e
            WHERE e.user = :user
            """;

    //Account
    public static final String GET_ALL_ACCOUNTS = """
            SELECT a
            FROM Account a
            """;
    public static final String GET_ACCOUNT_BY_USER_ID = """
            SELECT a
            FROM Account a
            WHERE a.user.id = :userId
            """;

    //User
    public static final String GET_USER_BY_ID_QUERY = """
            SELECT u
            FROM Users u
            WHERE u.id = :id
            """;
}
