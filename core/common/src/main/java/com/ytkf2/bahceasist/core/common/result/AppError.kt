package com.ytkf2.bahceasist.core.common.result

sealed interface AppError {
    // İnternet bağlantısı olmadığında
    data object NoInternet : AppError

    // Sunucu hata kodu döndüğünde (404, 500 vb.)
    data class Server(val code: Int) : AppError

    // Beklenmedik başka bir hata olduğunda
    data class Unknown(val cause: Throwable? = null) : AppError
}