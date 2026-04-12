package com.example.cobuild.navigation

import android.net.Uri
object Destinations {

    const val LOGIN = "login"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val ADD_PROJECT = "add_project"

    /* ---------------- PROFILE ---------------- */

    // Logged-in user profile (already exists)
    const val PROFILE = "profile"

    // Viewing other user's profile (NEW)
    const val PROFILE_VIEW_ROUTE = "profile_view/{userId}"

    fun profileViewRoute(userId: String): String {
        return "profile_view/$userId"
    }

    /* ---------------- CHAT ---------------- */

    const val CHAT_LIST = "chat_list"
    const val CHAT = "chat/{chatId}"

    fun chatRoute(chatId: String): String {
        return "chat/$chatId"
    }

    /* ---------------- PROJECT ---------------- */

    const val PROJECT_LIST = "project_list"
    const val PROJECT_DETAIL = "project_detail"
    const val PROJECT_DETAIL_ROUTE = "project_detail/{projectId}"

    fun projectDetailRoute(projectId: String): String {
        return "project_detail/$projectId"
    }

    /* -------- HOME PROJECT DETAIL (discovery/join view) -------- */
    const val HOME_PROJECT_DETAIL_ROUTE = "home_project_detail/{projectId}"

    fun homeProjectDetailRoute(projectId: String): String {
        return "home_project_detail/$projectId"
    }

    const val PEOPLE_LIST = "people_list"

    const val GROUP_CHAT_ROUTE = "group_chat/{projectId}/{projectTitle}"
    fun groupChatRoute(projectId: String, title: String) = "group_chat/$projectId/${Uri.encode(title)}"


    /* -------- EDIT PROJECT (NEW) -------- */

    const val EDIT_PROJECT_ROUTE = "edit_project/{projectId}"

    fun editProjectRoute(projectId: String): String {
        return "edit_project/$projectId"
    }

    const val NOTIFICATIONS = "notifications"
}