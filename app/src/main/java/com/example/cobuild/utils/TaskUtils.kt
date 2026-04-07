package com.example.cobuild.utils

import com.example.cobuild.data.model.Task

fun taskProgress(task: Task): Float {

    val total = task.deadline - task.startDate
    val passed = System.currentTimeMillis() - task.startDate

    if (total <= 0) return 0f

    return (passed.toFloat() / total).coerceIn(0f,1f)
}