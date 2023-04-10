package com.krish.headsup.model

data class Token(
    val expires: String,
    val token: String
)

data class TokenStore(
    val access: Token,
    val refresh: Token
)

