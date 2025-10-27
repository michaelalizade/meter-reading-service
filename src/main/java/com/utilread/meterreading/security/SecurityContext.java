package com.utilread.meterreading.security;

public class SecurityContext {

    private static final ThreadLocal<UserPrincipal> currentUser = new ThreadLocal<>();

    public static void setCurrentUser(UserPrincipal user){
        currentUser.set(user);
    }

    public static UserPrincipal getCurrentUser(){
        return currentUser.get();
    }

    public static void clear(){
        currentUser.remove();
    }

    public static boolean isAuthenticated(){
        return currentUser.get() != null;
    }

}
