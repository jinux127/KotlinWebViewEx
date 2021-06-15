package com.example.kotlinwebviewex.ext

import java.sql.Date
import java.text.SimpleDateFormat

fun now() : String = SimpleDateFormat("yyyy-MM-dd HHmm").run { format(Date(System.currentTimeMillis())) } ?: run { "" }