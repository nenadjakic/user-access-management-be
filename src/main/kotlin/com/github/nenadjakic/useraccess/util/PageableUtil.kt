package com.github.nenadjakic.useraccess.util

import org.springframework.data.domain.Sort

fun parseSortOrders(sortParams: List<String>): List<Sort.Order> {
    return sortParams.map { sortParam ->
        val parts = sortParam.split(",")
        if (parts.size == 2) {
            Sort.Order(Sort.Direction.fromString(parts[1]), parts[0])
        } else {
            Sort.Order(Sort.Direction.ASC, parts[0]) // Default ASC ako nije definisano
        }
    }
}