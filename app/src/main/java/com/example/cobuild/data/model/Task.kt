package com.example.cobuild.data.model

//data class Task(
//    val id: String = "",
//    val title: String = "",
//    val description: String = "",
//    val assignedTo: String = "",
//    val assignedName: String = "",
//    val role: String = "",
//    val status: String = "TODO",
//    val startDate: Long = 0,
//    val deadline: Long = 0
//)

data class Task(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val assignedTo: String = "",
    val assignedName: String = "",
    val role: String = "",
    val status: String = "TODO",
    val progress: Int = 0,        // ← add this
    val startDate: Long = 0L,
    val deadline: Long = 0L
)