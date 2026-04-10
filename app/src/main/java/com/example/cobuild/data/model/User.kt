//package com.example.cobuild.data.model
//
//
//data class User(
//    val userId: String = "",
//    val name: String = "",
//    val bio: String = "",
//    val skills: List<String> = emptyList(),
//    val role: String = "",
//    val profileImageUrl: String = ""
//)

package com.example.cobuild.data.model

data class User(
    val id: String           = "",
    val name: String         = "",
    val bio: String          = "",
    val role: String         = "",       // e.g. "Frontend Developer"
    val college: String      = "",
    val skills: List<String> = emptyList(),
    val github: String       = "",
    val linkedin: String     = "",
    val portfolio: String    = "",
    val photoUrl: String     = ""
)