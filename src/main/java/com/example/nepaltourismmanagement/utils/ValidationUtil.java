package com.example.nepaltourismmanagement.utils;

import java.util.regex.Pattern;

public class ValidationUtil {

    // Email validation pattern
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    // Phone number validation pattern (simple format)
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^[0-9+()-]{8,15}$");

    /**
     * Validates if an email address is in correct format
     * @param email The email address to validate
     * @return True if the email is valid, false otherwise
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validates if a phone number is in correct format
     * @param phone The phone number to validate
     * @return True if the phone number is valid, false otherwise
     */
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Checks if a password is strong enough
     * A strong password has:
     * - At least 8 characters
     * - At least one letter
     * - At least one number
     * - At least one special character
     *
     * @param password The password to check
     * @return True if the password is strong, false otherwise
     */
    public boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            } else {
                hasSpecial = true;
            }
        }

        return hasLetter && hasNumber && hasSpecial;
    }

    /**
     * Validates a numeric input field
     * @param input The input string to validate
     * @return True if the input is a valid number, false otherwise
     */
    public boolean isValidNumber(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates an integer input field
     * @param input The input string to validate
     * @return True if the input is a valid integer, false otherwise
     */
    public boolean isValidInteger(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }

        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates that a string is not empty or null
     * @param input The string to validate
     * @return True if the string is not empty, false otherwise
     */
    public boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }
}