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

//package com.example.cobuild.data.model
//
//data class User(
//    val id: String           = "",
//    val name: String         = "",
//    val bio: String          = "",
//    val role: String         = "",       // e.g. "Frontend Developer"
//    val college: String      = "",
//    val skills: List<String> = emptyList(),
//    val github: String       = "",
//    val linkedin: String     = "",
//    val portfolio: String    = "",
//    val photoUrl: String     = ""
//)

package com.example.cobuild.data.model

data class User(
    val id: String               = "",
    val uid: String              = "",   // Firestore has "uid" field
    val name: String             = "",
    val email: String            = "",
    val role: String             = "",
    val location: String         = "",
    val goals: String            = "",
    val companyOrCollege: String = "",   // ← Firestore field is "companyOrCollege"
    val photo: String            = "",   // ← Firestore field is "photo" not "photoUrl"
    val github: String           = "",
    val linkedin: String         = "",
    val skills: List<String>     = emptyList(),   // array in Firestore
    val interests: List<String>  = emptyList(),   // array in Firestore
    val portfolio: List<String>  = emptyList()    // array in Firestore
)