package com.example.thesstransit.ui.utils

object SupabaseErrorMapper {

    fun map (error: Throwable):String {
        val message = error.message
            ?.lowercase()
            ?: ""

        return when {
            error is NoClassDefFoundError || error is ClassNotFoundException ->
                "Σφάλμα συμβατότητας (Serialization). Δοκίμασε να καθαρίσεις το project."

            "already registered" in message ->
                "Υπάρχει ήδη λογαριασμός με αυτό το email"

            "invalid email" in message ->
                "Το email δεν είναι έγκυρο"

            "password" in message &&
                    "weak" in message ->
                "Ο κωδικός είναι πολύ αδύναμος"

            "network" in message ->
                "Πρόβλημα σύνδεσης. Έλεγξε το Internet"

            else ->
                "Παρουσιάστηκε σφάλμα. Δοκίμασε ξανά"
        }
    }

}