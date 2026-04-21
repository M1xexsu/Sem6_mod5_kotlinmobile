package github.m1xexsu.sem6_mod5_kotlinmobile.domain.model

data class TodoItem(
    val id: Int,
    val title: String,
    val description: String,
    var isCompleted: Boolean
)
