package com.example.fittrack.core.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    fun formatDate(date: LocalDate): String = date.format(dateFormatter)

    fun formatDateTime(dateTime: LocalDateTime): String = dateTime.format(dateTimeFormatter)

    fun parseDate(value: String): LocalDate = LocalDate.parse(value, dateFormatter)

    fun parseDateTime(value: String): LocalDateTime = LocalDateTime.parse(value, dateTimeFormatter)

    fun today(): String = formatDate(LocalDate.now())
}
