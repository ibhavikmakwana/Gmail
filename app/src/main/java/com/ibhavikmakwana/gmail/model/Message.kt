package com.ibhavikmakwana.gmail.model

data class Message(
        var id: Int = 0,
        var from: String? = null,
        var subject: String? = null,
        var message: String? = null,
        var timestamp: String? = null,
        var picture: String? = null,
        var isImportant: Boolean = false,
        var isRead: Boolean = false,
        var color: Int = -1
)