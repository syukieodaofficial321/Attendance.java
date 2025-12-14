package com.hcdc.attendance;

import javax.swing.JFrame;

/**
 * Authentication Presenter - handles login and sign up logic
 */
public class AuthPresenter {
    private SQLDatabase model;
    private LoginDialog loginDialog;
    private SignUpDialog signUpDialog;
    private User currentUser;

    public AuthPresenter(SQLDatabase model, JFrame parent) {
        this.model = model;
        this.loginDialog = new LoginDialog(parent);
        this.signUpDialog = new SignUpDialog(parent);
        attachListeners();
    }

    private void attachListeners() {
        // Login button listener
        loginDialog.setLoginListener(e -> handleLogin());

        // Sign up button from login dialog
        loginDialog.setSignUpListener(e -> {
            loginDialog.setVisible(false);
            signUpDialog.clearFields();
            signUpDialog.setVisible(true);
        });

        // Sign up button from sign up dialog
        signUpDialog.setSignUpListener(e -> handleSignUp());

        // Cancel button from sign up dialog
        signUpDialog.setCancelListener(e -> {
            signUpDialog.setVisible(false);
            loginDialog.clearFields();
            loginDialog.setVisible(true);
        });
    }

    private void handleLogin() {
        String username = loginDialog.getUsername();
        String password = loginDialog.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            loginDialog.showError("Please enter both username and password!");
            return;
        }

        User user = model.authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            loginDialog.setLoggedInUser(user);
            loginDialog.setLoginSuccessful(true);
            loginDialog.setVisible(false);
        } else {
            loginDialog.showError("Invalid username or password!");
            loginDialog.clearFields();
        }
    }

    private void handleSignUp() {
        if (!signUpDialog.validateFields()) {
            return;
        }

        String username = signUpDialog.getUsername();
        String email = signUpDialog.getEmail();
        String password = signUpDialog.getPassword();
        String fullName = signUpDialog.getFullName();

        // Check if user already exists
        if (model.userExists(username, email)) {
            signUpDialog.showError("Username or email already exists!");
            return;
        }

        // Create new user
        User newUser = new User(username, email, password, fullName);
        if (model.registerUser(newUser)) {
            signUpDialog.showSuccess("Account created successfully! Please login.");
            signUpDialog.setSignUpSuccessful(true);
            signUpDialog.setVisible(false);
            loginDialog.clearFields();
            loginDialog.setVisible(true);
        } else {
            signUpDialog.showError("Failed to create account. Please try again.");
        }
    }

    /**
     * Show login dialog and wait for authentication
     * @return true if login successful, false otherwise
     */
    public boolean showLogin() {
        loginDialog.setVisible(true);
        return loginDialog.isLoginSuccessful();
    }

    /**
     * Get the currently logged in user
     */
    public User getCurrentUser() {
        return currentUser != null ? currentUser : loginDialog.getLoggedInUser();
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}
