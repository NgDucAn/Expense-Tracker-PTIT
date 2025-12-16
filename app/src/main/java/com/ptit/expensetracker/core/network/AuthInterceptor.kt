package com.ptit.expensetracker.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * OkHttp interceptor that attaches Firebase ID token as Bearer token.
 * If user is not signed in or token retrieval fails, the request proceeds without Authorization header.
 */
class AuthInterceptor(
    private val auth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val user = auth.currentUser
        if (user == null) {
            return chain.proceed(original)
        }

        return try {
            val token = Tasks.await(user.getIdToken(true), 5, TimeUnit.SECONDS)?.token
            if (token.isNullOrBlank()) {
                chain.proceed(original)
            } else {
                val newReq = original.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newReq)
            }
        } catch (e: Exception) {
            chain.proceed(original)
        }
    }
}


