package com.example.thesstransit.ui.utils

import android.util.Patterns

object ValidationUtils {

    fun validateEmail(email: String):String? {
        return when {
            email.isBlank() ->
                "Το email είναι υποχρεωτικό πεδίο"

            !Patterns.EMAIL_ADDRESS
                .matcher(email)
                .matches() ->
                    "Μη έγκυρη διεύθυνση email."

            else -> null
        }
    }

    fun validatePassword(password: String):String? {
        return when {
            password.isBlank() ->
                "Ο κωδικός είναι υποχρεωτικός"

            password.length < 8 ->
                "Ο κωδικός πρέπει να έχει τουλάχιστον 8 χαρακτήρες"

            !password.any { it.isUpperCase() } ->
                "Χρειάζεται τουλάχιστον ένα κεφαλαίο γράμμα"

            !password.any { it.isLowerCase() } ->
                "Χρειάζεται τουλάχιστον ένα μικρό γράμμα"

            !password.any { it.isDigit() } ->
                "Χρειάζεται τουλάχιστον έναν αριθμό"

            else -> null
        }
    }
}